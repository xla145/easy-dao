package cn.assist.easydao.dao;

import cn.assist.easydao.annotation.Id;
import cn.assist.easydao.common.Conditions;
import cn.assist.easydao.common.Sort;
import cn.assist.easydao.common.SqlExpr;
import cn.assist.easydao.dao.datasource.DataSourceHolder;
import cn.assist.easydao.dao.mapper.ColumnRecordRowMapper;
import cn.assist.easydao.dao.sqlcreator.ReturnKeyCreator;
import cn.assist.easydao.dao.sqlcreator.ReturnKeysCallback;
import cn.assist.easydao.dao.sqlcreator.SpringResultHandler;
import cn.assist.easydao.exception.DaoException;
import cn.assist.easydao.pojo.BasePojo;
import cn.assist.easydao.pojo.PagePojo;
import cn.assist.easydao.pojo.RecordPojo;
import cn.assist.easydao.util.CommonUtil;
import cn.assist.easydao.util.Inflector;
import cn.assist.easydao.util.MessageFormat;
import cn.assist.easydao.util.PojoHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 封装常用数据库操作方法--测试版
 * <p>
 * 后续：
 * 1、代码结构优化
 * 2、优化支持多数据源
 * 3、事务支持（同一数据源事务、不同数据源事务）
 * 4、支持嵌入其他框架
 *
 * @author xula
 * @version 1.8.0
 */
public class BaseDao implements IBaseDao {

    private Log logger = LogFactory.getLog(BaseDao.class);

    private String dataSourceName;

    public static BaseDao dao = new BaseDao();

    /**
     * 使用数据源
     * @param dataSourceName 数据源名称
     * @return BaseDao
     */
    public static BaseDao use(String dataSourceName) {
        return new BaseDao(dataSourceName);
    }

    BaseDao() {
    }

    BaseDao(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public int update(String sql) {
        return this.executeUpdate(sql, null);
    }

    @Override
    public int update(String sql, Object... params) {
        return this.executeUpdate(sql, params);
    }

    @Override
    public <T extends BasePojo> int update(T entity) {
        return update(entity, null, null);
    }

    @Override
    public <T extends BasePojo> int update(T entity, String[] params) {
        if (params == null || params.length == 0) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  The params is not null ").toString());
        }
        return update(entity, null, params);
    }


    @Override
    public <T extends BasePojo> int update(T entity, Conditions conn) {
        return update(entity, conn, null);
    }


    @Override
    public <T extends BasePojo> int update(T entity, Conditions conn, String[] params) {
        PojoHelper pojoHelper = new PojoHelper(entity);

        String tableName = pojoHelper.getTableName(); //表名
        String pkName = pojoHelper.getPkName(Id.class); //主键名
        Object pkValue = pojoHelper.getPkValue(pkName); //主键值

        //待更新字段<===>数据
        Map<String, Object> validDatas;

        if (params != null && params.length > 0) {
            validDatas = Arrays.stream(params).collect(Collectors.toMap(p -> p, p -> pojoHelper.getMethodValue(p)));
        } else {
            validDatas = pojoHelper.validDataList();
        }
        // 主键不更新(如果指定了主键名)
        validDatas.remove(pkName);

        //更新条件
        if (conn == null || StringUtils.isBlank(conn.getConnSql())) {
            if (StringUtils.isBlank(pkName)) {
                throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  Do not specify a primary key constraint:Reference：Add the base ").append(Id.class.getClass()).toString());
            }
            conn = new Conditions(pkName, SqlExpr.EQUAL, pkValue);
        }

        return updateMulti(tableName, validDatas, conn);
    }

