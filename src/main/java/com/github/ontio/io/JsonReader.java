package com.github.ontio.io;

import java.io.IOException;
import java.lang.reflect.Array;

import com.github.ontio.io.json.JArray;
import com.github.ontio.io.json.JObject;

public class JsonReader {
	private JObject json;
	
	public JsonReader(JObject json) {
		this.json = json;
	}

	public <T extends JsonSerializable> T readSerializable(Class<T> t) throws InstantiationException, IllegalAccessException, IOException {
		T obj = t.newInstance();
		obj.fromJson(this);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends JsonSerializable> T[] readSerializableArray(Class<T> t, int count, String field) throws InstantiationException, IllegalAccessException, IOException {
		JArray jsonArr = (JArray)json.get(field);
		T[] array = (T[])Array.newInstance(t, count);
		for (int i = 0; i < count; i++) {
			array[i] = t.newInstance();
			array[i].fromJson(new JsonReader(jsonArr.get(i)));
		}
		return array;
	}
	
	public JObject json() {
		return json;
	}
}
