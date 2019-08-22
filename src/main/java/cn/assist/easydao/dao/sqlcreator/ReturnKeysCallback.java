package cn.assist.easydao.dao.sqlcreator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;

/**
 * 获取并返回insert数据的数据库自增唯一索引
 * 
 * @author xula
 *
 * @param <T>
 */
public class ReturnKeysCallback<T> implements PreparedStatementCallback<Integer> {

	private PreparedStatementSetter pss; //声明
	
	public ReturnKeysCallback(PreparedStatementSetter pss) {
		this.pss = pss;
	}

	@Override
	public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
		ResultSet rs = null;
		if (pss != null) {
			pss.setValues(ps);
		}
		ps.executeUpdate(); //这里真正插入数据
		rs = ps.getGeneratedKeys();
		if (rs.next()) {
			return rs.getInt(1);
		}
		return null;
	}
}