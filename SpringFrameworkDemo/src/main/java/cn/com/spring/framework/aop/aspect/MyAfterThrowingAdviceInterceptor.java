package cn.com.spring.framework.aop.aspect;

import java.lang.reflect.Method;

import cn.com.spring.framework.aop.intercept.MyMethodInterceptor;
import cn.com.spring.framework.aop.intercept.MyMethodInvocation;

/**
 * Created by Tom on 2019/4/15.
 */
public class MyAfterThrowingAdviceInterceptor extends MyAbstractAspectAdvice implements MyAdvice,MyMethodInterceptor {

    private String throwingName;

    public String getThrowingName()
    {
        return throwingName;
    }

    public MyAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MyMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
