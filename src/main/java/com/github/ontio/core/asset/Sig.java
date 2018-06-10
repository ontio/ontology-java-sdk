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
import java.util.*;

import static com.github.ontio.core.program.Program.*;

/**
 *
 */
public class Sig implements Serializable {
    public byte[][] pubKeys = null;
    public int M;
    public byte[][] sigData;

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        //TODO fix below
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
        writer.writeVarBytes(ProgramFromParams(sigData));
        try {
            if(pubKeys.length == 1){
                writer.writeVarBytes(ProgramFromPubKey(pubKeys[0]));
            }else if(pubKeys.length > 1){
                writer.writeVarBytes(ProgramFromMultiPubKey(M,pubKeys));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public Object json() {
        Map json = new HashMap<>();
        json.put("M", M);
        List list = new ArrayList();
        for(int i=0;i<pubKeys.length;i++){
            list.add(Helper.toHexString(pubKeys[i]));
        }
        List list2 = new ArrayList();
        for(int i=0;i<sigData.length;i++){
            list2.add(Helper.toHexString(sigData[i]));
        }
        json.put("PubKeys",list);
        json.put("SigData",list2);
        //json.put("PubKeys", Arrays.stream(pubKeys).map(p->Helper.toHexString(p)).toArray(String[]::new));
        //json.put("SigData", Arrays.stream(sigData).map(p->Helper.toHexString(p)).toArray(String[]::new));
        return json;
    }

}
