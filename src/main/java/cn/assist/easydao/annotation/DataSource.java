package cn.assist.easydao.annotation;
 
import java.lang.annotation.*;

/**
 * 通过注解的方式切换数据源
 * @author caixb
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String name() default DataSource.master;
 
    public static String master = "dataSource1";
 
}