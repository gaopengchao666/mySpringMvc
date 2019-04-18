package cn.com.spring.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.spring.framework.annotation.MyRequestParam;

/**
 * 处理器适配器
 */
public class MyHandlerAdapter {

    public boolean supports(Object handler){ return (handler instanceof MyHandlerMapping);}


    MyModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception{
        //把方法的形参列表和request的参数列表所在顺序进行一一对应
        Map<String,Integer> paramIndexMapping = new HashMap<String, Integer>();
        MyHandlerMapping handle = (MyHandlerMapping) handler;
        Method method = handle.getMethod();
        
        //请求参数列表
        Map<String, String[]> parameterMap = req.getParameterMap();
        //形参类型列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        //形参列表
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameterTypes.length; i++)
        {
            Class<?> parameterType = parameterTypes[i];
            //request response
            if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class)
            {
                paramIndexMapping.put(parameterType.getName(), i);
            }
            //@MyRequestParam
            else if (parameters[i].getAnnotation(MyRequestParam.class) != null)
            {
                paramIndexMapping.put(parameters[i].getAnnotation(MyRequestParam.class).value(), i);
            }
        }
        
        //实际参数列表
        Object [] params = new Object[parameters.length];
        parameterMap.forEach((key,value) ->  {
            if (paramIndexMapping.containsKey(key)) {
                int index = paramIndexMapping.get(key);
                String paramValue = Arrays.toString(value).replaceAll("\\[|\\]","").replaceAll("\\s",",");
                params[index] = caseStringValue(paramValue,parameterTypes[index]);
            }
        });
        
        //req
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName()))
        {
            params[paramIndexMapping.get(HttpServletRequest.class.getName())] = req;
        }
        //reps
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName()))
        {
            params[paramIndexMapping.get(HttpServletResponse.class.getName())] = resp;
        }
        
        //调用目标方法
        Object result = method.invoke(handle.getController(), params);
        if(result == null || result instanceof Void){ return null; }

        boolean isModelAndView = handle.getMethod().getReturnType() == MyModelAndView.class;
        if(isModelAndView){
            return (MyModelAndView) result;
        }
        return null;
    }
    
    private Object caseStringValue(String value, Class<?> paramsType) {
        if(String.class == paramsType){
            return value;
        }
        //如果是int
        if(Integer.class == paramsType){
            return Integer.valueOf(value);
        }
        else if(Double.class == paramsType){
            return Double.valueOf(value);
        }else {
            if(value != null){
                return value;
            }
            return null;
        }
        //如果还有double或者其他类型，继续加if
        //这时候，我们应该想到策略模式了
        //在这里暂时不实现，希望小伙伴自己来实现

    }
}
