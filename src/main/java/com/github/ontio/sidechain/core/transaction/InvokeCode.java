package com.github.ontio.sidechain.core.transaction;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InvokeCode extends Transaction {
    public byte[] code;

    public InvokeCode() {
        super(TransactionType.InvokeCode);
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        try {
            code = reader.readVarBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(code);
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }

    @Override
    public Object json() {
        Map obj = (Map) super.json();
        Map payload = new HashMap();
        payload.put("Code", Helper.toHexString(code));
        obj.put("Payload", payload);
        return obj;
    }
}
