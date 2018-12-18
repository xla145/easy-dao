package cn.assist.easydao.interceptor;


import cn.assist.easydao.annotation.DataSource;
import cn.assist.easydao.annotation.DataSourceConfig;
import cn.assist.easydao.dao.datasource.DataSourceContextHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import java.lang.reflect.Method;

/**
 * 数据源切换
 * @author xla
 */
public class DataSourceInterceptor implements MethodInterceptor {


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<?> className = methodInvocation.getThis().getClass();
        DataSourceConfig dataSourceConfig = className.getAnnotation(DataSourceConfig.class);
        Method method = methodInvocation.getMethod();
        if (dataSourceConfig == null || !method.isAnnotationPresent(DataSource.class)) {
            return methodInvocation.proceed();
        }
        String dataSourceName = dataSourceConfig.name();
        DataSource dataSource = method.getAnnotation(DataSource.class);
        if (StringUtils.isNotEmpty(dataSource.value())) {
            dataSourceName = dataSource.value();
        }
        DataSourceContextHolder.setDbType(dataSourceName);
        Object object =  methodInvocation.proceed();
        DataSourceContextHolder.clearDbType();
        return object;
    }
}
