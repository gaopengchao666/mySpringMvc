package cn.com.spring.framework.core;

/**
 * @author gaopengchao
 * 2019年4月11日
 */
public interface MyBeanFactory
{
    public Object getBean(String beanName) throws Exception;
    
    public Object getBean(Class<?> beanClass) throws Exception;
}
