package cn.com.spring.framework.webmvc.servlet;

import lombok.Data;

import java.util.Map;

/**
 * 视图抽象
 */
@Data
public class MyModelAndView {
    private String viewName;
    private Map<String,?> model;

    public MyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
