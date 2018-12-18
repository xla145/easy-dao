package cn.assist.easydao.dao.sqlcreator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

import cn.assist.easydao.exception.DaoException;
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

	private static String BASE_PACKAGE_NAME = "cn.assist.easydao.pojo.basepojo";

	@Override
	public void processRow(ResultSet rs) {
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
			throw new DaoException("copy property error ");
		}
		dataList.add(rsEntity);
	}

	/**
	 * 获取字段
	 * @param clazz
	 * @return
	 */
	private Set<String> getFields(Class<?> clazz){
		
		Set<String> set = new HashSet<>();
		try {
			Class<?> entityClass = clazz.newInstance().getClass();
			if (entityClass == null || entityClass.getName().equalsIgnoreCase(BASE_PACKAGE_NAME)) {
				return set;
			}
			while (entityClass != null) {
				Set<String> nowSet = Arrays.stream(entityClass.getDeclaredFields()).filter(s -> s != null).map(Field::getName).collect(Collectors.toSet());
				set.addAll(nowSet);
				entityClass = entityClass.getSuperclass();
			}
		} catch (Exception e) {
			throw new DaoException("get instance fail",e);
		}
		return set;
	}
	
	
	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

}
