package ontology.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Function;

import ontology.common.UInt160;
import ontology.common.UInt256;
import ontology.core.scripts.Program;
import ontology.crypto.MerkleTree;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.io.JsonReader;
import ontology.io.JsonSerializable;
import ontology.io.Serializable;
import ontology.io.json.JArray;
import ontology.io.json.JNumber;
import ontology.io.json.JObject;
import ontology.io.json.JString;
import ontology.common.Inventory;
import ontology.common.InventoryType;

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
    public UInt256 prevBlock;
    /**
     *  该区块中所有交易的Merkle树的根
     */
    public UInt256 merkleRoot;
    public UInt256 blockRoot;
    public UInt256 stateRoot;
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
    public long nonce; 
    /**
     *  下一个区块的记账合约的散列值
     */
    public UInt160 nextMiner;
    /**
     *  用于验证该区块的脚本
     */
    public Program script;
    /**
     *  交易列表，当列表中交易的数量为0时，该Block对象表示一个区块头
     */
    public Transaction[] transactions;

    /**
     *  该区块的区块头
     */
	//[NonSerialized]
    private Block _header = null;
    public Block header() {
        if (isHeader()) return this;
        if (_header == null) {
            _header = new Block();
            _header.prevBlock = prevBlock;
            _header.merkleRoot = this.merkleRoot;
            _header.blockRoot = this.blockRoot;
            _header.stateRoot = this.stateRoot;
            _header.timestamp = this.timestamp;
            _header.height = this.height;
            _header.nonce = this.nonce;
            _header.nextMiner = this.nextMiner;
            _header.script = this.script;
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
        if (reader.readByte() != 1) 
        	throw new IOException();
        // 脚本
        try {
			script = reader.readSerializable(Program.class);
		} catch (InstantiationException | IllegalAccessException ex) {
        	throw new IOException(ex);
		}
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
            version = reader.readInt();
            prevBlock = reader.readSerializable(UInt256.class);
            merkleRoot = reader.readSerializable(UInt256.class);
            blockRoot = reader.readSerializable(UInt256.class);
            stateRoot = reader.readSerializable(UInt256.class);
            timestamp = reader.readInt();
            height = reader.readInt();
            nonce = Long.valueOf(reader.readLong());
			nextMiner = reader.readSerializable(UInt160.class);
	        transactions = new Transaction[0];
		} catch (InstantiationException | IllegalAccessException ex) {
        	throw new IOException(ex);
		}
    }
    
    @Override 
    public void serialize(BinaryWriter writer) throws IOException {
        serializeUnsigned(writer);
        writer.writeByte((byte)1);
        writer.writeSerializable(script);
        writer.writeSerializableArray2(transactions);
    }

    @Override 
    public void serializeUnsigned(BinaryWriter writer) throws IOException {
        writer.writeInt(version);
        writer.writeSerializable(prevBlock);
        writer.writeSerializable(merkleRoot);
        writer.writeSerializable(blockRoot);
        writer.writeSerializable(stateRoot);
        writer.writeInt(timestamp);
        writer.writeInt(height);
        writer.writeLong(nonce);
        writer.writeSerializable(nextMiner);
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
	        	block.script = reader.readSerializable(Program.class);
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
    public UInt160[] getAddressU160ForVerifying() {
        if (prevBlock.equals(UInt256.ZERO)) {
            return new UInt160[] { Program.toScriptHash(script.parameter) };
        }
        Block prev_header;
		try {
			prev_header = Blockchain.current().getHeader(prevBlock);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
        if (prev_header == null) {
        	throw new IllegalStateException();
        }
        return new UInt160[] { prev_header.nextMiner };
    }

    /**
     * 根据区块中所有交易的Hash生成MerkleRoot
     */
    public void rebuildMerkleRoot() {
        merkleRoot = MerkleTree.computeRoot(Arrays.stream(transactions).map(p -> p.hash()).toArray(UInt256[]::new));
    }

    public JObject json() {
        JObject json = new JObject();
        json.set("hash", new JString(hash().toString()));
        json.set("version", new JNumber(Integer.toUnsignedLong(version)));
        json.set("previousblockhash", new JString(prevBlock.toString()));
        json.set("merkleroot", new JString(merkleRoot.toString()));
        json.set("blockRoot", new JString(blockRoot.toString()));
        json.set("stateRoot", new JString(stateRoot.toString()));
        json.set("time", new JNumber(timestamp));
        json.set("height", new JNumber(Integer.toUnsignedLong(height)));
        json.set("nonce", new JNumber(nonce));
//        json.set("nextminer", new JString(Wallet.toAddress(nextMiner)));
        json.set("script", script.json());
        json.set("tx", new JArray(Arrays.stream(transactions).map(p -> p.json()).toArray(JObject[]::new)));
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
	            writer.writeSerializable(script);
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
		JObject json = reader.json().get("BlockData");
		// unsigned
		this.version = (int)json.get("Version").asNumber();
		this.prevBlock = UInt256.parse(json.get("PrevBlockHash").asString());
		this.merkleRoot = UInt256.parse(json.get("TransactionsRoot").asString());
        this.blockRoot = UInt256.parse(json.get("BlockRoot").asString());
        this.stateRoot = UInt256.parse(json.get("StateRoot").asString());
		this.height = (int)json.get("Height").asNumber();
		this.timestamp = new BigDecimal(json.get("Timestamp").asString()).intValue();
		this.nextMiner = UInt160.parse(json.get("NextBookKeeper").asString());
		this.nonce = new BigDecimal(json.get("ConsensusData").asString()).longValue();
		// script
		try {
			this.script = new JsonReader(json.get("Program")).readSerializable(Program.class);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			throw new RuntimeException("");
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
}