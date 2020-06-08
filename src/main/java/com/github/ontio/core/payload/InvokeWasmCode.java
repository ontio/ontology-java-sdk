package com.github.ontio.core.payload;

import com.github.ontio.common.Address;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

import java.io.IOException;

public class InvokeWasmCode extends Transaction {

    public byte[] invokeCode;

    public InvokeWasmCode() {
        super(TransactionType.InvokeWasmCode);
    }

    public InvokeWasmCode(byte[] invokeCode) {
        super(TransactionType.InvokeWasmCode);
        this.invokeCode = invokeCode;
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        try {
            invokeCode = reader.readVarBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(invokeCode);
    }
}
