package cn.com.spring.framework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

/**
 * 视图解析器
 */
public class MyViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    private File templateRootDir;
    
    public MyViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }
    
    /**
                *  根据返回值解析视图对象那个
     * @param viewName
     * @param locale
     * @return
     * @throws Exception
     */
    public MyView resolveViewName(String viewName, Locale locale) throws Exception{
        if(null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new MyView(templateFile);
    }
}
