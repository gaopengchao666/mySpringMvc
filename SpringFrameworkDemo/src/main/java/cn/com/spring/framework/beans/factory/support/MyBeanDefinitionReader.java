package cn.com.spring.framework.beans.factory.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cn.com.spring.framework.beans.config.MyBeanDefinition;
import lombok.Getter;

/**
 * @author gaopengchao 2019年4月11日
 */
public class MyBeanDefinitionReader
{
    private List<String> registyBeanClasses = new ArrayList<String>();

    @Getter
    private Properties config = new Properties();

    /**
     * 构造函数 加载properties文件
     * 
     * @param configLocations
     */
    public MyBeanDefinitionReader(String... configLocations)
    {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace("classpath:", ""));
        try
        {
            config.load(resourceAsStream);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (resourceAsStream != null)
            {
                try
                {
                    resourceAsStream.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty("scanPackage"));
    }

    /**
     * 扫描所有的类文件
     * 
     * @param scanPackage
     */
    private void doScanner(String scanPackage)
    {
        // 转换为文件路径，实际上就是把.替换为/就OK了
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles())
        {
            if (file.isDirectory())
            {
                doScanner(scanPackage + "." + file.getName());
            }
            else
            {
                if (!file.getName().endsWith(".class"))
                {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registyBeanClasses.add(className);
            }
        }
    }

    // 把配置文件中扫描到的所有的配置信息转换为MyBeanDefinition对象，以便于之后IOC操作方便
    public List<MyBeanDefinition> loadBeanDefinitions()
    {
        List<MyBeanDefinition> result = new ArrayList<MyBeanDefinition>();
        try
        {
            for (String className : registyBeanClasses)
            {
                Class<?> beanClass = Class.forName(className);
                // 如果是一个接口，是不能实例化的
                // 用它实现类来实例化
                if (beanClass.isInterface())
                {
                    continue;
                }

                // beanName有三种情况:
                // 1、默认是类名首字母小写
                // 2、自定义名字
                // 3、接口注入
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces)
                {
                    // 如果是多个实现类，只能覆盖
                    // 为什么？因为Spring没那么智能，就是这么傻
                    // 这个时候，可以自定义名字
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    // 把每一个配信息解析成一个BeanDefinition
    private MyBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName)
    {
        MyBeanDefinition beanDefinition = new MyBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    // 如果类名本身是小写字母，确实会出问题
    // 但是我要说明的是：这个方法是我自己用，private的
    // 传值也是自己传，类也都遵循了驼峰命名法
    // 默认传入的值，存在首字母小写的情况，也不可能出现非字母的情况

    // 为了简化程序逻辑，就不做其他判断了，大家了解就OK
    // 其实用写注释的时间都能够把逻辑写完了
    private String toLowerFirstCase(String simpleName)
    {
        char[] chars = simpleName.toCharArray();
        // 之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        // 在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
