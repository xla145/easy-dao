package cn.assist.easydao.dao.datasource;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;


/**
 * 数据源 持有者 -- 测试版
 * 
 * @version 1.0.0
 * @author caixb
 *
 */
public class DataSourceHolder{

	public static JdbcTemplate jdbcTemplate;
	public static boolean dev = false; 		//是否开启开发模式
	

	public static void setDev(boolean dev) {
		DataSourceHolder.dev = dev;
	}
	public final void setDataSource(DataSource dataSource) {
		if (this.jdbcTemplate == null || dataSource != this.jdbcTemplate.getDataSource()) {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
	}
	
//	 //线程本地环境
//    private static final ThreadLocal<String> dataSources = new ThreadLocal<String>();
//    
//    //设置数据源
//    public static void setDataSource(String customerType) {
//        dataSources.set(customerType);
//    }
//    
//    //获取数据源
//    public static String getDataSource() {
//        return (String) dataSources.get();
//    }
//    
//    //清除数据源
//    public static void clearDataSource() {
//        dataSources.remove();
//    }
}
