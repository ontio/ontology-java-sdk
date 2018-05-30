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
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TransferFrom implements Serializable {
    public Address sender;
    public Address from;
    public Address to;
    public long value;

    public TransferFrom(Address sender,Address from, Address to, long amount){
        this.sender = sender;
        this.from = from;
        this.to = to;
        this.value = amount;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        try {
            sender = reader.readSerializable(Address.class);
            from = reader.readSerializable(Address.class);
            to = reader.readSerializable(Address.class);
            value = reader.readVarInt();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeSerializable(sender);
        writer.writeSerializable(from);
        writer.writeSerializable(to);
        writer.writeVarInt(value);

    }


    public Object json() {
        Map json = new HashMap<>();
        json.put("sender", sender.toHexString());
        json.put("from", from.toHexString());
        json.put("to", to.toHexString());
        json.put("value", value);
        return json;
    }

}
