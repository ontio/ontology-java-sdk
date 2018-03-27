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

import java.io.*;
import java.util.*;
import java.util.stream.*;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.*;
import com.github.ontio.core.Inventory;
import com.github.ontio.core.InventoryType;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

/**
 *
 */
public abstract class Transaction extends Inventory {


    public byte version = 0;
    public final TransactionType txType;
    public int nonce = new Random().nextInt();
    public Attribute[] attributes;
    public Fee[] fee = new Fee[0];
    public long networkFee;
    public Sig[] sigs = new Sig[0];

    protected Transaction(TransactionType type) {
        this.txType = type;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        deserializeUnsigned(reader);
        try {
            sigs = reader.readSerializableArray(Sig.class);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deserializeUnsigned(BinaryReader reader) throws IOException {
        version = reader.readByte();
        if (txType.value() != reader.readByte()) {
            throw new IOException();
        }
        deserializeUnsignedWithoutType(reader);
    }

    private void deserializeUnsignedWithoutType(BinaryReader reader) throws IOException {
        try {
            deserializeExclusiveData(reader);
            attributes = reader.readSerializableArray(Attribute.class);
            int len = (int) reader.readVarInt();
            fee = new Fee[len];
            for (int i = 0; i < len; i++) {
                fee[i] = new Fee(reader.readLong(), reader.readSerializable(Address.class));
            }
            networkFee = reader.readLong();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IOException(ex);
        }
    }

    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        serializeUnsigned(writer);
        writer.writeSerializableArray(sigs);
    }

    @Override
    public void serializeUnsigned(BinaryWriter writer) throws IOException {
        writer.writeByte(version);
        writer.writeByte(txType.value());
        writer.writeInt(nonce);
        serializeExclusiveData(writer);
        writer.writeSerializableArray(attributes);
        writer.writeSerializableArray(fee);
        writer.writeLong(networkFee);
    }

    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Transaction)) {
            return false;
        }
        Transaction tx = (Transaction) obj;
        return hash().equals(tx.hash());
    }

    @Override
    public int hashCode() {
        return hash().hashCode();
    }


    public static Transaction deserializeFrom(byte[] value) throws IOException {
        return deserializeFrom(value, 0);
    }

    public static Transaction deserializeFrom(byte[] value, int offset) throws IOException {
        try (ByteArrayInputStream ms = new ByteArrayInputStream(value, offset, value.length - offset)) {
            try (BinaryReader reader = new BinaryReader(ms)) {
                return deserializeFrom(reader);
            }
        }
    }

    public static Transaction deserializeFrom(BinaryReader reader) throws IOException {
        try {
            byte ver = reader.readByte();
            TransactionType type = TransactionType.valueOf(reader.readByte());
            String typeName = "com.github.ontio.core.payload." + type.toString();
            Transaction transaction = (Transaction) Class.forName(typeName).newInstance();
            transaction.nonce = reader.readInt();
            transaction.version = ver;
            transaction.deserializeUnsignedWithoutType(reader);
            transaction.sigs = new Sig[(int) reader.readVarInt()];
            for (int i = 0; i < transaction.sigs.length; i++) {
                transaction.sigs[i] = reader.readSerializable(Sig.class);
            }
            return transaction;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        HashSet<Address> hashes = new HashSet<Address>(Arrays.stream(this.fee).map(p -> p.payer).collect(Collectors.toList()));
        return hashes.stream().sorted().toArray(Address[]::new);
    }

    @Override
    public final InventoryType inventoryType() {
        return InventoryType.TX;
    }

    public Object json() {
        Map json = new HashMap();
        json.put("Hash", hash().toString());
        json.put("Version", (int) version);
        json.put("Nonce", nonce);
        json.put("TxType", txType.value() & Byte.MAX_VALUE);
        json.put("Attributes", Arrays.stream(attributes).map(p -> p.json()).toArray(Object[]::new));
        json.put("Fee", Arrays.stream(fee).map(p -> p.json()).toArray(Object[]::new));
        json.put("Sigs", Arrays.stream(sigs).map(p -> p.json()).toArray(Object[]::new));
        return json;
    }

    @Override
    public boolean verify() {
        return true;
    }

}
