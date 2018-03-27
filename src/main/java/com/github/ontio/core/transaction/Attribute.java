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

package com.github.ontio.core.transaction;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;


public class Attribute implements Serializable {

	public AttributeUsage usage;
	public byte[] data;
	public int size;

	@Override
	public void serialize(BinaryWriter writer) throws IOException {
        writer.writeByte(usage.value());
        if (usage == AttributeUsage.Script
        		|| usage == AttributeUsage.DescriptionUrl
        		|| usage == AttributeUsage.Description
        		|| usage == AttributeUsage.Nonce) {
            writer.writeVarBytes(data);
        } else {
            throw new IOException();
        }
	}

	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		usage = AttributeUsage.valueOf(reader.readByte());
        if (usage == AttributeUsage.Script
        		|| usage == AttributeUsage.DescriptionUrl
        		|| usage == AttributeUsage.Description
        		|| usage == AttributeUsage.Nonce) {
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
