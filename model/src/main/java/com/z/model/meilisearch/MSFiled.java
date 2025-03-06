package com.z.model.meilisearch;


import java.lang.annotation.*;

/**
 * MeiliSearch 字段注解
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MSFiled {

    /**
     * 是否开启过滤
     */
    boolean openFilter() default false;

    /**
     * 是否不展示
     */
    boolean noDisplayed() default false;

    /**
     * 是否开启排序
     */
    boolean openSort() default false;


    /**
     *  处理的字段名
     */
    String key() ;

}

