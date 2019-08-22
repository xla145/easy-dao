package cn.assist.easydao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标识字段为数据自增索引
 * 
 * 
 * @author xula
 *
 */
@Retention (RUNTIME)
@Target (FIELD)
public @interface Id {

    /**
     * 名称
     * @return name
     */
    String name() default "";
    
}  