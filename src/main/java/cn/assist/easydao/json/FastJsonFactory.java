package cn.assist.easydao.json;

import cn.assist.easydao.pojo.RecordPojo;
import com.alibaba.fastjson.serializer.SerializeConfig;


/**
 * IJsonFactory 的 fastjson 实现.
 * @author xla
 */
public class FastJsonFactory implements IJsonFactory {
	
	private static FastJsonFactory me = new FastJsonFactory();
	
	public static FastJsonFactory me() {
		return me;
	}

	@Override
	public Json getJson() {
		return FastJson.getJson();
	}
	
	/**
	 * 移除 FastJsonRecordSerializer
	 */
	public void removeRecordSerializer() {
		SerializeConfig.getGlobalInstance().put(RecordPojo.class, null);
	}
}





