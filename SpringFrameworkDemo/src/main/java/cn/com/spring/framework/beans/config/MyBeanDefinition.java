package cn.com.spring.framework.beans.config;

import lombok.Data;

/**
 * @author gaopengchao
 * 2019年4月11日
 */
@Data
public class MyBeanDefinition
{
    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;
}
