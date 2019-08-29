package cn.assist.easydao.plugin.dialect;

import cn.assist.easydao.common.Conditions;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xula
 * @date 2019/08/22 10:05
 **/
public class PostgrepSqlDialect extends Dialect {

    /**
     * 左边双引号
     */
    private static final  String LEFT_MARKS = "\"";

    /**
     * 左边双引号
     */
    private static final  String RIGHT_MARKS = "\"";


    @Override
    public String forTableBuilderSelect(String tableName, List<String> fields) {
        StringBuffer sql = new StringBuffer("select ");
        sql.append(fields.stream().map(s -> formatFields(s)).collect(Collectors.joining(",")));
        sql.append(" from " + formatFields(tableName));
        return sql.toString();
    }

    @Override
    public String forPaginate(int pageNo, int pageSize, StringBuffer sql) {
        int offset = pageSize * (pageNo - 1);
        sql.append(" limit ").append(pageSize).append(" offset ").append(offset);
        return sql.toString();
    }


    @Override
    public void forDbUpdate(String tableName, Map<String, Object> param, Conditions conn, StringBuffer sql, List<Object> paras) {
        sql.append("update ").append(formatFields(tableName)).append(" set ");
        Iterator<String> iterator = param.keySet().iterator();
        int flag = 0;
        while (iterator.hasNext()) {
            String fieldName = iterator.next();
            if (flag > 0) {
                sql.append(", ");
            }
            sql.append(formatFields(fieldName) +" = ?");
            paras.add(param.get(fieldName));
            flag++;
        }
        if (StringUtils.isNotEmpty(conn.getConnSql())) {
            String conSql = conn.getConnSql().replace("`","\"");
            sql.append(" where ").append(conSql);
        }
        paras.addAll(conn.getConnParams());
    }

    @Override
    public void forDbSave(String tableName, Conditions conn, StringBuffer sql, List<Object> paras) {

    }

    /**
     * 给字段格式化，左右加上 /"
     * @return
     */
    @Override
    public String formatFields(String field) {
        return LEFT_MARKS + field + RIGHT_MARKS;
    }

}
