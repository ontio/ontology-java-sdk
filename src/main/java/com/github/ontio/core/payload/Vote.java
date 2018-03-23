package com.github.ontio.core.payload;

import com.github.ontio.common.Helper;
import com.github.ontio.common.Address;
import com.github.ontio.core.TransactionType;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.core.Transaction;
import com.github.ontio.crypto.ECC;
import com.github.ontio.io.BinaryReader;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;

/**
 *  投票信息
 */
public class Vote extends Transaction {
    public ECPoint[] pubKeys;
    public Address account;
    public Vote() {
        super(TransactionType.Vote);
    }
    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        try {
            int len = reader.readInt();
            pubKeys = new ECPoint[len];
            for (int i = 0; i < len; i++) {
                pubKeys[i] = ECC.secp256r1.getCurve().createPoint(
                        new BigInteger(1, reader.readVarBytes()), new BigInteger(1, reader.readVarBytes()));
            }
            account = reader.readSerializable(Address.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeInt(pubKeys.length);
        for(ECPoint pubkey:pubKeys) {
            writer.writeVarBytes(Helper.removePrevZero(pubkey.getXCoord().toBigInteger().toByteArray()));
            writer.writeVarBytes(Helper.removePrevZero(pubkey.getYCoord().toBigInteger().toByteArray()));
        }
        writer.writeSerializable(account);
    }
}
