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

package com.github.ontio.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.*;
import com.github.ontio.core.payload.BookKeeping;
import com.github.ontio.core.payload.DeployCodeTransaction;
import com.github.ontio.core.payload.InvokeCodeTransaction;
import com.github.ontio.io.JsonReader;
import com.github.ontio.crypto.MerkleTree;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.JsonSerializable;
import com.github.ontio.io.Serializable;
import com.github.ontio.io.json.JArray;
import com.github.ontio.io.json.JNumber;
import com.github.ontio.io.json.JObject;
import com.github.ontio.io.json.JString;
import com.github.ontio.crypto.ECC;
import com.github.ontio.io.BinaryReader;
import org.bouncycastle.math.ec.ECPoint;

/**
 *  区块或区块头
 */
public class Block extends Inventory implements JsonSerializable {
    /**
     *  区块版本
     */
    public int version; 
    /**
     *  前一个区块的散列值
     */
    public UInt256 prevBlockHash;
    /**
     *  该区块中所有交易的Merkle树的根
     */
    public UInt256 transactionsRoot;
    public UInt256 blockRoot;
    /**
     *  时间戳
     */
    public int timestamp; 
    /**
     *  区块高度
     */
    public int height; 
    /**
     *  随机数
     */
    public long consensusData;
    /**
     *  下一个区块的记账合约的散列值
     */
    public Address nextBookkeeper;
    /**
     *  用于验证该区块的脚本
     */
    public String[] sigData;
    public ECPoint[] bookkeepers;
    /**
     *  交易列表，当列表中交易的数量为0时，该Block对象表示一个区块头
     */
    public Transaction[] transactions;
    public UInt256 hash;

    /**
     *  该区块的区块头
     */
	//[NonSerialized]
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
            _header.transactions = new Transaction[0];
        }
        return _header;
    }

    /**
     *  资产清单的类型
     */
    @Override
	public InventoryType inventoryType() { return InventoryType.Block; }

    /**
     *  是否为区块头
     */
    public boolean isHeader() { return transactions.length == 0; }

    /**
     *  反序列化
     *  <param name="reader">数据来源</param>
     * @throws IOException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    @Override 
    public void deserialize(BinaryReader reader) throws IOException {
    	// 未签名
        deserializeUnsigned(reader);
        int len = (int)reader.readVarInt();
        sigData = new String[len];
        for(int i=0;i<len;i++){
            this.sigData[i] = Helper.toHexString(reader.readVarBytes());
            System.out.println(this.sigData[i]);
        }

        // 填充值
//        if (reader.readByte() != 1) {
//            throw new IOException();
//        }
        // 脚本
//        try {
//			script = reader.readSerializable(Program.class);
//		} catch (InstantiationException | IllegalAccessException ex) {
//        	throw new IOException(ex);
//		}
        // 交易
//      transactions = new Transaction[(int) reader.readVarInt(0x10000000)];//xy
        len = (int) reader.readInt();
        System.out.println(len);
        transactions = new Transaction[len];	//dna
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
//                this.bookkeepers[i].x = Helper.toHexString(reader.readVarBytes());
//                this.bookkeepers[i].y = Helper.toHexString(reader.readVarBytes());
            }
	        transactions = new Transaction[0];
		} catch (InstantiationException | IllegalAccessException ex) {
        	throw new IOException(ex);
		}
    }
    
    @Override 
    public void serialize(BinaryWriter writer) throws IOException {
        serializeUnsigned(writer);
        writer.writeByte((byte)1);
//        writer.writeSerializable(script);
        writer.writeSerializableArray2(transactions);
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
    
    /**
     * 反序列化Block(static)
     */
    public static Block fromTrimmedData(byte[] data, int index) throws IOException {
    	return fromTrimmedData(data, index, null);
    }

    public static Block fromTrimmedData(byte[] data, int index, Function<UInt256, Transaction> txSelector) throws IOException {
        Block block = new Block();
        try (ByteArrayInputStream ms = new ByteArrayInputStream(data, index, data.length - index)) {
	        try (BinaryReader reader = new BinaryReader(ms)) {
	        	// 未签名的
	        	block.deserializeUnsigned(reader);
	        	// 填充值
	        	reader.readByte(); 
	        	// 脚本
//	        	block.script = reader.readSerializable(Program.class);
	        	// 交易
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

    /**
     * 获取验证脚本
     */
    @Override 
    public Address[] getAddressU160ForVerifying() {
//        if (prevBlockHash.equals(UInt256.ZERO)) {
//            return new Address[] { Program.toScriptHash(script.parameter) };
//        }
        Block prev_header;
		try {
			prev_header = Blockchain.current().getHeader(prevBlockHash);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
        if (prev_header == null) {
        	throw new IllegalStateException();
        }
        return new Address[] { prev_header.nextBookkeeper };
    }

    /**
     * 根据区块中所有交易的Hash生成MerkleRoot
     */
    public void rebuildMerkleRoot() {
        transactionsRoot = MerkleTree.computeRoot(Arrays.stream(transactions).map(p -> p.hash()).toArray(UInt256[]::new));
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
    
	/**
     *  把区块对象变为只包含区块头和交易Hash的字节数组，去除交易数据
     *  <returns>返回只包含区块头和交易Hash的字节数组</returns>
     */
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

    /**
     * 验证该区块头是否合法
     * 
     */
    @Override public boolean verify() {
        return verify(false);
    }

    /**
     * 验证该区块头是否合法
     * 
     * @param completely 是否同时验证区块中的每一笔交易
     * @return
     */
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
//		// 针对于数组的解析
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
