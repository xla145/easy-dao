/**
 * Copyright (c) 2011-2019, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.assist.easydao.json;

import cn.assist.easydao.pojo.RecordPojo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Json 转换 fastjson 实现.
 * @author xla
 */
public class FastJson extends Json {

	private static FastJson fastJson = new FastJson();

	static {
		SerializeConfig.getGlobalInstance().put(RecordPojo.class, new FastJsonRecordSerializer());
	}
	
	public static FastJson getJson() {
		return fastJson;
	}

	@Override
	public String toJson(Object object) {
		String dp = datePattern != null ? datePattern : getDefaultDatePattern();
		return JSON.toJSONStringWithDateFormat(object, dp, SerializerFeature.WriteDateUseDateFormat);
	}

	@Override
	public <T> T parse(String jsonString, Class<T> type) {
		return JSON.parseObject(jsonString, type);
	}
}


