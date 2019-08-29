package cn.assist.easydao.util;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import cn.assist.easydao.annotation.Id;
import cn.assist.easydao.annotation.Table;
import cn.assist.easydao.annotation.Temporary;
import cn.assist.easydao.pojo.BasePojo;

/**
 * pojo 操作
 * 
 * @author xula
 *
 */
public class PojoHelper {
	
	/**
	 * 操作对像实例
	 */
	private Object obj;
	
	/**
	 * 操作对像所有可见get方法
	 * 
	 */
	private ThreadLocal<Hashtable<String, Method>> getMethodHash = new ThreadLocal<Hashtable<String,Method>>();
	
	/**
	 * 操作对像所有可见set方法
	 * 
	 */
	private ThreadLocal<Hashtable<String, Method>> setMethodHash = new ThreadLocal<Hashtable<String,Method>>();
	
	/**
	 * 操作对像所有可见字段属性
	 */
	private ThreadLocal<Hashtable<String, Field>> fieldHash = new ThreadLocal<Hashtable<String,Field>>();
	
	/**
	 * 转化器
	 * 
	 */
	private Inflector inflector;
	
	public PojoHelper(Object obj){
		this.obj = obj;
		inflector = Inflector.getInstance();
		initMethods(obj.getClass());
		initDeclaredFields(obj.getClass());
	}
	
	/**
	 * 获取主键Id名 
	 * 
	 * @param annClazz 字段名
	 * @return
	 */
	public <T extends Id> String getPkName(Class<T> annClazz) {
		String pkName = "id";
		if(annClazz != null){
			for (Field field : fieldHash.get().values()) {
				if(field == null){
					continue;
				}
				if(field.isAnnotationPresent(annClazz)){
					String fieldName = field.getName();
					Map<String, Object> map = getAnnotationParams(fieldName, annClazz);
					if(map == null || map.size() < 1){
						pkName = inflector.underscore(fieldName);
						break;
					}else{
						pkName = inflector.underscore((String)map.get("name"));
						break;
					}
				}
			}
		}
		return pkName;
	}
	
	
	/**
	 * 获取主键值
	 * 
	 * @return
	 */
	public Object getPkValue(String property) {
		if(StringUtils.isBlank(property)){
			return null;
		}
		return getMethodValue(property.replaceAll("_", ""));
	}
	
	/**
     * 根据entity 得到表名
     *
     * 规则：优先获取注解表名，如未设置，则以实体名作为表名（驼峰转为下划线）
	 *
	 * @return
	 */
    public <T extends BasePojo> String getTableName() {
    	Table table = obj.getClass().getAnnotation(Table.class);
    	if(table != null){
    		if(StringUtils.isNotBlank(table.name())){
    			return (table.name() );
    		}
    	}
    	
        String simpleName = obj.getClass().getSimpleName();
        return (inflector.underscore(simpleName));
        
    }
	
    /**
	 * 列出要插入到数据库的域集合
	 * 
	 * @return
	 */
	public Map<String, Object> validDataList() {
		try {
			Map<String, Object> props = new LinkedHashMap<>();
			for (Field field : fieldHash.get().values()) {
				if (!isAnnotationPresent(field.getName(), Temporary.class)) {
					Object val = getMethodValue(field.getName());
					if(val != null){
						if (val instanceof CharSequence && ((CharSequence) val).length() < 1){
							continue;
						}
						props.put(inflector.underscore(field.getName()), val);
					}
				}
			}
			return props;
		} catch (Exception e) {
			throw new RuntimeException("Exception when Fetching fields of "+ obj);
		}
	}
	
	/**
	 * 列出属性字段集合
	 * 
	 * @return
	 */
	public List<String> validFieldList() {
		try {
			List<String> fields = new ArrayList<String>();
			for (Field field : fieldHash.get().values()) {
				if (!isAnnotationPresent(field.getName(), Temporary.class)) {
					fields.add(inflector.underscore(field.getName()));
				}
			}
			return fields;
		} catch (Exception e) {
			throw new RuntimeException("Exception when Fetching fields of "+ obj);
		}
	}
	
