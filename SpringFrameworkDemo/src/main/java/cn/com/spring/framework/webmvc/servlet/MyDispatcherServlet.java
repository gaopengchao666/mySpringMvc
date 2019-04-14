package cn.com.spring.framework.webmvc.servlet;

import cn.com.spring.framework.annotation.MyController;
import cn.com.spring.framework.annotation.MyRequestMapping;
import cn.com.spring.framework.context.MyApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * spring前端控制前
 * @author gaopengchao
 * 2019年3月25日
 */
@Slf4j
public class MyDispatcherServlet extends HttpServlet
{
    private static final long serialVersionUID = 2577115309246845312L;

    private final String LOCATION = "contextConfigLocation";

    //处理器映射器
    private List<MyHandlerMapping> handlerMappings = new ArrayList<MyHandlerMapping>();

    //处理器适配器
    private Map<MyHandlerMapping,MyHandlerAdapter> handlerAdapters = new HashMap<MyHandlerMapping, MyHandlerAdapter>();

    //视图解析器
    private List<MyViewResolver> viewResolvers = new ArrayList<MyViewResolver>();

    //顶层容器
    private MyApplicationContext context;
    
    @Override
    public void init(ServletConfig config)
    {
        //相当于把 IOC 容器初始化了
        context = new MyApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    /**
     * 初始化springmvc九大组件
     * @param context
     */
    private void initStrategies(MyApplicationContext context) {
        //有九种策略
        // 针对于每个用户请求，都会经过一些处理的策略之后，最终才能有结果输出
        // 每种策略可以自定义干预，但是最终的结果都是一致
        // ============= 这里说的就是传说中的九大组件 ================
        initMultipartResolver(context);//文件上传解析，如果请求类型是 multipart 将通过
        initMultipartResolver(context); //进行文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析
        //方法、实例、url关系
        initHandlerMappings(context);//通过 HandlerMapping，将请求映射到处理器
        // HandlerAdapters 用来动态匹配 Method 参数，包括类转换，动态赋值
        initHandlerAdapters(context);//通过 HandlerAdapter 进行多类型的参数动态匹配
        initHandlerExceptionResolvers(context);//如果执行过程中遇到异常，将交给HandlerExceptionResolver 来解析
        initRequestToViewNameTranslator(context);//直接解析请求到视图名
        //通过 ViewResolvers 实现动态模板的解析
        //自己解析一套模板语言
        initViewResolvers(context);//通过 viewResolver 解析逻辑视图到具体视图实现
        initFlashMapManager(context);//flash 映射管理器
    }

    private void initViewResolvers(MyApplicationContext context) {
        //拿到模板的存放目录
        String templateRoot = context.getReader().getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i ++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
            //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new MyViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters(MyApplicationContext context) {
        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参
        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        for (MyHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new MyHandlerAdapter());
        }
    }

    //将 Controller 中配置的 RequestMapping 和 Method 进行一一对应
    private void initHandlerMappings(MyApplicationContext context) {
        Set<String> beanNames = context.beanDefinitionMap.keySet();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if(!clazz.isAnnotationPresent(MyController.class)){
                    continue;
                }
                String baseUrl = "";
                //获取Coroller的url配置
                if(clazz.isAnnotationPresent(MyRequestMapping.class)){
                    MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //获取Method的url配置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    //没有加RequestMapping注解的直接忽略
                    if(!method.isAnnotationPresent(MyRequestMapping.class)){ continue; }
                    //映射URL
                    MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                    //  /demo/query
                    //  (//demo//query)
                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new MyHandlerMapping(pattern,controller,method));
                    log.info("Mapped " + regex + "," + method);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initFlashMapManager(MyApplicationContext context) {}
    private void initRequestToViewNameTranslator(MyApplicationContext context) {}
    private void initHandlerExceptionResolvers(MyApplicationContext context) {}
    private void initThemeResolver(MyApplicationContext context) {}
    private void initLocaleResolver(MyApplicationContext context) {}
    private void initMultipartResolver(MyApplicationContext context) {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try{
            this.doDispatch(req,resp);
        }catch(Exception e){
            //如果匹配过程出现异常，将异常信息打印出去
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        MyHandlerMapping handler = getHandler(req);

        if(handler == null){
            //new ModelAndView("404")
            return;
        }
        //2、准备调用前的参数
        MyHandlerAdapter ha = getHandlerAdapter(handler);

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        MyModelAndView mv = ha.handle(req,resp,handler);
        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MyModelAndView mv) {
        //把给我的ModleAndView变成一个HTML、OuputStream、json、freemark、veolcity
        //ContextType
        if(null == mv){return;}
    }

    private MyHandlerAdapter getHandlerAdapter(MyHandlerMapping handler) {
        return null;
    }

    /**
     * 获取处理器映射器
     * @param req
     * @return
     */
    private MyHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (MyHandlerMapping handler : this.handlerMappings) {
            try{
                Matcher matcher = handler.getPattern().matcher(url);
                //如果没有匹配上继续下一个匹配
                if(!matcher.matches()){ continue; }

                return handler;
            }catch(Exception e){
                throw e;
            }
        }
        return null;
    }

}
