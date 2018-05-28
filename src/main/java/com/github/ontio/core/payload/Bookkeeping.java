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

package com.github.ontio.core.payload;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.ontio.common.Address;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

public class Bookkeeping extends Transaction {
    private long nonce;

    public Bookkeeping() {
        super(TransactionType.Bookkeeping);
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        nonce = reader.readLong();
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeLong(nonce);
    }

    @Override
    public Object json() {
        Map obj = (Map) super.json();
        Map payload = new HashMap();
        payload.put("Nonce", nonce);
        obj.put("Payload", payload);
        return obj;
    }
}