	/**
	 * 调用get方法
	 * 
	 * @param property  方法名
	 * @return value 返回值
	 */
	public Object getMethodValue(String property) {
		Object value = null;
		Method m = getMethodHash.get().get(property.toLowerCase());
		if (m != null) {
			try {
				value = m.invoke(obj, new Object[] {});
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * 调用set方法
	 * 
	 * @param property 方法名
	 * @param value 属性值
	 * @return 是否设置成功
	 */
	public boolean setMethodValue(String property,Object value) {
		Method m = setMethodHash.get().get(property.toLowerCase());
		if (m != null) {
			try {
				m.invoke(obj, value);
				return true;
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}
	
	
	/**
	 * 判断属性是否有注解
	 * 
	 * @param fieldName 字段名
	 * @param annClazz 注解类
	 * @return
	 */
	public <T extends Annotation> boolean isAnnotationPresent(String fieldName, Class<T> annClazz) {
		Field field = fieldHash.get().get(fieldName);
		if(field == null || annClazz == null){
			return false;
		}
		return field.isAnnotationPresent(annClazz);
	}
	
	/**
	 * 获取有指定注解类的字段名
	 * 
	 * @param annClazz 字段名
	 * @return
	 */
	public <T extends Annotation> Set<String> getAnnotationfields(Class<T> annClazz) {
		Set<String> fieleSet = new HashSet<String>();
		if(annClazz == null){
			return fieleSet;
		}
		for (Field field : fieldHash.get().values()) {
			if(field == null){
				break;
			}
			if(field.isAnnotationPresent(annClazz)){
				fieleSet.add(field.getName());
			}
		}
		return fieleSet;
	}
	
	/**
	 * 获取注解参数
	 * @param <T>
	 * @param fieldName
	 * @param annClazz
	 * @return
	 */
	public <T extends Annotation> Map<String, Object> getAnnotationParams(String fieldName, Class<T> annClazz){
		Field field = fieldHash.get().get(fieldName);
		Annotation myAnnotation = field.getAnnotation(annClazz);
		Map<String, Object> map = new HashMap<String, Object>(); 
		for (Method method : myAnnotation.annotationType().getDeclaredMethods()) {
			Object val = null;
			try {
				val = method.invoke(myAnnotation);
			} catch (Exception e) {
				continue;
			}
			if(val instanceof Integer){
				map.put(method.getName(), (int)val);
			}
			if(val instanceof String){
				String v = (String)val;
				if(StringUtils.isNotBlank(v)){
					map.put(method.getName(), val);
				}
			}
		}
		return map;
    }
	
	private void initMethods(Class<?> clazz){
		if(!clazz.getCanonicalName().equals("java.lang.Object")){
			String rapl = "$1";
			
			String gs = "get(\\w+)";
			Pattern getM = Pattern.compile(gs);
			
			String ss = "set(\\w+)";
			Pattern setM = Pattern.compile(ss);
			
			Method[] methods = clazz.getMethods();
			for (int i = 0; i < methods.length; ++i) {
				Method m = methods[i];
				String methodName = m.getName();
				if (Pattern.matches(gs, methodName)) {
					String param = getM.matcher(methodName).replaceAll(rapl).toLowerCase();
					if(getMethodHash.get() == null){
						Hashtable<String, Method> me = new Hashtable<String, Method>();
						me.put(param, m);
						getMethodHash.set(me);
					}else if(!getMethodHash.get().containsKey(param)){
						getMethodHash.get().put(param, m);
					}
				}
				if (Pattern.matches(ss, methodName)) {
					String param = setM.matcher(methodName).replaceAll(rapl).toLowerCase();
					if(setMethodHash.get() == null){
						Hashtable<String, Method> me = new Hashtable<String, Method>();
						me.put(param, m);
						setMethodHash.set(me);
					}else if(!setMethodHash.get().containsKey(param)){
						setMethodHash.get().put(param, m);
					}
				}
			}
			initMethods(clazz.getSuperclass());
		}
	}
	
	private void initDeclaredFields(Class<?> clazz){
		if(!clazz.getCanonicalName().equals("java.lang.Object")){
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; ++i) {
				Field field = fields[i];
				String fileName = field.getName();
				if(fieldHash.get() == null){
					Hashtable<String, Field> fi = new Hashtable<String, Field>();
					fi.put(field.getName(), field);
					fieldHash.set(fi);
				}else if(!fieldHash.get().containsKey(fileName)){
					fieldHash.get().put(field.getName(), field);
				}
			}
			initDeclaredFields(clazz.getSuperclass());
		}
	}
}
