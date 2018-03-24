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

package com.github.ontio.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.ontio.common.Helper;
import com.github.ontio.io.json.JObject;
import com.github.ontio.io.json.JString;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.JsonReader;
import com.github.ontio.io.JsonSerializable;
import com.github.ontio.io.Serializable;

/**
 *  交易属性
 */
public class TransactionAttribute implements Serializable, JsonSerializable {
	/**
	 * 用途
	 */
	public TransactionAttributeUsage usage;
	/**
	 * 描述
	 */
	public byte[] data;
	public int size;
	
	/**
	 * byte格式数据反序列化
	 */
	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		// usage
        writer.writeByte(usage.value());
        // data
        if (usage == TransactionAttributeUsage.Script 
        		|| usage == TransactionAttributeUsage.DescriptionUrl
        		|| usage == TransactionAttributeUsage.Description
        		|| usage == TransactionAttributeUsage.Nonce) {
            writer.writeVarBytes(data);
        } else {
            throw new IOException();
        }
	}

	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		// usage
		usage = TransactionAttributeUsage.valueOf(reader.readByte());
		// data
        if (usage == TransactionAttributeUsage.Script
        		|| usage == TransactionAttributeUsage.DescriptionUrl
        		|| usage == TransactionAttributeUsage.Description
        		|| usage == TransactionAttributeUsage.Nonce) {
        			data = reader.readVarBytes(255);
        } else {
            throw new IOException();
        }
	}
	
	public Object json() {
        Map json = new HashMap<>();
        json.put("usage", usage.value());
        json.put("data", Helper.toHexString(data));
        return json;
	}
	
	@Override
	public String toString() {
		return "TransactionAttribute [usage=" + usage + ", data="
				+ Arrays.toString(data) + "]";
	}
	
//	@Override
//	public void fromJson(JsonReader reader) {
//		JObject json = reader.json();
//		usage = TransactionAttributeUsage.valueOf((byte)json.get("Usage").asNumber());
//		data = Helper.hexToBytes(json.get("Data").asString());
//	}
}
