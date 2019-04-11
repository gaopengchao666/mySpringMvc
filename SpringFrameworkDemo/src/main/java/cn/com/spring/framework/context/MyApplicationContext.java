package cn.com.spring.framework.context;

import java.util.List;

import cn.com.spring.framework.beans.config.MyBeanDefinition;
import cn.com.spring.framework.beans.factory.support.MyBeanDefinitionReader;
import cn.com.spring.framework.beans.factory.support.MyDefaultListableBeanFactory;
import cn.com.spring.framework.core.MyBeanFactory;

/**
 * @author gaopengchao
 * 2019年4月11日
 */
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory
{
    private String [] configLocations;
    private MyBeanDefinitionReader reader;
    
    public MyApplicationContext(String... configLocations)
    {
        this.configLocations = configLocations;
        try
        {
            refresh();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Object getBean(String beanName)
    {
        return null;
    }

    /**
     * 刷新容器
     */
    @Override
    public void refresh() throws Exception
    {
        //1.定位，定位配置文件
        reader = new MyBeanDefinitionReader(this.configLocations);
        //2.加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<MyBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3.注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);
        //4.把不是延迟加载的类，提前初始化
        doAutowired();
    }

    /**
     * 把不是延迟加载的类，提前初始化
     */
    private void doAutowired()
    {
        
    }

    /**
     * 注册容器
     * @param beanDefinitions
     */
    private void doRegisterBeanDefinition(List<MyBeanDefinition> beanDefinitions) throws Exception
    {
        beanDefinitions.forEach(beanDefinition -> {
            if (! super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new RuntimeException("the " + beanDefinition.getFactoryBeanName() + " is existes!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        });
    }
    
    
}
