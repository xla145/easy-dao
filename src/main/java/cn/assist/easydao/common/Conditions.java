package cn.assist.easydao.common;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.assist.easydao.exception.DaoException;
import cn.assist.easydao.util.Inflector;

/**
 * 定义sql 条件 标点
 * 
 * @author caixb
 *
 */
public class Conditions implements Serializable {

	private String connSql = "";

	private List<Object> connParams = new ArrayList<Object>();
	private static final List<String> joinTypes = Arrays.asList(new String[] { "AND", "OR" });

	private static final String POINT = ".";

	public Conditions() {}

	/**
	 * 
	 * @param field
	 *            对象属性名， 可为pojo对象属性名或者数据库字段名，即驼峰式属性名或者下划线字段名
	 * @param expr
	 *            表达式 SqlExpr
	 * @param value
	 *            当SqlExpr 为 in表达式的时候， val为可变参数， 可传入多个基本类型的值， 且支持传入基本类型数组， 会拆分为单项值
	 *            
	 */
	public Conditions(String field, SqlExpr sqlExpr, Object... value) throws DaoException {
		String expr = sqlExpr.toString();
		field = field.trim();
		if (StringUtils.isBlank(field)) {
			throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  Invalid field : ").append(field).toString());
		}

		String original = Inflector.getInstance().underscore(field);

		StringBuffer sqlBuffer = new StringBuffer();

		/**
		 * 如果现在多表之间，有相同的字段名 如 a.status 返回 a.`status`
		 */
		if (original.contains(POINT)) {
			String[] o = original.split("\\.");
			sqlBuffer.append(o[0]+POINT+"`" + o[1] + "`");
		} else {
			sqlBuffer.append("`" + original + "`");
		}
		switch (sqlExpr) {
		case IS_NULL:
			sqlBuffer.append(" " + expr + " ");
			break;
		case IS_NOT_NULL:
			sqlBuffer.append(" " + expr + " ");
			break;
		case LEFT_LIKE:
			if (value == null || value[0] == null || value.length != 1) {
				throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  only suppot 1 params: ").append(expr).toString());
			}
			sqlBuffer.append(" " + expr + " %?");
			connParams.add(value[0]);
			break;
		case RIGHT_LIKE:
			if (value == null || value[0] == null || value.length != 1) {
				throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  only suppot 1 params: ").append(expr).toString());
			}
			sqlBuffer.append(" " + expr + " ?%");
			connParams.add(value[0]);
			break;
		case ALL_LIKE:
			if (value == null || value[0] == null || value.length != 1) {
				throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  only suppot 1 params: ").append(expr).toString());
			}
			sqlBuffer.append(" " + expr + " %?%");
			connParams.add(value[0]);
			break;
		case IN:
			if (value == null || value.length < 1) {
				throw new DaoException(new StringBuilder().append(getClass().getName()).append(" : at least 1 params: ").append(field).toString());
			}
			sqlBuffer.append(" " + expr + "(");
			for (int i = 0; i < value.length; i++) {
				Object val = value[i];
				if (val == null) {
					continue;
				}
				if (val.getClass().isArray()) {
					int len = Array.getLength(val);
					for (int x = 0; x < len; x++) {
						Object o = Array.get(val, x);
						if (o == null) {
							continue;
						}
						connParams.add(o);
						if (i > 0) {
							sqlBuffer.append(",");
						}
					}
				} else {
					connParams.add(val);
					if (i > 0) {
						sqlBuffer.append(",");
					}
				}
				sqlBuffer.append("?");
			}
			sqlBuffer.append(")");
			break;
		default:
			if (value == null || value[0] == null || value.length != 1) {
				throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  only suppot 1 params: ").append(expr).toString());
			}
			sqlBuffer.append(" " + expr + " ?");
			connParams.add(value[0]);
			break;
		}
		connSql = sqlBuffer.toString();
	}

	public void add(Conditions conn, SqlJoin sqlJoin) {
		String joint = sqlJoin.toString();
		if (StringUtils.isBlank(joint)) {
			throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  invalid joint : ").append(joint).toString());
		}
		if (!(joinTypes.contains(joint.toUpperCase()))) {
			throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  invalid joint : ").append(joint).toString());
		}
		/**
		 * 如果是无参构造函数，创建Conditions，connSql默认为空字符串
		 */
		if (StringUtils.isEmpty(this.connSql)) {
			this.connSql = String.format("%s", new Object[] { conn.getConnSql() });
		} else if (StringUtils.isBlank(conn.getConnSql())) {
			// this.connSql = this.connSql;
		} else {
			this.connSql = String.format("(%s %s %s)", new Object[] { this.connSql, joint, conn.getConnSql() });
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
