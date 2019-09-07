package cn.assist.easydao.plugin.dialect;

import cn.assist.easydao.common.Conditions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author xula
 * @date 2019/08/22 9:55
 **/
public abstract class Dialect {


    /**
     * 生成查询sql
     * @param tableName
     * @param fields
     * @return
     */
    public abstract String forTableBuilderSelect(String tableName,List<String> fields);

    /**
     * 生成分页sql
     * @param pageNo
     * @param pageSize
     * @param sql
     * @return
     */
    public abstract String forPaginate(int pageNo, int pageSize, StringBuffer sql);


    /**
     * 生成sql语句
     * @param tableName
     * @param entitys
     * @param validDatas
     * @param sql
     * @param paramList
     * @param <T>
     */
    public abstract <T> void forDbSave(String tableName,List<T> entitys, Map<String, Object> validDatas, StringBuffer sql, List<Object> paramList);


    /**
     * 生成sql语句
     * @param tableName
     * @param param
     * @param conn
     * @param sql
     * @param paras
     */
    public abstract void forDbUpdate(String tableName, Map<String, Object> param, Conditions conn, StringBuffer sql, List<Object> paras);


    /**
     * 给字段格式化，左右加上 /"
     * @return
     */
    public abstract String formatFields(String field);

}
