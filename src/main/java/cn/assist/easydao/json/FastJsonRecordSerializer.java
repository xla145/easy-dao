
package cn.assist.easydao.json;

import cn.assist.easydao.pojo.RecordPojo;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.lang.reflect.Type;

/**
 * FastJsonRecordSerializer 支持序列化 easy-dao RecordPojo 类型
 * @author xla
 */
public class FastJsonRecordSerializer implements ObjectSerializer {

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
		if (object != null) {
			RecordPojo record = (RecordPojo)object;
			serializer.write(record.getColumns());
		}
	}
}


