package cn.assist.easydao.dao.datasource;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import cn.assist.easydao.plugin.dialect.ActiveDb;
import cn.assist.easydao.plugin.dialect.Dialect;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 数据源 持有者 -- 测试版
 * 
 * @version 1.8.0
 * @author xula
 *
 */
public class DataSourceHolder extends AbstractRoutingDataSource implements ApplicationContextAware {

	/**
	 * 是否开启开发模式
	 */
	public static boolean dev = false;
	
	private ApplicationContext context;
	
	private static JdbcTemplate jdbcTemplate = new JdbcTemplate();

	private Map<Object, JdbcTemplate> jdbcTemplateMap = new HashMap<>();

	private Map<Object, Dialect> targetDialect;

	private String currentLookupKey;
	
	public static DataSourceHolder ds;

	public String defaultDataSourceName = "mysql";

	public void setTargetActiveDb(Map<Object, ActiveDb> targetActiveDb) {
		// 创建map存数据源信息
		Map<Object, Object> targetDataSources = new HashMap<>(targetActiveDb.size());
		targetDialect = new HashMap<>(targetActiveDb.size());
		targetActiveDb.entrySet().forEach(s -> {
			targetDataSources.put(s.getKey(),s.getValue().getDataSource());
			targetDialect.put(s.getKey(),s.getValue().getDialect());
			// 不同的数据源使用不同的JdbcTemplate对象
			jdbcTemplateMap.put(s.getKey(),new JdbcTemplate());
		});
		super.setTargetDataSources(targetDataSources);
	}


	/**
	 * 为了兼容历史版本
	 * @param dataSource 数据源
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
	 * @param lookupKey 数据源名称
	 * @return JdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate(String lookupKey) {
		setCurrentLookupKey(lookupKey);
		DataSource dataSource = determineTargetDataSource();
		JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(defaultDataSourceName);
		if (jdbcTemplateMap.get(lookupKey) != null) {
			jdbcTemplate = jdbcTemplateMap.get(lookupKey);
		}
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
		return defaultDataSourceName;
	}
	
	public static void setDev(boolean dev) {
		DataSourceHolder.dev = dev;
	}


	/**
	 * 设置当前的数据源
	 * @return 数据源名称
	 */
	public String getCurrentLookupKey() {
		return currentLookupKey;
	}

	public void setCurrentLookupKey(String currentLookupKey) {
		this.currentLookupKey = currentLookupKey;
	}


	/**
	 * 获取当前数据库的方言
	 * @param dataSourceName
	 * @return
	 */
	public Dialect getTargetDialect(String dataSourceName) {
		if (StringUtils.isEmpty(dataSourceName)) {
			dataSourceName = (String) determineCurrentLookupKey();
		}
		return targetDialect.get(dataSourceName);
	}

	public void setTargetDialect(Map<Object, Dialect> targetDialect) {
		this.targetDialect = targetDialect;
	}

	public String getDefaultDataSourceName() {
		return defaultDataSourceName;
	}

	public void setDefaultDataSourceName(String defaultDataSourceName) {
		this.defaultDataSourceName = defaultDataSourceName;
	}
}
