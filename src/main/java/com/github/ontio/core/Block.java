package com.github.ontio.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Function;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Address;
import com.github.ontio.common.UInt256;
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
import com.github.ontio.common.Inventory;
import com.github.ontio.common.InventoryType;

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
    public Address nextBookKeeper;
    /**
     *  用于验证该区块的脚本
     */
    public String[] sigData;
    public PubKeyInfo[] bookkeepers;
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
            _header.nextBookKeeper = this.nextBookKeeper;
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
        // 填充值
        if (reader.readByte() != 1) {
            throw new IOException();
        }
        // 脚本
//        try {
//			script = reader.readSerializable(Program.class);
//		} catch (InstantiationException | IllegalAccessException ex) {
//        	throw new IOException(ex);
//		}
        // 交易
//      transactions = new Transaction[(int) reader.readVarInt(0x10000000)];//xy
        transactions = new Transaction[(int) reader.readInt()];	//dna
        for (int i = 0; i < transactions.length; i++) {
            transactions[i] = Transaction.deserializeFrom(reader);
        }
        if (transactions.length > 0) {
            if (transactions[0].type != TransactionType.BookKeeping 
            		|| Arrays.stream(transactions).skip(1).anyMatch(p -> p.type == TransactionType.BookKeeping)) {
                throw new IOException();
            }
        }
    }

    @Override 
    public void deserializeUnsigned(BinaryReader reader) throws IOException {
        try {
            System.out.println("######b###deserializeUnsigned#####");
            version = reader.readInt();
            prevBlockHash = reader.readSerializable(UInt256.class);
            transactionsRoot = reader.readSerializable(UInt256.class);
            blockRoot = reader.readSerializable(UInt256.class);
            timestamp = reader.readInt();
            height = reader.readInt();
            consensusData = Long.valueOf(reader.readLong());
            nextBookKeeper = reader.readSerializable(Address.class);
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
        writer.writeSerializable(nextBookKeeper);
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
        return new Address[] { prev_header.nextBookKeeper };
    }

    /**
     * 根据区块中所有交易的Hash生成MerkleRoot
     */
    public void rebuildMerkleRoot() {
        transactionsRoot = MerkleTree.computeRoot(Arrays.stream(transactions).map(p -> p.hash()).toArray(UInt256[]::new));
    }

    public JObject json() {
        JObject json = new JObject();
        json.set("hash", new JString(hash().toString()));
        json.set("version", new JNumber(Integer.toUnsignedLong(version)));
        json.set("prevblockhash", new JString(prevBlockHash.toString()));
        json.set("transactionsRoot", new JString(transactionsRoot.toString()));
        json.set("blockRoot", new JString(blockRoot.toString()));
        json.set("time", new JNumber(timestamp));
        json.set("height", new JNumber(Integer.toUnsignedLong(height)));
        json.set("consensusData", new JNumber(consensusData));
//        json.set("nextminer", new JString(Wallet.toAddress(nextMiner)));
        json.set("transactions", new JArray(Arrays.stream(transactions).map(p -> p.json()).toArray(JObject[]::new)));
        return json;
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
    
    @Override
	public void fromJson(JsonReader reader) {
		JObject json = reader.json().get("Header");
		// unsigned
		this.version = new Double(json.get("Version").asNumber()).intValue();
        this.hash = UInt256.parse(json.get("Hash").asString());
		this.prevBlockHash = UInt256.parse(json.get("PrevBlockHash").asString());
		this.transactionsRoot = UInt256.parse(json.get("TransactionsRoot").asString());
        this.blockRoot = UInt256.parse(json.get("BlockRoot").asString());
		this.height = (int)json.get("Height").asNumber();
		this.timestamp = new BigDecimal(json.get("Timestamp").asString()).intValue();
		this.nextBookKeeper = Address.parse(json.get("NextBookKeeper").asString());
		this.consensusData = new BigDecimal(json.get("ConsensusData").asString()).longValue();
		JArray arr = (JArray) json.get("BookKeepers");
        this.bookkeepers = new PubKeyInfo[arr.size()];
        for(int i=0;i<arr.size();i++){
            this.bookkeepers[i] = new PubKeyInfo();
            this.bookkeepers[i].x = arr.get(i).get("X").asString();
            this.bookkeepers[i].y = arr.get(i).get("Y").asString();
        }
        JArray sigArr = (JArray) json.get("SigData");
        this.sigData  =  new String[sigArr.size()];
        for(int i=0;i<sigArr.size();i++){
            this.sigData[i] = new String();
            this.sigData[i] = sigArr.get(i).asString();
        }
		// txs
		JArray txsJson = (JArray) reader.json().get("Transactions");
		if(txsJson == null) {
			this.transactions = new Transaction[0];
			return;
		}
		// 针对于数组的解析
		int count = txsJson.size();
		this.transactions = new Transaction[count];
		for(int i=0; i<count; ++i) {
			try {
				this.transactions[i] = Transaction.fromJsonD(new JsonReader(txsJson.get(i)));
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Tx.deserialize failed");
			}
		}
	}

	class PubKeyInfo{
        public String x;
        public String y;
        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }
}
