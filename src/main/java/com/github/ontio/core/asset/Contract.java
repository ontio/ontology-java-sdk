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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Contract implements Serializable {
    public byte version;
    public Address constracHash;
    public String method;
    public byte[] args;
    public Contract(){

    }
    public Contract(byte version,Address constracHash, String method,byte[] args){
        this.version = version;
        this.constracHash = constracHash;
        this.method = method;
        this.args = args;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        try {
            version = reader.readByte();
            constracHash = reader.readSerializable(Address.class);
            method = new String(reader.readVarBytes());
            args = reader.readVarBytes();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeByte(version);
        writer.writeSerializable(constracHash);
        writer.writeVarBytes(method.getBytes());
        writer.writeVarBytes(args);
    }


    public static Contract deserializeFrom(byte[] value) throws IOException {
        try {
            int offset = 0;
            ByteArrayInputStream ms = new ByteArrayInputStream(value, offset, value.length - offset);
            BinaryReader reader = new BinaryReader(ms);
            Contract contract = new Contract();
            contract.deserialize(reader);
            return contract;
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }
}
