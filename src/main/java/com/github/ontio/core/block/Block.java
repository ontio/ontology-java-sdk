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

package com.github.ontio.core.block;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.*;
import com.github.ontio.core.Inventory;
import com.github.ontio.core.InventoryType;
import com.github.ontio.core.payload.BookKeeping;
import com.github.ontio.core.payload.DeployCodeTransaction;
import com.github.ontio.core.payload.InvokeCodeTransaction;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.JsonSerializable;
import com.github.ontio.io.Serializable;
import com.github.ontio.crypto.ECC;
import org.bouncycastle.math.ec.ECPoint;

/**
 *
 */
public class Block extends Inventory implements JsonSerializable {

    public int version;
    public UInt256 prevBlockHash;
    public UInt256 transactionsRoot;
    public UInt256 blockRoot;
    public int timestamp;
    public int height;
    public long consensusData;
    public Address nextBookkeeper;
    public String[] sigData;
    public ECPoint[] bookkeepers;
    public Transaction[] transactions;
    public UInt256 hash;
    private Block _header = null;
    public Block header() {
        if (isHeader()){
            return this;
        }
        if (_header == null) {
            _header = new Block();
            _header.prevBlockHash = prevBlockHash;
            _header.transactionsRoot = this.transactionsRoot;
            _header.blockRoot = this.blockRoot;
            _header.timestamp = this.timestamp;
            _header.height = this.height;
            _header.consensusData = this.consensusData;
            _header.nextBookkeeper = this.nextBookkeeper;
            _header.sigData = this.sigData;
            _header.bookkeepers = this.bookkeepers;
            _header.transactions = new Transaction[0];
        }
        return _header;
    }

    @Override
	public InventoryType inventoryType() { return InventoryType.Block; }

    public boolean isHeader() { return transactions.length == 0; }

    @Override 
    public void deserialize(BinaryReader reader) throws IOException {
        deserializeUnsigned(reader);
        int len = (int)reader.readVarInt();
        sigData = new String[len];
        for(int i=0;i<len;i++){
            this.sigData[i] = Helper.toHexString(reader.readVarBytes());
            System.out.println(this.sigData[i]);
        }

        len = (int) reader.readInt();
        System.out.println(len);
        transactions = new Transaction[len];
        for (int i = 0; i < transactions.length; i++) {
            transactions[i] = Transaction.deserializeFrom(reader);
        }
        if (transactions.length > 0) {
            if (transactions[0].txType != TransactionType.BookKeeping
            		|| Arrays.stream(transactions).skip(1).anyMatch(p -> p.txType == TransactionType.BookKeeping)) {
                throw new IOException();
            }
        }
    }

    @Override 
    public void deserializeUnsigned(BinaryReader reader) throws IOException {
        try {
            version = reader.readInt();
            prevBlockHash = reader.readSerializable(UInt256.class);
            transactionsRoot = reader.readSerializable(UInt256.class);
            blockRoot = reader.readSerializable(UInt256.class);
            timestamp = reader.readInt();
            height = reader.readInt();
            consensusData = Long.valueOf(reader.readLong());
            nextBookkeeper = reader.readSerializable(Address.class);
            int len = (int)reader.readVarInt();
            bookkeepers = new ECPoint[len];
            for(int i=0;i<len;i++){
                this.bookkeepers[i] = ECC.secp256r1.getCurve().createPoint(
                        new BigInteger(1, reader.readVarBytes()), new BigInteger(1, reader.readVarBytes()));
            }
	        transactions = new Transaction[0];
		} catch (InstantiationException | IllegalAccessException ex) {
        	throw new IOException(ex);
		}
    }
    
    @Override 
    public void serialize(BinaryWriter writer) throws IOException {
//        serializeUnsigned(writer);
//        writer.writeByte((byte)1);
//        writer.writeSerializableArray2(transactions);
    }

    @Override 
    public void serializeUnsigned(BinaryWriter writer) throws IOException {
        writer.writeInt(version);
        writer.writeSerializable(prevBlockHash);
        writer.writeSerializable(transactionsRoot);
        writer.writeSerializable(blockRoot);
        writer.writeInt(timestamp);
        writer.writeInt(height);
        writer.writeLong(consensusData);
        writer.writeSerializable(nextBookkeeper);
    }

