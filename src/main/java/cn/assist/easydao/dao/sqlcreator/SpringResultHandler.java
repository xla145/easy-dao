package cn.assist.easydao.dao.sqlcreator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.springframework.jdbc.core.RowCallbackHandler;

import cn.assist.easydao.util.Inflector;

/**
 * 查询映射结果集到entity
 * 
 * @author caixb
 *
 * @param <T>
 */
public class SpringResultHandler<T> implements RowCallbackHandler {

	private Class<T> entityClass;
	private List<T> dataList;

	public SpringResultHandler(Class<T> entityClass) {
		this.entityClass = entityClass;
		this.dataList = new ArrayList<>();
	}

	@Override
	public void processRow(ResultSet rs) throws SQLException {
		T rsEntity = null;
		try {
			rsEntity = entityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		Set<String> fields = getFields(rsEntity.getClass());
		Inflector inf = Inflector.getInstance();
		
		try {
			for (String fieldName : fields) {
				Object columnValue = null;
				try {
					// 取结果集中的值
					columnValue = rs.getObject(inf.underscore(fieldName));
				} catch (Exception e) {
					continue;
				}
				ConvertUtils.register(new DateConverter(null), java.util.Date.class);
				//指定字段copy值
				BeanUtils.copyProperty(rsEntity, fieldName, columnValue);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		dataList.add(rsEntity);
	}

	/**
	 * 获取字段
	 * @param clazz
	 * @return
	 */
	private Set<String> getFields(Class<?> clazz){
		
		Set<String> set = new HashSet<String>();
		try {
			Class<?> entityClass = clazz.newInstance().getClass();
			while (entityClass != null && !entityClass.getName().toLowerCase().equals("cn.assist.easydao.pojo.basepojo")) {
				Field[] fields = entityClass.getDeclaredFields();
				if(fields != null){
					for (Field field : fields) {
						set.add(field.getName());
					}
				}
				// 得到父类,然后赋给自己
			    entityClass = entityClass.getSuperclass();
			}
			//单独处理
			if(entityClass.getName().toLowerCase().equals("cn.assist.easydao.pojo.basepojo")){
				Field[] fields = entityClass.getDeclaredFields();
				if(fields != null){
					for (Field field : fields) {
						set.add(field.getName());
					}
				}
			}
		} catch (Exception e) {}
		return set;
	}
	
	
	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

}
