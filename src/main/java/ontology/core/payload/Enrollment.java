package ontology.core.payload;

import ontology.common.Helper;
import ontology.core.Transaction;
import ontology.core.TransactionType;
import ontology.crypto.ECC;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by zx on 2018/2/1.
 */
public class Enrollment extends Transaction {
        public ECPoint pubKey;
        public Enrollment() {
            super(TransactionType.Enrollment);
        }
        @Override
        protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
            try {
                pubKey = ECC.secp256r1.getCurve().createPoint(
                        new BigInteger(1, reader.readVarBytes()), new BigInteger(1, reader.readVarBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
            writer.writeVarBytes(Helper.removePrevZero(pubKey.getXCoord().toBigInteger().toByteArray()));
            writer.writeVarBytes(Helper.removePrevZero(pubKey.getYCoord().toBigInteger().toByteArray()));
        }
}
