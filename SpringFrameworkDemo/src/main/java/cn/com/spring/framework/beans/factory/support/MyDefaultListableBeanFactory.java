package cn.com.spring.framework.beans.factory.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.com.spring.framework.beans.config.MyBeanDefinition;
import cn.com.spring.framework.context.support.MyAbstractApplicationContext;

/**
 * @author gaopengchao
 * 2019年4月11日
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext
{
    protected final Map<String,MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,MyBeanDefinition>();
}
