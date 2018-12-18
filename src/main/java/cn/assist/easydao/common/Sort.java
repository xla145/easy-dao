package cn.assist.easydao.common;

import cn.assist.easydao.exception.DaoException;
import cn.assist.easydao.util.Inflector;
import org.apache.commons.lang.StringUtils;

/**
 * 定义 结果排序
 * 
 * @author caixb
 *
 */
public class Sort {

	private String sortSql;
	/**
	 * 
	 * @param field 对象属性名
	 * @param sqlSort 表达式
	 */
	public Sort(String field, SqlSort sqlSort){
		if (sqlSort == null) {
			throw new DaoException(new StringBuilder().append(getClass().getName()).append(" sqlSort is null ").append(field).toString());
		}
		if (StringUtils.isEmpty(field)) {
			throw new DaoException(new StringBuilder().append(getClass().getName()).append(" field is empty ").append(field).toString());
		}
		String expr = sqlSort.toString();
		String original = Inflector.getInstance().underscore(field.trim());
		StringBuffer sqlBuffer = new StringBuffer(original);
		sqlBuffer.append(" " + expr);
		this.sortSql = sqlBuffer.toString();
	}
	
	public void add(Sort sort){
		StringBuffer sql = new StringBuffer(this.sortSql);
		sql.append("," + sort.getSortSql());
		this.sortSql = sql.toString();
	}
	
	
	public String getSortSql() {
		return sortSql;
	}
}
