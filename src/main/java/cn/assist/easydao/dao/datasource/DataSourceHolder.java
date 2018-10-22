package cn.assist.easydao.dao.datasource;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 数据源 持有者 -- 测试版
 * 
 * @version 1.8.0
 * @author caixb
 *
 */
public class DataSourceHolder extends AbstractRoutingDataSource implements ApplicationContextAware{
	public static boolean dev = false; 		//是否开启开发模式
	
	private ApplicationContext context;
	
	private static JdbcTemplate jdbcTemplate = new JdbcTemplate();
	
	public static DataSourceHolder ds;
	
	/**
	 * 为了兼容历史版本
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		targetDataSources.put("default", dataSource);
		super.setDefaultTargetDataSource(dataSource);
		super.setTargetDataSources(targetDataSources);
	}
	
	/**
	 * 获取JdbcTemplate 
	 * 
	 * @param lookupKey
	 * @return
	 */
	public JdbcTemplate getJdbcTemplate(String lookupKey) {
		DataSource dataSource = determineTargetDataSource(lookupKey);
		jdbcTemplate.setDataSource(dataSource);
		return jdbcTemplate;
	}
	
	@PostConstruct
	public void afterProperties() {
		ds = this.context.getBean(this.getClass());
	}
	@Override
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}
	
	@Override
	protected Object determineCurrentLookupKey() {
		return null;
	}
	
	public static void setDev(boolean dev) {
		DataSourceHolder.dev = dev;
	}

}
