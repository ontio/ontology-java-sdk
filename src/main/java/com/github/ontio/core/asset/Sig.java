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
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *  脚本
 */
public class Sig implements Serializable, JsonSerializable {
    public ECPoint[] pubKeys = null;
    public int M;
    public byte[][] sigData;

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    	int len = (int)reader.readVarInt();
        pubKeys = new ECPoint[(int)len];
        for(int i=0;i<pubKeys.length;i++) {
            pubKeys[i] = ECC.secp256r1.getCurve().createPoint(
                    new BigInteger(1, reader.readVarBytes()), new BigInteger(1, reader.readVarBytes()));
        }
        M = (int)reader.readVarInt();
        len = (int)reader.readVarInt();
        sigData = new byte[len][];
        for(int i=0;i<sigData.length;i++) {
            sigData[i] = reader.readVarBytes();
            System.out.println(Helper.toHexString(sigData[i]));
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    	writer.writeVarInt(pubKeys.length);
    	for(int i=0;i<pubKeys.length;i++) {
            writer.writeVarBytes(Helper.removePrevZero(pubKeys[i].getXCoord().toBigInteger().toByteArray()));
            writer.writeVarBytes(Helper.removePrevZero(pubKeys[i].getYCoord().toBigInteger().toByteArray()));
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
        json.put("PubKeys", Arrays.stream(pubKeys).map(p->Helper.toHexString(p.getEncoded(true))).toArray(Object[]::new));
        json.put("sigData", Arrays.stream(sigData).map(p->Helper.toHexString(p)).toArray(Object[]::new));
        return json;
    }
//    @Override
//    public void fromJson(JsonReader reader) {
//        JObject json = reader.json();
//        M = new Double(json.get("M").asNumber()).intValue();
//       // pubKeys = Address.parse(json.get("Payer").asString());
//        //JArray array = (JArray) json.get("SigData");
//        //sigData =reader.readSerializableArray(Byte.class, array.size(), "SigData");
//    }
//    public static Sig fromJsonD(JsonReader reader) throws IOException {
//        try {
//            Sig f = (Sig)Class.forName("com.github.ontio.core.asset.Sig").newInstance();
//            f.fromJson(reader);;
//            return f;
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
//            ex.printStackTrace();
//            throw new IOException(ex);
//        }
//    }
}
