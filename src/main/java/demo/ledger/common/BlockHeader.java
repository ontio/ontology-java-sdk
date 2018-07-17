package demo.ledger.common;

import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.io.BinaryReader;

import java.io.IOException;

/**
 *
 *
 */
public class BlockHeader extends Block {

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        deserializeUnsigned(reader);
        int len = (int) reader.readVarInt();
        sigData = new String[len];
        for (int i = 0; i < len; i++) {
            this.sigData[i] = Helper.toHexString(reader.readVarBytes());
        }

//        len = reader.readInt();
//        transactions = new Transaction[len];
//        for (int i = 0; i < transactions.length; i++) {
//            transactions[i] = Transaction.deserializeFrom(reader);
//        }
    }
}