    @Override
    public <T extends BasePojo> int update(Class<T> entityClazz, Object uniqueValue, Map<String, Object> param) {
        /**
         * 主键名
         */
        String pkName = "";
        try {
            pkName = new PojoHelper(entityClazz.newInstance()).getPkName(Id.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return update(entityClazz, new Conditions(pkName, SqlExpr.EQUAL, uniqueValue),param);
    }

    @Override
    public <T extends BasePojo> int update(Class<T> entityClazz, Conditions conn, Map<String, Object> param) {
        if (param == null || param.entrySet().size() == 0) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  The param is not null ").toString());
        }
        // 条件不能为空
        if (conn == null || StringUtils.isNotBlank(conn.getConnSql()) || CollectionUtils.isEmpty(conn.getConnParams())) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  The conn is not null ").toString());
        }
        PojoHelper pojoHelper = null;
        // 表名
        String tableName = "";
        try {
            pojoHelper = new PojoHelper(entityClazz.newInstance());
            tableName = pojoHelper.getTableName(); //表名
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(tableName)) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  The table name is not null ").toString());
        }
        StringBuffer sql = new StringBuffer("update " + tableName + " set ");
        List<Object> paramList = new ArrayList<>();
        param.entrySet().stream().forEach(s -> {
            sql.append("`" + s.getKey() + "` = ?,");
            paramList.add(s.getValue());
        });
        // 去除多余的逗号
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" where " + conn.getConnSql());
        paramList.addAll(conn.getConnParams());
        return executeUpdate(sql.toString(), paramList.toArray());
    }

    @Override
    public int insert(String sql) {
        return executeInsert(sql, null, false);
    }

    @Override
    public int insert(String sql, Object... params) {
        return executeInsert(sql, params, false);
    }

    @Override
    public <T extends BasePojo> int insert(T entity) {
        List<T> entitys = new ArrayList<T>();
        entitys.add(entity);
        return insert(entitys);
    }

    @Override
    public <T extends BasePojo> int insert(List<T> entitys) {
        return insertMulti(entitys, false);
    }

    @Override
    public <T extends BasePojo> int merge(T entity, String... params) {
        StringBuffer sql = new StringBuffer("insert into ");
        StringBuffer insertFields = new StringBuffer();
        PojoHelper pojoHelper = new PojoHelper(entity);
        /**
         * 表名
         */
        sql.append(pojoHelper.getTableName());
        StringBuffer insertValues = new StringBuffer();
        //待插入字段<===>数据
        Map<String, Object> validDatas = pojoHelper.validDataList();
        //待插入参数
        List<Object> paramList = new ArrayList<Object>();
        validDatas.entrySet().stream().map(Map.Entry::getKey).forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                insertFields.append("`" + s + "` ,");
                insertValues.append("?,");
                paramList.add(validDatas.get(s));
            }
        });
        sql.append("(" + insertFields.deleteCharAt(insertFields.length() - 1) + ") ");
        sql.append("values(" + insertValues.deleteCharAt(insertValues.length() - 1) + ") ");
        sql.append("ON DUPLICATE KEY UPDATE ");
        for (String p : params) {
            String newParam = Inflector.getInstance().underscore(p);
            sql.append(newParam + " = ? ,");
            paramList.add(validDatas.get(newParam));
        }
        sql.deleteCharAt(sql.length() - 1);
        return executeInsert(sql.toString(), paramList.toArray(), false);
    }


    @Override
    public int insertReturnId(String sql) {
        return executeInsert(sql, null, true);
    }

    @Override
    public int insertReturnId(String sql, Object... params) {
        return executeInsert(sql, params, true);
    }

    @Override
    public <T extends BasePojo> int insertReturnId(T entity) {
        List<T> entitys = new ArrayList<T>();
        entitys.add(entity);
        return insertMulti(entitys, true);
    }

    @Override
    public <T extends BasePojo> int insertReturnId(List<T> entitys) {
        return insertMulti(entitys, true);
    }

    @Override
    public int queryForInt(String sql) {
        return queryForIntMulti(sql, null);
    }

    @Override
    public int queryForInt(String sql, Object... params) {
        return queryForIntMulti(sql, params);
    }

    private int queryForIntMulti(String sql, Object[] params) {
        if (DataSourceHolder.dev) {
            logger.info("sql:" + MessageFormat.format(sql, "\\?", params));
        }

        ReturnKeyCreator creator = new ReturnKeyCreator(sql);
        if (params == null || params.length < 1) {
            return getJdbcTemplate().queryForObject(creator.getSql(), Integer.class);
        }
        return getJdbcTemplate().queryForObject(creator.getSql(), Integer.class, params);
    }

    @Override
    public Map<String, Object> queryForMap(String sql) {
        List<Map<String, Object>> list = this.queryForListMapMulti(sql, null);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object... params) {
        List<Map<String, Object>> list = this.queryForListMapMulti(sql, params);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }


    @Override
    public List<Map<String, Object>> queryForListMap(String sql) {

        return queryForListMapMulti(sql, null);
    }

    @Override
    public List<Map<String, Object>> queryForListMap(String sql, Object... params) {
        if (params.length == 1 && params[0] instanceof List) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" : object do not support List type").toString());
        }
        return queryForListMapMulti(sql, params);
    }

    private List<Map<String, Object>> queryForListMapMulti(String sql, Object[] params) {
        if (DataSourceHolder.dev) {
            logger.info("sql:" + MessageFormat.format(sql, "\\?", params));
        }
        ReturnKeyCreator creator = new ReturnKeyCreator(sql);

        if (params == null || params.length < 1) {
            return getJdbcTemplate().queryForList(creator.getSql());
        }
        return getJdbcTemplate().queryForList(creator.getSql(), params);
    }


    @Override
    public <T extends BasePojo> T queryForEntity(Class<T> entityClazz, Object pkValue) {
        if (pkValue == null) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  The pkValue is not null ").toString());
        }
        PojoHelper pojoHelper = null;
        String pkName = null; //主键名
        try {
            pojoHelper = new PojoHelper(entityClazz.newInstance());
            pkName = pojoHelper.getPkName(Id.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Conditions conn = new Conditions(pkName, SqlExpr.EQUAL, pkValue);

        return queryForEntity(entityClazz, conn);
    }

    @Override
    public <T extends BasePojo> T queryForEntity(Class<T> entityClazz, Conditions conn) {

        StringBuffer sql = new StringBuffer(this.selectMulti(entityClazz));
        if (StringUtils.isNotBlank(conn.getConnSql())) {
            sql.append(" where " + conn.getConnSql());
        }

        return queryForEntity(entityClazz, sql.toString(), conn.getConnParams());
    }

    @Override
    public <T extends BasePojo> T queryForEntity(Class<T> entityClazz, String sql, List<Object> params) {
        return queryForEntity(entityClazz, sql, params.toArray());
    }

    @Override
    public <T extends BasePojo> T queryForEntity(Class<T> entityClazz, String sql, Object... params) {
        List<T> list = queryForListEntity(entityClazz, sql, params);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, Conditions conn) {

        StringBuffer sql = new StringBuffer(this.selectMulti(entityClazz));
        if (StringUtils.isNotBlank(conn.getConnSql())) {
            sql.append(" where " + conn.getConnSql());
        }

        return queryForListEntity(entityClazz, sql.toString(), conn.getConnParams());
    }

    @Override
    public <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, String sql, List<Object> params) {
        return queryForListEntity(entityClazz, sql, params.toArray());
    }

    @Override
    public <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, String sql, Object... params) {
        return queryForListEntity(entityClazz, sql, null, params);
    }

    @Override
    public <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, Conditions conn, Sort sort) {

        StringBuffer sql = new StringBuffer(this.selectMulti(entityClazz));
        if (StringUtils.isNotBlank(conn.getConnSql())) {
            sql.append(" where " + conn.getConnSql());
        }

        return queryForListEntity(entityClazz, sql.toString(), sort, conn.getConnParams());
    }

    @Override
    public <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, String sql, Sort sort, List<Object> params) {
        return queryForListEntity(entityClazz, sql, sort, params.toArray());
    }

    @Override
    public <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, String sql, Sort sort, Object... params) {
        if (sort != null) {
            sql += " order by " + sort.getSortSql();
        }
        if (DataSourceHolder.dev) {
            logger.info("sql:" + MessageFormat.format(sql, "\\?", params));
        }
        SpringResultHandler<T> srh = new SpringResultHandler<T>(entityClazz);

        ReturnKeyCreator creator = new ReturnKeyCreator(sql);

        if (params == null || params.length < 1) {
            getJdbcTemplate().query(creator.getSql(), srh);
        } else {
            getJdbcTemplate().query(creator.getSql(), srh, params);
        }
        return srh.getDataList();
    }

    @Override
    public <T extends BasePojo> PagePojo<T> queryForListPage(Class<T> entityClazz, Conditions conn, Sort sort, int pageNo, int pageSize) {
        if (conn == null) {
            conn = new Conditions();
        }
        StringBuffer sql = new StringBuffer(this.selectMulti(entityClazz));
        if (StringUtils.isNotBlank(conn.getConnSql())) {
            sql.append(" where " + conn.getConnSql());
        }
        return queryForListPage(entityClazz, sql.toString(), conn.getConnParams(), sort, pageNo, pageSize);
    }

    @Override
    public <T extends BasePojo> PagePojo<T> queryForListPage(Class<T> entityClazz, String sql, List<Object> params, Sort sort, int pageNo, int pageSize) {
        Object[] paramArr = null;
        if (params != null && params.size() > 0) {
            paramArr = params.toArray();
        }
        PagePojo<T> page = new PagePojo<T>();
        pageSize = pageSize < 1 ? 10 : pageSize;
        pageNo = pageNo < 2 ? 1 : pageNo;
        int total = queryForInt("select count(*) from (" + sql + ") as tab_temp", paramArr);

        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        page.setTotal(total);
        page.setPageTotal((total + pageSize - 1) / pageSize);

        if (sort != null) {
            sql += " order by " + sort.getSortSql();
        }
        sql += " limit " + ((pageNo - 1) * pageSize) + ", " + pageSize;

        if (DataSourceHolder.dev) {
            logger.info("sql:" + MessageFormat.format(sql, "\\?", paramArr));
        }
        SpringResultHandler<T> srh = new SpringResultHandler<T>(entityClazz);

        ReturnKeyCreator creator = new ReturnKeyCreator(sql.toString());

        if (params == null || params.size() < 1) {
            getJdbcTemplate().query(creator.getSql(), srh);
        } else {
            getJdbcTemplate().query(creator.getSql(), srh, paramArr);
        }
        page.setPageData(srh.getDataList());

        return page;
    }

    @Override
    public int delete(String sql, Object... params) {
        if (DataSourceHolder.dev) {
            logger.info("sql:" + sql);
        }
        return getJdbcTemplate().update(sql, params);
    }

    @Override
    public <T extends BasePojo> int delete(T entity) {
        PojoHelper pojoHelper = new PojoHelper(entity);
        String tableName = pojoHelper.getTableName(); //table name
        String pkName = pojoHelper.getPkName(Id.class); // primary key name
        Object pkValue = pojoHelper.getPkValue(pkName); // primary key
        if (pkValue == null) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" : the primary key is null").toString());
        }
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM "+ tableName + " WHERE "+ pkName +" = ? ");
        return delete(sql.toString(),pkValue);
    }

    /**
     * 解析查询sql
     *
     * @param entityClazz
     * @return
     */
    private <T extends BasePojo> String selectMulti(Class<T> entityClazz) {
        PojoHelper pojoHelper = null;
        String tableName = null; // 表名
        try {
            pojoHelper = new PojoHelper(entityClazz.newInstance());
            tableName = pojoHelper.getTableName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtils.isBlank(tableName)) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  The table name is not null ").toString());
        }

        List<String> fields = pojoHelper.validFieldList();
        for (int i = 0; i < fields.size(); i++) {
            fields.set(i, "`" + fields.get(i) + "`");
        }
        StringBuffer sql = new StringBuffer("select ");
        sql.append(StringUtils.join(fields, ","));
        sql.append(" from " + tableName);
        return sql.toString();
    }


    /**
     * 解析insert sql条件
     * <p>
     * <p>
     * 目前存在一个问题，如果批量插入，有几个数据某个字段为空，有几条数据是不为空的，会现在问题（这样）
     * <p>
     * 目前主要问题是 为空的字段不插入数据库
     *
     * @param entitys
     * @param isReturnId
     * @return
     */
    private <T extends BasePojo> int insertMulti(List<T> entitys, boolean isReturnId) {
        if(entitys == null || entitys.size() < 1){
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  The entitys name is not null ").toString());
        }
        T t = entitys.get(0);
        StringBuffer sql = new StringBuffer("insert into ");
        StringBuffer insertFields = new StringBuffer();

        PojoHelper pojoHelper = new PojoHelper(t);

        sql.append(pojoHelper.getTableName()); //表名
        StringBuffer insertValues = new StringBuffer();

        //待插入字段<===>数据
        Map<String, Object> validDatas =  pojoHelper.validDataList();

        //待插入参数
        List<Object> paramList = new ArrayList<Object>();

        Iterator<String> iterator = validDatas.keySet().iterator();
        int flag = 0;
        while (iterator.hasNext()) {
            String fieldName = iterator.next();
            if (flag > 0){
                insertFields.append(", ");
                insertValues.append(", ");
            }
            insertFields.append("`" + fieldName + "`");
            insertValues.append("?");
            paramList.add(validDatas.get(fieldName));
            flag++;
        }
        sql.append("(" + insertFields + ") ");
        sql.append("values(" + insertValues + ") ");

        if(entitys.size() > 1){
            for (int i = 1; i < entitys.size(); i++) {
                T extra = entitys.get(i);
                pojoHelper = new PojoHelper(extra);
                Map<String, Object> vds =  pojoHelper.validDataList();
                List<Object> extraParams = vds.entrySet().stream().map(Map.Entry::getValue).filter(s -> s != null).collect(Collectors.toList());
                if(extraParams.size() != validDatas.size()){
                    throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  list size is not consistent！").toString());
                }
                sql.append(",(" + insertValues + ")");
                paramList.addAll(extraParams);
            }
        }
        return executeInsert(sql.toString(), paramList.toArray(), isReturnId);
    }

    /**
     * 解析更新sql条件
     *
     * @param tableName
     * @param validDatas
     * @param conn
     * @return
     */
    private int updateMulti(String tableName, Map<String, Object> validDatas, Conditions conn) {
        if (conn == null) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  The Conditions is not null ").toString());
        }
        if (StringUtils.isBlank(tableName)) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  The table name is not null ").toString());
        }
        if (validDatas == null || validDatas.size() < 1) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  No update field ：").toString());
        }
        List<Object> paramList = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer("update " + tableName + " set ");

        Iterator<String> iterator = validDatas.keySet().iterator();
        int flag = 0;
        while (iterator.hasNext()) {
            String fieldName = iterator.next();
            if (flag > 0) {
                sql.append(", ");
            }
            sql.append("`" + fieldName + "` = ?");
            paramList.add(validDatas.get(fieldName));
            flag++;
        }
        if (StringUtils.isNotBlank(conn.getConnSql())) {
            sql.append(" where " + conn.getConnSql());
        }
        paramList.addAll(conn.getConnParams());

        return this.executeUpdate(sql.toString(), paramList.toArray());
    }

    /**
     * 执行更新
     *
     * @param sql
     * @param params
     * @return
     */
    private int executeUpdate(String sql, Object[] params) {

        if (sql.toUpperCase().indexOf("WHERE") < 1) {
            throw new DaoException(new StringBuilder().append(getClass().getName()).append(" : missing \"where\" keywords for sql: ").append(sql).toString());
        }

        if (DataSourceHolder.dev) {
            logger.info("sql:" + MessageFormat.format(sql, "\\?", params));
        }
        ReturnKeyCreator creator = new ReturnKeyCreator(sql);
        if (params == null || params.length < 1) {
            Integer i = getJdbcTemplate().update(sql);
            return i;
        }
        return getJdbcTemplate().update(creator.getSql(), params);
    }

    /**
     * 执行insert
     *
     * @param sql
     * @param params
     * @param isReturnId
     * @return
     */
    private int executeInsert(String sql, Object[] params, boolean isReturnId) {
        if (DataSourceHolder.dev) {
            logger.info("sql:" + MessageFormat.format(sql, "\\?", params));
        }
        if (isReturnId) {
            return executeInsertReturnId(sql, params);
        }

        ReturnKeyCreator creator = new ReturnKeyCreator(sql);
        if (params == null || params.length < 1) {
            return getJdbcTemplate().update(sql);
        }

        return getJdbcTemplate().update(creator.getSql(), params);
    }

    /**
     * 执行insert 并返回数据库唯一自增索引
     *
     * @param sql
     * @param params
     * @return
     */
    private int executeInsertReturnId(String sql, Object[] params) {
        ReturnKeyCreator creator = new ReturnKeyCreator(sql);
        PreparedStatementSetter pss = null;
        if (params != null && params.length > 0) {
            pss = new ArgumentPreparedStatementSetter(params);
        }
        Integer id = getJdbcTemplate().execute(creator, new ReturnKeysCallback<Integer>(pss));
        if (id == null) {
            return -1;
        }
        return id;
    }


    /**
     *
     * @param sql sql语句
     * @return 返回RecordPojo对象
     */
    @Override
    public RecordPojo query(String sql) {
        return query(sql,null);
    }

    /**
     *
     * @param sql sql语句
     * @param params 参数
     * @return 返回RecordPojo对象
     */
    @Override
    public RecordPojo query(String sql, Object... params) {
        if (DataSourceHolder.dev) {
            logger.info("sql:" + MessageFormat.format(sql, "\\?", params));
        }
        if (CommonUtil.isEmpty(params)) {
            return  getJdbcTemplate().queryForObject(sql,new ColumnRecordRowMapper());
        }
        return getJdbcTemplate().queryForObject(sql,params,new ColumnRecordRowMapper());
    }


    /**
     * 获取RecordPojo 列表
     * @param sql sql语句
     * @return 返回RecordPojo列表
     */
    @Override
    public List<RecordPojo> queryList(String sql) {
        return queryList(sql,null);
    }

    /**
     * 获取RecordPojo 列表
     * @param sql sql语句
     * @param params 参数
     * @return 返回RecordPojo列表
     */
    @Override
    public List<RecordPojo> queryList(String sql, Object... params) {
        if (DataSourceHolder.dev) {
            logger.info("sql:" + MessageFormat.format(sql, "\\?", params));
        }
        if (CommonUtil.isEmpty(params)) {
            return  getJdbcTemplate().query(sql,new ColumnRecordRowMapper());
        }
        return getJdbcTemplate().query(sql,params,new ColumnRecordRowMapper());
    }


    /**
     *
     * @param sql 执行的sql语句
     * @param params 参数
     * @param pageNo 页码
     * @param pageSize 每页显示的条数
     * @return 返回分页结果
     */
    @Override
    public PagePojo<RecordPojo> queryPage(String sql,List<Object> params,Integer pageNo,Integer pageSize) {
        Object[] paramArr = null;
        if (params != null && params.size() > 0) {
            paramArr = params.toArray();
        }
        PagePojo<RecordPojo> page = new PagePojo<RecordPojo>();
        pageSize = pageSize < 1 ? 10 : pageSize;
        pageNo = pageNo < 2 ? 1 : pageNo;
        int total = queryForInt("select count(*) from (" + sql + ") as tab_temp", paramArr);

        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        page.setTotal(total);
        page.setPageTotal((total + pageSize - 1) / pageSize);


        sql += " limit " + ((pageNo - 1) * pageSize) + ", " + pageSize;

        if (DataSourceHolder.dev) {
            logger.info("sql:" + MessageFormat.format(sql, "\\?", paramArr));
        }
        ReturnKeyCreator creator = new ReturnKeyCreator(sql);
        List<RecordPojo> list = new ArrayList<>();
        if (params == null || params.size() < 1) {
            list = getJdbcTemplate().query(creator.getSql(), new ColumnRecordRowMapper());
        } else {
            list = getJdbcTemplate().query(creator.getSql(), paramArr,new ColumnRecordRowMapper());
        }
        page.setPageData(list);
        return page;
    }

    /**
     *
     * @return 返回 JdbcTemplate对象
     */
    @Override
    public JdbcTemplate getJdbcTemplate() {
        return DataSourceHolder.ds.getJdbcTemplate(this.dataSourceName);
    }
}
