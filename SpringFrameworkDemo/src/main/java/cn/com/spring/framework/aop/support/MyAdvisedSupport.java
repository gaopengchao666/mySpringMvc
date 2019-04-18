package cn.com.spring.framework.aop.support;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.spring.framework.aop.aspect.MyAfterReturningAdviceInterceptor;
import cn.com.spring.framework.aop.aspect.MyAfterThrowingAdviceInterceptor;
import cn.com.spring.framework.aop.aspect.MyMethodBeforeAdviceInterceptor;
import cn.com.spring.framework.aop.config.MyAopConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author gaopengchao
 * 2019年4月18日
 */
public class MyAdvisedSupport
{
    private Class<?> targetClass;
    
    @Getter
    @Setter
    private Object target;
    
    private Pattern pointCutClassPattern;
    
    private transient Map<Method,List<Object>> methodCache;
    
    private MyAopConfig config;
    
    public MyAdvisedSupport(MyAopConfig cfg)
    {
        this.config = cfg;
    }
    
    /**
     * Determine a list of {@link org.aopalliance.intercept.MethodInterceptor} objects
     * for the given method, based on this configuration.
     * @param method the proxied method
     * @param targetClass the target class
     * @return List of MethodInterceptors (may also include InterceptorAndDynamicMethodMatchers)
     * @throws SecurityException 
     * @throws Exception 
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception{
        List<Object> cached = methodCache.get(method);
        //如果缓存没命中
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m, cached);
        }
        return cached;
    }
    
    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }
    
    public Class<?> getTargetClass()
    {
        return targetClass;
    }
    
    private void parse() {
        String pointCut = config.getPointCut();
        //玩正则
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("(") - 2);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));

        try {

            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);

            Class<?> aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String,Method>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(),m);
            }

            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //执行器链
                    List<Object> advices = new LinkedList<Object>();
                    //把每一个方法包装成 MethodIterceptor
                    //before
                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        //创建一个Advivce
                        advices.add(new MyMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }
                    //after
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        //创建一个Advivce
                        advices.add(new MyAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }
                    //afterThrowing
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        //创建一个Advivce
                        MyAfterThrowingAdviceInterceptor throwingAdvice =
                        new MyAfterThrowingAdviceInterceptor(
                                aspectMethods.get(config.getAspectAfterThrow()),
                                aspectClass.newInstance());
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(m,advices);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
