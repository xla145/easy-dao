package cn.assist.easydao.plugin.dialect;

import cn.assist.easydao.common.Conditions;

import java.util.List;

/**
 * @author xula
 * @date 2019/08/22 9:55
 **/
public abstract class Dialect {


    /**
     * 生成查询sql
     * @param entity
     * @return
     */
    public abstract String forTableBuilderSelect(Class entity);

    /**
     * 生成分页sql
     * @param pageNo
     * @param pageSize
     * @param sql
     * @return
     */
    public abstract String forPaginate(int pageNo, int pageSize, StringBuffer sql);


    /**
     * 生成插入sql
     * @param entity
     * @param conn
     * @param sql
     * @param paras
     */
    public abstract void forDbSave(Class entity, Conditions conn , StringBuffer sql, List<Object> paras);


    public abstract void forDbUpdate(Class entity, Conditions conn, StringBuilder sql, List<Object> paras);
}
