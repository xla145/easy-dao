package cn.assist.easydao.util;

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
        System.out.println(getPlaceholderGroup(5));
    }
}