    @Override 
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (!(obj instanceof Block)) {
        	return false;
        }
        return this.hash().equals(((Block) obj).hash());
    }
    
    @Override 
    public int hashCode() {
        return hash().hashCode();
    }

    public static Block fromTrimmedData(byte[] data, int index) throws IOException {
    	return fromTrimmedData(data, index, null);
    }

    public static Block fromTrimmedData(byte[] data, int index, Function<UInt256, Transaction> txSelector) throws IOException {
        Block block = new Block();
        try (ByteArrayInputStream ms = new ByteArrayInputStream(data, index, data.length - index)) {
	        try (BinaryReader reader = new BinaryReader(ms)) {
	        	block.deserializeUnsigned(reader);
	        	reader.readByte();
	        	if (txSelector == null) {
	        		block.transactions = new Transaction[0];
	        	} else {
		        	block.transactions = new Transaction[(int)reader.readVarInt(0x10000000)];
		        	for (int i = 0; i < block.transactions.length; i++) {
		        		block.transactions[i] = txSelector.apply(reader.readSerializable(UInt256.class));
		        	}
	        	}
	        } catch (InstantiationException | IllegalAccessException ex) {
				throw new IOException(ex);
			}
        }
        return block;
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }


    public Object json() {
        Map json = new HashMap();
        Map head = new HashMap();
        json.put("Hash", hash().toString());

        head.put("Version", version);
        head.put("PrevBlockHash", prevBlockHash.toString());
        head.put("TransactionsRoot", transactionsRoot.toString());
        head.put("BlockRoot", blockRoot.toString());
        head.put("Timestamp", timestamp);
        head.put("Height", height);
        head.put("ConsensusData",consensusData & Long.MAX_VALUE);
        head.put("NextBookkeeper",nextBookkeeper);
        head.put("Hash",hash().toString());
        head.put("SigData", Arrays.stream(sigData).toArray(Object[]::new));

        json.put("Header", head);
        System.out.println(transactions.length);
        json.put("Transactions", Arrays.stream(transactions).map(p -> {
            if (p instanceof InvokeCodeTransaction) {
                return ((InvokeCodeTransaction)p).json();
            } else if(p instanceof DeployCodeTransaction) {
                return ((DeployCodeTransaction)p).json();
            } else if(p instanceof BookKeeping) {
                return ((BookKeeping)p).json();
            }else {
                return p.json();
            }
        }).toArray(Object[]::new));
        return JSON.toJSONString(json);
    }

    public byte[] trim() {
        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
	        try (BinaryWriter writer = new BinaryWriter(ms)) {
	            serializeUnsigned(writer);
	            writer.writeByte((byte)1);
	            writer.writeSerializableArray(Arrays.stream(transactions).map(p -> p.hash()).toArray(Serializable[]::new));
	            writer.flush();
	            return ms.toByteArray();
	        }
        } catch (IOException ex) {
        	throw new UnsupportedOperationException(ex);
		}
    }

    @Override public boolean verify() {
        return verify(false);
    }


    public boolean verify(boolean completely) {
    	return true;
    }
    
//    @Override
//	public void fromJson(JsonReader reader) {
//		JObject json = reader.json().get("Header");
//		// unsigned
//		this.version = new Double(json.get("Version").asNumber()).intValue();
//        this.hash = UInt256.parse(json.get("Hash").asString());
//		this.prevBlockHash = UInt256.parse(json.get("PrevBlockHash").asString());
//		this.transactionsRoot = UInt256.parse(json.get("TransactionsRoot").asString());
//        this.blockRoot = UInt256.parse(json.get("BlockRoot").asString());
//		this.height = (int)json.get("Height").asNumber();
//		this.timestamp = new BigDecimal(json.get("Timestamp").asString()).intValue();
//		this.nextBookKeeper = Address.parse(json.get("NextBookKeeper").asString());
//		this.consensusData = new BigDecimal(json.get("ConsensusData").asString()).longValue();
//		JArray arr = (JArray) json.get("BookKeepers");
//        this.bookkeepers = new ECPoint[arr.size()];
//        for(int i=0;i<arr.size();i++){
//            this.bookkeepers[i] = ECC.secp256r1.getCurve().createPoint(
//                    new BigInteger(1, arr.get(i).get("X").asString().getBytes()), new BigInteger(1, arr.get(i).get("Y").asString().getBytes()));
//        }
//        JArray sigArr = (JArray) json.get("SigData");
//        this.sigData  =  new String[sigArr.size()];
//        for(int i=0;i<sigArr.size();i++){
//            this.sigData[i] = new String();
//            this.sigData[i] = sigArr.get(i).asString();
//        }
//		// txs
//		JArray txsJson = (JArray) reader.json().get("Transactions");
//		if(txsJson == null) {
//			this.transactions = new Transaction[0];
//			return;
//		}
//
//		int count = txsJson.size();
//		this.transactions = new Transaction[count];
//		for(int i=0; i<count; ++i) {
//			try {
//				this.transactions[i] = Transaction.fromJsonD(new JsonReader(txsJson.get(i)));
//			} catch (IOException e) {
//				e.printStackTrace();
//				throw new RuntimeException("Tx.deserialize failed");
//			}
//		}
//	}
}
