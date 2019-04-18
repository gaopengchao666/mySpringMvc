package cn.com.spring.framework.aop.aspect;

import java.lang.reflect.Method;

import cn.com.spring.framework.aop.intercept.MyMethodInterceptor;
import cn.com.spring.framework.aop.intercept.MyMethodInvocation;

/**
 * Created by Tom on 2019/4/15.
 */
public class MyAfterReturningAdviceInterceptor extends MyAbstractAspectAdvice implements MyAdvice,MyMethodInterceptor {

    private MyJoinPoint joinPoint;

    public MyAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MyMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
