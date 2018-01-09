package cn.assist.easydao.dao.datasource;
//package cn.jugame.judao.dao.datasource;
//import java.util.Map;
//
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
//
///**
// * 继承AbstractRoutingDataSource 重写determineCurrentLookupKey方法动态改变数据源类
// * 
// * @author caixb
// *
// */
//public class DynamicDataSource extends AbstractRoutingDataSource {
//
//	@Override
//	protected Object determineCurrentLookupKey() {
//		return DataSourceHolder.getDataSource();
//	}
//	
//	@Override
//	public void setDataSourceLookup(DataSourceLookup dataSourceLookup) {
//		super.setDataSourceLookup(dataSourceLookup);
//	}
//	
//	@Override
//	public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
//		super.setDefaultTargetDataSource(defaultTargetDataSource);
//	}
//
//	@Override
//	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
//		super.setTargetDataSources(targetDataSources);
//	}
//}
