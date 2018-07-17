package demo.ledger.store;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.payload.DeployCode;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import demo.ledger.common.BookkeeperState;
import demo.ledger.common.DataEntryPrefix;
import org.iq80.leveldb.DB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
public class StateStore {
    public DB db;

    public StateStore(DB db) {
        this.db = db;
    }
    public Map GetMerkleTree(){
        Map map = new HashMap();
        byte[] key = DataEntryPrefix.getMerkleTreeKey();
        byte[] value = db.get(key);
        ByteArrayInputStream ms = new ByteArrayInputStream(value);
        BinaryReader reader = new BinaryReader(ms);
        try {
            int treeSize = reader.readInt();
            int hashCount = (value.length-4)/UInt256.ZERO.toArray().length;
            UInt256[] hashes = new UInt256[hashCount];
            String[] hashesStr = new String[hashCount];
            for (int i = 0; i < hashCount; i++) {
                hashes[i] = reader.readSerializable(UInt256.class);
                hashesStr[i] = hashes[i].toHexString();
            }
            map.put("treeSize",treeSize);
            map.put("hashes",hashesStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    public DeployCode GetContractState(Address contractHash){
        DeployCode deployCode = new DeployCode();
        byte[] key = DataEntryPrefix.getContractStateKey(contractHash);
        byte[] value = db.get(key);
        ByteArrayInputStream ms = new ByteArrayInputStream(value);
        BinaryReader reader = new BinaryReader(ms);
        try {
            deployCode.deserializeExclusiveData(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deployCode;
    }
    public BookkeeperState GetBookkeeperState(){
        BookkeeperState bookkeeperState = new BookkeeperState();
        byte[] key = DataEntryPrefix.getBookkeeperKey();
        byte[] value = db.get(key);
        ByteArrayInputStream ms = new ByteArrayInputStream(value);
        BinaryReader reader = new BinaryReader(ms);
        try {
            bookkeeperState.StateVersion = reader.readByte();
            bookkeeperState.CurrBookkeeper = new byte[reader.readInt()][];
            for(int i=0;i < bookkeeperState.CurrBookkeeper.length;i++){
                bookkeeperState.CurrBookkeeper[i] = reader.readVarBytes();
            }
            bookkeeperState.NextBookkeeper = new byte[reader.readInt()][];
            for(int i=0;i < bookkeeperState.NextBookkeeper.length;i++){
                bookkeeperState.NextBookkeeper[i] = reader.readVarBytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookkeeperState;
    }
    public void SaveBookkeeperState(BookkeeperState bookkeeperState){
        byte[] key = DataEntryPrefix.getBookkeeperKey();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        try {
            bw.writeByte(bookkeeperState.StateVersion);
            bw.writeInt(bookkeeperState.CurrBookkeeper.length);
            for(int i=0;i < bookkeeperState.CurrBookkeeper.length;i++){
                bw.writeVarBytes(bookkeeperState.CurrBookkeeper[i]);
            }
            bw.writeInt(bookkeeperState.NextBookkeeper.length);
            for(int i=0;i < bookkeeperState.NextBookkeeper.length;i++){
                bw.writeVarBytes(bookkeeperState.NextBookkeeper[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.put(key, baos.toByteArray());
    }
}
