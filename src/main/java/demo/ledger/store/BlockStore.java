package demo.ledger.store;

import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import demo.ledger.common.BlockHeader;
import demo.ledger.common.DataEntryPrefix;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

/**
 *
 *
 */
public class BlockStore {
    public DB db;

    public BlockStore(DB db){
        this.db = db;
    }

    public void SaveHeaderIndexList(long startIndex,UInt256[] indexList){
        byte[] indexKey = DataEntryPrefix.getHeaderIndexListKey(startIndex);
        int indexSize = indexList.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        try {
            bw.writeInt(indexSize);
            for(int i=0;i<indexSize;i++) {
                bw.writeSerializable(indexList[i]);
            }
            db.put(indexKey, baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveCurrentBlock(long height,UInt256 blockHash){
        byte[] indexKey = DataEntryPrefix.getCurrentBlockKey();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        try {
            bw.writeSerializable(blockHash);
            bw.writeVarInt(height);
            db.put(indexKey, baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void SaveBlockHash(long height,UInt256 blockHash){
        byte[] key = DataEntryPrefix.getBlockHashKey(height);
        db.put(key, blockHash.toArray());
    }

    public void SaveTransaction(Transaction tx,long height){
        putTransaction(tx,height);
    }

    public void putTransaction(Transaction tx,long height){
        byte[] key = DataEntryPrefix.getTransactionKey(tx.hash());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        try {
            bw.writeVarInt(height);
            db.put(key, baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void saveBlockToStateStore(Block block){

        SaveCurrentBlock(block.height,block.hash);

    }
    public void saveBlockToBlockStore(Block block){

    }
    public Transaction GetTransaction(UInt256 txhash){
        byte[] key = DataEntryPrefix.getTransactionKey(txhash);
        byte[] value = db.get(key);
        System.out.println(Helper.toHexString(value));
        ByteArrayInputStream ms = new ByteArrayInputStream(value);
        BinaryReader reader = new BinaryReader(ms);
        try {
            int height = reader.readInt();
            System.out.println(height);
            int len = reader.available();
            byte[] message = new byte[len];
            System.arraycopy(value,value.length-len,message,0,len);
            Transaction tx = Transaction.deserializeFrom(message);
            return tx;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    public Block GetBlock(UInt256 blockHash){
        byte[] key = DataEntryPrefix.getHeaderKey(blockHash);
        byte[] value = db.get(key);
        System.out.println(Helper.toHexString(value));
        ByteArrayInputStream ms = new ByteArrayInputStream(value);
        BinaryReader reader = new BinaryReader(ms);
        try {
            long sysFee = reader.readLong();
            System.out.println(sysFee);
            BlockHeader header = reader.readSerializable(BlockHeader.class);
            int txSize = reader.readInt();
            UInt256[] hashes = new UInt256[txSize];
            System.out.println(header.json());
            for(int i=0;i<txSize;i++){
                hashes[i] = reader.readSerializable(UInt256.class);
                System.out.println(hashes[i].toHexString());
                Transaction tx = GetTransaction(hashes[i]);
                System.out.println(tx.json());
            }
            //System.out.println((txSize));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Block();
    }
    public Map GetCurrentBlock(){
        byte[] key = DataEntryPrefix.getCurrentBlockKey();
        byte[] value = db.get(key);
        ByteArrayInputStream ms = new ByteArrayInputStream(value);
        BinaryReader reader = new BinaryReader(ms);
        Map map = null;
        try {
            UInt256 hash = reader.readSerializable(UInt256.class);
            long height = reader.readInt();
            map = new HashMap();
            map.put("Hash",hash.toHexString());
            map.put("Height",height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
