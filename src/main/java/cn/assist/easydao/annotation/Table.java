package cn.assist.easydao.annotation;  
import java.lang.annotation.Documented;
import  java.lang.annotation.ElementType;  
import  java.lang.annotation.Retention;  
import  java.lang.annotation.RetentionPolicy;  
import  java.lang.annotation.Target;  

import org.springframework.stereotype.Component;
 
/**
 * 标识bojo类对应数据库的表名
 * 
 * 
 * @author caixb
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component 
public @interface Table {  
    String name();
    
}  