

import cn.com.spring.framework.context.MyApplicationContext;

/**
 */
public class Test {

    public static void main(String[] args) {

        MyApplicationContext context = new MyApplicationContext("classpath:application.properties");
        try {
            Object object = context.getBean("userController");
            System.out.println(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
