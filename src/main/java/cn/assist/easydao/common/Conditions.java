package cn.assist.easydao.common;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.assist.easydao.exception.DaoException;
import cn.assist.easydao.util.Inflector;

/**
 * 定义sql 条件
 * 
 * @author caixb
 *
 */
public class Conditions{

	private String connSql = "";
	private List<Object> connParams = new ArrayList<Object>();
	private static final List<String> joinTypes = Arrays.asList(new String[] { "AND", "OR" });
	
	public Conditions(){}
	
	/**
	 * 
	 * @param field 对象属性名
	 * @param expr 表达式
	 * @param value 值
	 */
	public Conditions(String field, SqlExpr sqlExpr, Object... value) throws DaoException{
		String expr = sqlExpr.toString();
		field = field.trim();
	    if (StringUtils.isBlank(field)) {
	      throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  Invalid field : ").append(field).toString());
	    }
	   
		String original = Inflector.getInstance().underscore(field);
		StringBuffer sqlBuffer = new StringBuffer("`" + original + "`");
		
		switch (sqlExpr) {
			case IS_NULL:
				sqlBuffer.append(" " + expr + " ");
				break;
			case IS_NOT_NULL:
				sqlBuffer.append(" " + expr + " ");
				break;
			case LEFT_LIKE:
				if (value == null || value[0] == null || value.length != 1){
					throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  only suppot 1 params: ").append(expr).toString());
				}
				sqlBuffer.append(" " + expr + " %?");
				connParams.add(value[0]);
				break;
			case RIGHT_LIKE:
				if (value == null || value[0] == null || value.length != 1){
					throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  only suppot 1 params: ").append(expr).toString());
				}
				sqlBuffer.append(" " + expr + " ?%");
				connParams.add(value[0]);
				break;
			case ALL_LIKE:
				if (value == null || value[0] == null || value.length != 1){
					throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  only suppot 1 params: ").append(expr).toString());
				}
				sqlBuffer.append(" " + expr + " %?%");
				connParams.add(value[0]);
				break;
			case IN:
				if(value == null || value.length < 1){
					throw new DaoException(new StringBuilder().append(getClass().getName()).append(" : at least 1 params: ").append(field).toString());
				}
				sqlBuffer.append(" " + expr + "(");
				for (int i = 0; i < value.length; i++) {
					if(value[i] == null){
						continue;
					}
					connParams.add(value[i]);
					if(i > 0){
						sqlBuffer.append(",");
					}
					sqlBuffer.append("?");
				}
				sqlBuffer.append(")");
				break;
			default:
				if (value == null || value[0] == null || value.length != 1){
					throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  only suppot 1 params: ").append(expr).toString());
				}
				sqlBuffer.append(" " + expr + " ?");
				connParams.add(value[0]);
				break;
		}
		
//		if(SqlExpr.IN.toString().equalsIgnoreCase(expr)){ 
//			if(value == null || value.length < 1){
//				throw new DaoException(new StringBuilder().append(getClass().getName()).append(" : at least 1 params: ").append(field).toString());
//			}
//			sqlBuffer.append(" " + expr + "(");
//			for (int i = 0; i < value.length; i++) {
//				if(value[i] == null){
//					continue;
//				}
//				connParams.add(value[i]);
//				if(i > 0){
//					sqlBuffer.append(",");
//				}
//				sqlBuffer.append("?");
//			}
//			sqlBuffer.append(")");
//		}else if(SqlExpr.IS_NULL.toString().equalsIgnoreCase(expr) || SqlExpr.IS_NOT_NULL.toString().equalsIgnoreCase(expr)){
//			sqlBuffer.append(" " + expr + " ");
//		}else{
//			if (value == null || value.length != 1){
//				throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  only suppot 1 params: ").append(expr).toString());
//			}
//			if(value[0] == null){
//				return ;
//			}
//			sqlBuffer.append(" " + expr + " ?");
//			connParams.add(value[0]);
//		}
		connSql = sqlBuffer.toString();
	}
	
	public void add(Conditions conn, SqlJoin sqlJoin){
		String joint = sqlJoin.toString();
		if(StringUtils.isBlank(joint)){
			throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  invalid joint : ").append(joint).toString());
		}
		if (!(joinTypes.contains(joint.toString().toUpperCase()))) {
	      throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  invalid joint : ").append(joint).toString());
	    }
		if(StringUtils.isBlank(this.connSql)){
			this.connSql = String.format("%s", new Object[] {conn.getConnSql()});
		}else if(StringUtils.isBlank(conn.getConnSql())){
			//this.connSql = this.connSql;
		}else{
			this.connSql = String.format("(%s %s %s)", new Object[] { this.connSql, joint, conn.getConnSql()});
		}
		
		this.connParams.addAll(conn.getConnParams());
	}

	public String getConnSql() {
		return connSql;
	}

	public List<Object> getConnParams() {
		return connParams;
	}
}
