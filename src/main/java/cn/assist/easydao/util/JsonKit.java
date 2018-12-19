

package cn.assist.easydao.util;

import cn.assist.easydao.json.Json;

/**
 * JsonKit.
 * @author xla
 */
public class JsonKit {
	
	public static String toJson(Object object) {
		return Json.getJson().toJson(object);
	}
	
	public static <T> T parse(String jsonString, Class<T> type) {
		return Json.getJson().parse(jsonString, type);
	}

}

