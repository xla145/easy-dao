package cn.assist.easydao.util;

import cn.assist.easydao.common.Conditions;
import cn.assist.easydao.common.SqlExpr;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {


    /**
     * 返回一组占位符
     * @param size 占位符的个数
     * 需要5个占位符 返回 ?,?,?,?,?
     * @return
     */
    public static String getPlaceholderGroup(Integer size) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < size; i++) {
            s.append("?,");
        }
        return s.deleteCharAt(s.length() - 1).toString();
    }


    public static void main(String[] args) {


        List<Integer> list = new ArrayList<>();


        list.add(4444);

        list.add(4444333);


        Conditions conn = new Conditions("id", SqlExpr.IN,2,4);


        System.out.println(conn.getConnSql() +"  ===  "+ StringUtils.join(conn.getConnParams().toArray(),","));
//        System.out.println(getPlaceholderGroup(5));
    }
}
