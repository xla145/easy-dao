package cn.assist.easydao.dao.datasource;

/**
 * 数据源名管理
 * @author xla
 */
public class DataSourceContextHolder {

    /**
     *  线程本地环境
     */
    private static ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * 设置数据源类型
     * @param dbType
     */
    public static void setDbType(String dbType) {
        contextHolder.set(dbType);
    }

    /**
     * 获取数据源类型
     * @return
     */
    public static String getDbType() {
        return (contextHolder.get());
    }

    /**
     * 清除数据源类型
     */
    public static void clearDbType() {
        contextHolder.remove();
    }
}