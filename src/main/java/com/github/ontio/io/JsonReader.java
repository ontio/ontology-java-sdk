/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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

//	public <T extends JsonSerializable> T readSerializable(Class<T> t) throws InstantiationException, IllegalAccessException, IOException {
//		T obj = t.newInstance();
//		obj.fromJson(this);
//		return obj;
//	}
//
//	@SuppressWarnings("unchecked")
//	public <T extends JsonSerializable> T[] readSerializableArray(Class<T> t, int count, String field) throws InstantiationException, IllegalAccessException, IOException {
//		JArray jsonArr = (JArray)json.get(field);
//		T[] array = (T[])Array.newInstance(t, count);
//		for (int i = 0; i < count; i++) {
//			array[i] = t.newInstance();
//			array[i].fromJson(new JsonReader(jsonArr.get(i)));
//		}
//		return array;
//	}
	
	public JObject json() {
		return json;
	}
}
