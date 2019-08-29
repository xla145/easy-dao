package cn.assist.easydao.plugin.dialect;

import javax.sql.DataSource;

/**
 * db管理
 * @author xula
 * @date 2019/08/23 10:46
 **/
public class ActiveDb {

    /**
     * 数据源
     */
    private DataSource dataSource;

    /**
     * 数据库方言
     */
    private Dialect dialect;


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }
}
