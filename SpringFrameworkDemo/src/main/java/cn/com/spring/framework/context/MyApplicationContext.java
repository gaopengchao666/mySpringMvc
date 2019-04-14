package cn.com.spring.framework.context;

import cn.com.spring.framework.annotation.MyAutowired;
import cn.com.spring.framework.annotation.MyController;
import cn.com.spring.framework.annotation.MyService;
import cn.com.spring.framework.beans.MyBeanWrapper;
import cn.com.spring.framework.beans.config.MyBeanDefinition;
import cn.com.spring.framework.beans.config.MyBeanPostProcessor;
import cn.com.spring.framework.beans.factory.support.MyBeanDefinitionReader;
import cn.com.spring.framework.beans.factory.support.MyDefaultListableBeanFactory;
import cn.com.spring.framework.core.MyBeanFactory;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaopengchao
 * 2019年4月11日
 */
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory
{
    private String [] configLocations;
    @Getter
    private MyBeanDefinitionReader reader;

    //用来保证注册式单例的容器
    private Map<String,Object> singletonBeanCacheMap = new HashMap<String, Object>();

    //用来存储所有的被代理过的对象
    private Map<String, MyBeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, MyBeanWrapper>();
    
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

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    public Object getBean(String beanName) throws Exception
    {
        MyBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = instantiateBean(beanName,beanDefinition);
        //生成通知事件
        MyBeanPostProcessor postProcessor = new MyBeanPostProcessor();
        //在实例初始化以前调用一次
        postProcessor.postProcessBeforeInitialization(instance,beanName);
        //封装实例
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);
        this.beanWrapperMap.put(beanName,beanWrapper);

        //在实例初始化以后调用一次
        postProcessor.postProcessAfterInitialization(instance,beanName);

        //添加依赖注入
        populateBean(beanName,instance);
        return this.beanWrapperMap.get(beanName).getWrappedInstance();

    }

    /**
     * 添加bean的依赖注入
     * @param beanName
     * @param instance
     */
    private void populateBean(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();
        if (! clazz.isAnnotationPresent(MyController.class) ||  ! clazz.isAnnotationPresent(MyService.class)){return;}
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields)
        {
            //如果字段上没有autowired注解
            if ( ! field.isAnnotationPresent(MyAutowired.class)){continue;}
            MyAutowired annotation = field.getAnnotation(MyAutowired.class);
            String name = annotation.value().trim();
            if ("".equals(name))
            {
                 name = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                //给字段赋值
                field.set(instance,this.beanWrapperMap.get(name).getWrappedInstance());

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(String beanName,MyBeanDefinition beanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = beanDefinition.getBeanClassName();

        //2、反射实例化，得到一个对象
        Object instance = null;
        try {
            if(this.singletonBeanCacheMap.containsKey(className)){
                instance = this.singletonBeanCacheMap.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                //通过类名或者bean名称都可拿到单例
                this.singletonBeanCacheMap.put(className,instance);
                this.singletonBeanCacheMap.put(beanDefinition.getFactoryBeanName(),instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
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
        super.beanDefinitionMap.forEach((key ,beanDefinition) -> {
            if (! beanDefinition.isLazyInit())
            {
                try {
                    getBean(beanDefinition.getFactoryBeanName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
