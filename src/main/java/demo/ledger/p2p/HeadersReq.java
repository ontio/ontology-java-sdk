package demo.ledger.p2p;

import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;

/**
 *
 *
 */
public class HeadersReq {
    public byte len;
    public byte[] hashStart = new byte[32];
    public byte[] hashEnd =  new byte[32];
    public HeadersReq(){

    }
    public void deserialization(byte[] data){
        len = data[0];
        System.arraycopy(data,1,hashStart,0,hashStart.length);
        System.arraycopy(data,33,hashEnd,0,hashEnd.length);
        hashStart = Helper.reverse(hashStart);
        hashEnd = Helper.reverse(hashEnd);
    }
    public byte[] serialization(){
        byte[] data = new byte[65];
        data[0] = len;
        System.arraycopy(hashStart,0,data,1,hashStart.length);
        System.arraycopy(hashEnd,0,data,33,hashEnd.length);
        return data;
    }
    public byte[] msgSerialization(){
        Message msg = new Message(serialization());
        msg.header = new MessageHeader(Message.NETWORK_MAGIC_MAINNET,"getheaders".getBytes(),msg.message.length,Message.checkSum(msg.message));
        return msg.serialization();
    }
}
