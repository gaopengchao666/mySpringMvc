package cn.com.spring.framework.beans.factory.support;

import cn.com.spring.framework.beans.config.MyBeanDefinition;
import cn.com.spring.framework.context.support.MyAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaopengchao
 * 2019年4月11日
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext
{
    public final Map<String,MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,MyBeanDefinition>();
}
