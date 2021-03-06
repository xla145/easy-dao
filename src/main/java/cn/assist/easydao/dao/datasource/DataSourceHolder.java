package cn.assist.easydao.dao.datasource;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 数据源 持有者 -- 测试版
 * 
 * @version 1.8.0
 * @author caixb
 *
 */
public class DataSourceHolder extends AbstractRoutingDataSource implements ApplicationContextAware {

	/**
	 * 是否开启开发模式
	 */
	public static boolean dev = false;
	
	private ApplicationContext context;
	
	private static JdbcTemplate jdbcTemplate = new JdbcTemplate();

	private String currentLookupKey;
	
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
		setCurrentLookupKey(lookupKey);
		DataSource dataSource = determineTargetDataSource();
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
		/**
		 * 自己通过BaseDao use自定义数据源，优先级最高，然后再方法级的注解数据源，再类的注解数据源
		 */
		if (StringUtils.isNotEmpty(getCurrentLookupKey())) {
			return getCurrentLookupKey();
		}
		String dataSourceName = DataSourceContextHolder.getDbType();
		if (StringUtils.isNotEmpty(dataSourceName)) {
			return dataSourceName;
		}
		return null;
	}
	
	public static void setDev(boolean dev) {
		DataSourceHolder.dev = dev;
	}


	/**
	 * 设置当前的数据源
	 * @return
	 */
	public String getCurrentLookupKey() {
		return currentLookupKey;
	}

	public void setCurrentLookupKey(String currentLookupKey) {
		this.currentLookupKey = currentLookupKey;
	}
}
