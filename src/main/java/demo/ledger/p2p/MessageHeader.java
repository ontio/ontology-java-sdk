package demo.ledger.p2p;

import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

import java.util.Arrays;

public class MessageHeader {
    public int magic;
    public byte[] cmd = new byte[12];
    public int length;
    public byte[] checksum = new byte[4];
    public MessageHeader(){

    }
    public MessageHeader(int magic,byte[] cmd,int length,byte[] checksum) {
        this.magic = magic;
        System.arraycopy(cmd,0,cmd,0,cmd.length);
        this.length = length;
        this.checksum = checksum;
    }
    public void readMessageHeader(BinaryReader reader) throws Exception {
        magic = reader.readInt();
        cmd = reader.readBytes(cmd.length);
        length = reader.readInt();
        checksum = reader.readBytes(checksum.length);
    }
    public void writeMessageHeader(BinaryWriter writer) throws Exception {
        writer.writeInt(magic);
        writer.write(cmd);
        writer.writeInt(length);
        writer.write(checksum);
    }
    public String cmdType(){
        return new String(cmd);
    }

}
