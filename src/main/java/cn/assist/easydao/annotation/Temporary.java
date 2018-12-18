package cn.assist.easydao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 使用该注解的成员属性，表示为临时字段，不会解析保存到数据库
 * 
 * @author caixb
 *
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Temporary {

}
