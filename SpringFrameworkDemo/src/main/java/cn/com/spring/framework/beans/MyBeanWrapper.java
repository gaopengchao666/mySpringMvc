package cn.com.spring.framework.beans;

import lombok.Data;

/**
 * @author gaopengchao
 * 2019年4月11日
 */
@Data
public class MyBeanWrapper
{
    private Object wrappedInstance;
    private Class<?> wrappedClass;

    /**
     * @param instance
     */
    public MyBeanWrapper(Object instance)
    {
        this.wrappedInstance = instance;
        this.wrappedClass = instance.getClass();
    }
}
