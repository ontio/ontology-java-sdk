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

import com.github.ontio.common.Helper;
import com.github.ontio.io.*;
import com.github.ontio.crypto.ECC;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Sig implements Serializable {
    public byte[][] pubKeys = null;
    public int M;
    public byte[][] sigData;

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    	int len = (int)reader.readVarInt();
        pubKeys = new byte[len][];
        for(int i=0;i<pubKeys.length;i++) {
            pubKeys[i] = reader.readVarBytes();
        }
        M = (int)reader.readVarInt();
        len = (int)reader.readVarInt();
        sigData = new byte[len][];
        for(int i=0;i<sigData.length;i++) {
            sigData[i] = reader.readVarBytes();
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    	writer.writeVarInt(pubKeys.length);
    	for(int i=0;i<pubKeys.length;i++) {
            writer.writeVarBytes(pubKeys[i]);
        }
        writer.writeVarInt(M);
        writer.writeVarInt(sigData.length);
        for (int i = 0; i < sigData.length; i++) {
            writer.writeVarBytes(sigData[i]);
        }
    }

    public Object json() {
        Map json = new HashMap<>();
        json.put("M", M);
        json.put("PubKeys", Arrays.stream(pubKeys).map(p->Helper.toHexString(p)).toArray(Object[]::new));
        json.put("sigData", Arrays.stream(sigData).map(p->Helper.toHexString(p)).toArray(Object[]::new));
        return json;
    }

}
