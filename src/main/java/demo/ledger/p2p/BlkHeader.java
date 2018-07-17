package demo.ledger.p2p;

import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import demo.ledger.common.BlockHeader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *
 *
 */
public class BlkHeader {
    public BlockHeader[] headers;
    public BlkHeader(){

    }
    public void deserialization(byte[] data){
        ByteArrayInputStream ms = new ByteArrayInputStream(data);
        BinaryReader reader = new BinaryReader(ms);
        try {
            int count = reader.readInt();
            headers = new BlockHeader[count];
            for(int i=0;i<count;i++){
                headers[i] = reader.readSerializable(BlockHeader.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
