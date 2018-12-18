package cn.assist.easydao.dao.sqlcreator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.util.Assert;

/**
 * 预编译sql
 * 
 * @author caixb
 *
 */
public class ReturnKeyCreator implements PreparedStatementCreator, SqlProvider {

	private String sql;

	public ReturnKeyCreator(String sql) {
		Assert.notNull(sql, "SQL must not be null");
		this.sql = sql;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		return con.prepareStatement(this.sql, PreparedStatement.RETURN_GENERATED_KEYS);
	}

	@Override
	public String getSql() {
		return this.sql;
	}
}