package cn.assist.easydao.plugin.dialect;

import cn.assist.easydao.common.Conditions;
import cn.assist.easydao.exception.DaoException;
import cn.assist.easydao.util.PojoHelper;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xula
 * @date 2019/08/22 10:05
 **/
public class MysqlDialect extends Dialect {

    /**
     * 左边`
     */
    private static final  String LEFT_MARKS = "`";

    /**
     * 右边`
     */
    private static final  String RIGHT_MARKS = "`";


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
        sql.append(" limit ").append(offset).append(", ").append(pageSize);
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
            sql.append(formatFields(fieldName) + " = ?");
            paras.add(param.get(fieldName));
            flag++;
        }
        if (StringUtils.isNotEmpty(conn.getConnSql())) {
            sql.append(" where " + conn.getConnSql());
        }
        paras.addAll(conn.getConnParams());
    }


    @Override
    public <T> void forDbSave(String tableName,List<T> entitys, Map<String, Object> validDatas, StringBuffer sql, List<Object> paramList) {

        sql.append("insert into ");
        StringBuffer insertFields = new StringBuffer();
        sql.append(tableName);
        StringBuffer insertValues = new StringBuffer();
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
                PojoHelper pojoHelper = new PojoHelper(extra);
                Map<String, Object> vds =  pojoHelper.validDataList();
                List<Object> extraParams = vds.entrySet().stream().map(Map.Entry::getValue).filter(s -> s != null).collect(Collectors.toList());
                if(extraParams.size() != validDatas.size()){
                    throw new DaoException(new StringBuilder().append(getClass().getName()).append(" :  list size is not consistent！").toString());
                }
                sql.append(",(" + insertValues + ")");
                paramList.addAll(extraParams);
            }
        }
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
