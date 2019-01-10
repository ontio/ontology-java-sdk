package com.github.ontio.sidechain.core.transaction;

import com.github.ontio.common.Address;
import com.github.ontio.core.Inventory;
import com.github.ontio.core.InventoryType;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Transaction extends Inventory {
    public byte version = 1;
    public long sideChainId;
    public TransactionType txType;
    public int nonce = new Random().nextInt();
    public long gasPrice = 0;
    public long gasLimit = 0;
    public Address payer = new Address();
    public Attribute[] attributes;
    public Sig[] sigs = new Sig[0];
    protected Transaction(TransactionType type) {
        this.txType = type;
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
            transaction.sideChainId = reader.readLong();
            transaction.gasPrice = reader.readLong();
            transaction.gasLimit = reader.readLong();
            transaction.payer = reader.readSerializable(Address.class);
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
        txType = TransactionType.valueOf(reader.readByte());
        nonce = reader.readInt();
        version = reader.readByte();
        gasPrice = reader.readLong();
        gasLimit = reader.readLong();
        try {
            payer = reader.readSerializable(Address.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        deserializeUnsignedWithoutType(reader);
    }

    private void deserializeUnsignedWithoutType(BinaryReader reader) throws IOException {
        try {
            deserializeExclusiveData(reader);
            attributes = reader.readSerializableArray(Attribute.class);
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
        writer.writeInt((int) sideChainId);
        writer.writeByte(txType.value());
        writer.writeInt(nonce);
        writer.writeLong(gasPrice);
        writer.writeLong(gasLimit);
        writer.writeSerializable(payer);
        serializeExclusiveData(writer);
        writer.writeSerializableArray(attributes);
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
        if (!(obj instanceof com.github.ontio.core.transaction.Transaction)) {
            return false;
        }
        com.github.ontio.core.transaction.Transaction tx = (com.github.ontio.core.transaction.Transaction) obj;
        return hash().equals(tx.hash());
    }

    @Override
    public int hashCode() {
        return hash().hashCode();
    }


    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }

    @Override
    public final InventoryType inventoryType() {
        return InventoryType.TX;
    }

    public Object json() {
        Map json = new HashMap();
        json.put("Hash", hash().toString());
        json.put("Version", (int) version);
        json.put("Nonce", nonce& 0xFFFFFFFF);
        json.put("TxType", txType.value() & 0xFF);
        json.put("GasPrice",gasPrice);
        json.put("GasLimit",gasLimit);
        json.put("Payer",payer.toBase58());
        json.put("Attributes", Arrays.stream(attributes).map(p -> p.json()).toArray(Object[]::new));
        json.put("Sigs", Arrays.stream(sigs).map(p -> p.json()).toArray(Object[]::new));
        return json;
    }

    @Override
    public boolean verify() {
        return true;
    }
}
