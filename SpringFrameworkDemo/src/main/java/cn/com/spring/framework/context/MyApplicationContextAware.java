package cn.com.spring.framework.context;

/**
 * 通过解耦的方式获得ioc容器的设计 后面将通过一个监听器区扫描所有的类，只要实现了此接口
 * 将自动调用setApplicationContext()的方法，从而将IOC容器注入到目标类
 * @author gaopengchao
 * 2019年4月11日
 */
public interface MyApplicationContextAware
{
    void setApplicationContext(MyApplicationContext applicationContext);
}
