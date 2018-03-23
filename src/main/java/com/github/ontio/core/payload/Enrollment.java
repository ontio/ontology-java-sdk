package com.github.ontio.core.payload;

import com.github.ontio.common.Helper;
import com.github.ontio.core.TransactionType;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.core.Transaction;
import com.github.ontio.crypto.ECC;
import com.github.ontio.io.BinaryReader;
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
