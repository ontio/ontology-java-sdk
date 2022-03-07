package com.github.ontio.core.payload;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;
import org.ethereum.util.RLP;
import org.spongycastle.util.BigIntegers;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class EIP155 extends Transaction {
    public org.ethereum.core.Transaction code;

    public EIP155() {
        super(TransactionType.EIP155);
    }

    public EIP155(org.ethereum.core.Transaction code) {
        super(TransactionType.EIP155);
        this.code = code;
    }

    public static Transaction deserializeEIP155(BinaryReader reader) throws IOException {
        try {
            byte[] data = reader.readVarBytes();
            org.ethereum.core.Transaction code = new org.ethereum.core.Transaction(data);
            code.rlpParse();
            byte[] sender = code.getSender();
            Transaction transaction = new EIP155(code);
            transaction.nonce = BigIntegers.fromUnsignedByteArray(code.getNonce()).intValue();
            transaction.version = 0;
            transaction.gasPrice = BigIntegers.fromUnsignedByteArray(code.getGasPrice()).longValue();
            transaction.gasLimit = BigIntegers.fromUnsignedByteArray(code.getGasLimit()).longValue();
            transaction.payer = new Address(sender);
            transaction.attributes = new Attribute[]{};
            transaction.sigs = new Sig[]{};
            return transaction;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        throw new IOException("can not serialize");
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }

    @Override
    public Object json() {
        Map obj = (Map) super.json();
        obj.put("Payload", Helper.toHexString(code.getEncoded()));
        return obj;
    }

    public UInt256 hash() {
        return new UInt256(this.code.getHash());
    }
}
