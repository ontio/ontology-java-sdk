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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Contract implements Serializable {
    public byte version;
    public byte[] code = new byte[0];
    public Address constracHash;
    public String method;
    public byte[] args;

    public Contract(byte version,byte[] code,Address constracHash, String method,byte[] args){
        this.version = version;
        if (code != null) {
            this.code = code;
        }
        this.constracHash = constracHash;
        this.method = method;
        this.args = args;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        try {
            version = reader.readByte();
            code = reader.readVarBytes();
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
        writer.writeVarBytes(code);
        writer.writeSerializable(constracHash);
        writer.writeVarBytes(method.getBytes());
        writer.writeVarBytes(args);
    }
}
