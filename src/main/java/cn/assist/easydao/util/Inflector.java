package cn.assist.easydao.util;

import com.google.common.base.CaseFormat;
import com.google.common.primitives.Chars;

/**
 * 驼峰和下划线 转化
 * @author xla
 */
public class Inflector {

    private static Inflector instance = new Inflector();

    public static Inflector getInstance() {
        return instance;
    }

    /**
     * 驼峰转下划线
     * @param camelCasedWord
     * @return
     */
    public String underscore(String camelCasedWord) {
        return  CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelCasedWord);
    }

    /**
     * 下划线转驼峰
     * @param camelCasedWord
     * @return
     */
    public String hump(String camelCasedWord) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, camelCasedWord);
    }


    /**
     * 下划线转驼峰
     * @param camelCasedWord
     * @return
     */
    public String humpFirstLower(String camelCasedWord) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, camelCasedWord);
    }


    public static void main(String[] args) {
    	System.out.println(Inflector.getInstance().humpFirstLower("member_info"));
	}
}

