package cn.assist.easydao.plugin.dialect;

import cn.assist.easydao.common.Conditions;

import java.util.List;

/**
 * @author xula
 * @date 2019/08/22 10:05
 **/
public class MysqlDialect extends Dialect {

    @Override
    public String forTableBuilderSelect(Class entity) {
        return null;
    }

    @Override
    public String forPaginate(int pageNo, int pageSize, StringBuffer sql) {
        return null;
    }

    @Override
    public void forDbUpdate(Class entity, Conditions conn, StringBuilder sql, List paras) {

    }

    @Override
    public void forDbSave(Class entity, Conditions conn, StringBuffer sql, List paras) {

    }
}
