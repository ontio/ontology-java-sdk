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
import com.github.ontio.io.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Fee implements Serializable {
    public long amount;
    public Address payer;
    public Fee(){

    }
    public Fee(long amount,Address payer){
        this.amount = amount;
        this.payer = payer;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        amount = reader.readLong();
        try {
            payer = reader.readSerializable(Address.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeLong(amount);
        writer.writeSerializable(payer);
    }

    public Object json() {
        Map json = new HashMap<>();
        json.put("Amount", amount);
        json.put("Payer", payer.toHexString());
        return json;
    }

}
