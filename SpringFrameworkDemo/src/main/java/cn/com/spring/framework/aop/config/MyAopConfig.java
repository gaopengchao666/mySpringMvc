package cn.com.spring.framework.aop.config;

import lombok.Data;

/**
 * aop配置
 * @author gaopengchao
 * 2019年4月18日
 */
@Data
public class MyAopConfig
{
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
