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

package com.github.ontio.core.asset;

import com.github.ontio.common.Address;
import com.github.ontio.crypto.Digest;
import com.github.ontio.io.*;
import com.github.ontio.io.json.JObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Transfers implements Serializable, JsonSerializable {
    public TokenTransfer[] params;

    public Transfers(TokenTransfer[] params){
        this.params = params;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        reader.readVarBytes();
        int len = (int)reader.readVarInt();
        for(int i = 0;i <len;i++){
            try {
                params[i] = reader.readSerializable(TokenTransfer.class);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes("Token.Common.Transfer".getBytes());
        writer.writeSerializableArray(params);
    }


    public Object json() {
        Map json = new HashMap<>();
        return json;
    }


//    @Override
//	public void fromJson(JsonReader reader) {
//		JObject json = reader.json();
////		code = Helper.hexToBytes(json.get("Code").asString());
////		parameter = Helper.hexToBytes(json.get("Parameter").asString());
//	}
}
