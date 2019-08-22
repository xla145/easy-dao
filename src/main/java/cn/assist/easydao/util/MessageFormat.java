package cn.assist.easydao.util;

import org.apache.commons.lang.StringUtils;

/**
 * 格式化字符串
 * 
 * @author xula
 *
 */
public class MessageFormat {

	/**
	 * 替换字符串中的变量
	 * 
	 * @param str 源串
	 * @param regex 要被替换的内容
	 * @param arguments 目标值
	 * @return
	 */
	public static String format(String str, String regex, Object... arguments) {
		if(StringUtils.isBlank(str) || arguments == null || arguments.length < 1){
			return str;
		}
		try {
			for (int i = 0; i < arguments.length; i++) {
				str = str.replaceFirst(regex, arguments[i].toString());
			}
		} catch (Exception e) {
		}
		return str;
	}
}
