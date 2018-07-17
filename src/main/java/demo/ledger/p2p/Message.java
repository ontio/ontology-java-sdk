package demo.ledger.p2p;

import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.crypto.Digest;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 *
 *
 */
public class Message {
    public static int NETWORK_MAGIC_MAINNET = 0x8c77ab60;
    public static int NETWORK_MAGIC_POLARIS = 0x2d8829df;
    public MessageHeader header ;
    public byte[] message ;
    public Message(){

    }
    public Message(byte[] msg){
        message = msg;
    }

    public void deserialization(byte[] data){
        ByteArrayInputStream ms = new ByteArrayInputStream(data);
        BinaryReader reader = new BinaryReader(ms);
        try {
            header = new MessageHeader();
            header.readMessageHeader(reader);
            int len = reader.available();
            message = new byte[len];
            System.arraycopy(data,data.length-len,message,0,len);
            System.out.println(header.cmdType());
            if(header.cmdType().contains("block")){
                Block block = Serializable.from(message, Block.class);
                System.out.println(block.json());
            }else if(header.cmdType().contains("getheaders")){
                HeadersReq headersReq = new HeadersReq();
                headersReq.deserialization(message);
            } else if(header.cmdType().contains("headers")){
                BlkHeader header = new BlkHeader();
                header.deserialization(message);
            } else if(header.cmdType().contains("version")){
                VersionReq version = new VersionReq();
                version.deserialization(message);
            } else if(header.cmdType().contains("getdata")){
                DataReq dataReq = new DataReq();
                dataReq.deserialization(message);
            } else if(header.cmdType().contains("ping")){
                PingReq pingReq = new PingReq();
                pingReq.deserialization(message);
            } else if(header.cmdType().contains("pong")){
                PongRsp pongRsp = new PongRsp();
                pongRsp.deserialization(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public byte[] serialization(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        try {
            header.writeMessageHeader(bw);
            bw.write(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static byte[] checkSum(byte[] data){
        byte[] hash = Digest.hash256(data);
        byte[] checksum = new byte[4];
        System.arraycopy(hash,0,checksum,0,checksum.length);
        return checksum;
    }
}
