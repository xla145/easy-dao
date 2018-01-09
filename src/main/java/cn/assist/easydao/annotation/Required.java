package cn.assist.easydao.annotation;  
import  java.lang.annotation.ElementType;  
import  java.lang.annotation.Retention;  
import  java.lang.annotation.RetentionPolicy;  
import  java.lang.annotation.Target;  
 
/**
 * 自定义注解 标注bean属性值使用时必须set值
 * 
 * 
 * @author caixb
 *
 */
@Retention (RetentionPolicy.RUNTIME)   
@Target (ElementType.FIELD)   
public @interface Required {  
    int maxLength() default 0;
    int minLength() default 0;
    String propertyName();
    
}  