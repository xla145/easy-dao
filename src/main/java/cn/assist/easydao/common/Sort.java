package cn.assist.easydao.common;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.assist.easydao.exception.DaoException;
import cn.assist.easydao.util.Inflector;

/**
 * 定义 结果排序
 * 
 * @author caixb
 *
 */
public class Sort {

	private String sortSql;
	private static final List<String> sortTypes = Arrays.asList(new String[] { "DESC", "ASC" });
	/**
	 * 
	 * @param field 对象属性名
	 * @param expr 表达式
	 */
	public Sort(String field, SqlSort sqlSort){
		String expr = sqlSort.toString();
		field = field.trim();
	    if (StringUtils.isBlank(field)) {
	      throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  Invalid field : ").append(field).toString());
	    }
		String original = Inflector.getInstance().underscore(field);
		StringBuffer sqlBuffer = new StringBuffer(original);
		
		if (!(sortTypes.contains(expr.toUpperCase()))) {
	      throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  invalid sort expr : ").append(expr).toString());
	    }
		sqlBuffer.append(" " + expr);
		
		sortSql = sqlBuffer.toString();
	}
	
	public void add(Sort sort){
		this.sortSql = this.sortSql + "," + sort.getSortSql();
	}
	
	
	public String getSortSql() {
		return sortSql;
	}
}
