package demo.ledger.p2p;

import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 *
 */
public class DataReq {
    public byte inventoryType = 2;// 1 transaction, 2 block , 3 consensus
    public byte[] hash = new byte[32];
    public DataReq(){

    }
    public DataReq(byte inventoryType,byte[] hash){
        this.inventoryType = inventoryType;
        this.hash = hash;
    }
    public byte[] serialization(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        try {
            bw.writeByte(inventoryType);
            bw.write(hash);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
    public byte[] msgSerialization(){
        Message msg = new Message(serialization());
        msg.header = new MessageHeader(Message.NETWORK_MAGIC_MAINNET,"getdata".getBytes(),msg.message.length,Message.checkSum(msg.message));
        return msg.serialization();
    }
    public void deserialization(byte[] data){
        ByteArrayInputStream ms = new ByteArrayInputStream(data);
        BinaryReader reader = new BinaryReader(ms);
        try {
            inventoryType = reader.readByte();
            hash = reader.readBytes(32);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
