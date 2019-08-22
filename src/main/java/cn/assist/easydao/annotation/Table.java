package cn.assist.easydao.annotation;  
import java.lang.annotation.Documented;
import  java.lang.annotation.ElementType;  
import  java.lang.annotation.Retention;  
import  java.lang.annotation.RetentionPolicy;  
import  java.lang.annotation.Target;  

import org.springframework.stereotype.Component;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标识bojo类对应数据库的表名
 * 
 * 
 * @author xula
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Component 
public @interface Table {

    /**
     * 表名
     * @return name
     */
    String name() ;
}  