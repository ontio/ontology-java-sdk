package com.github.ontio.core.ontid;

import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;

public class Attribute implements Serializable {
    byte[] key;
    byte[] valueType;
    byte[] value;
    public Attribute(byte[] key,byte[] value,byte[] valueType){
        this.key = key;
        this.valueType = valueType;
        this.value = value;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.key = reader.readVarBytes();
        this.valueType = reader.readVarBytes();
        this.value = reader.readVarBytes();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(key);
        writer.writeVarBytes(valueType);
        writer.writeVarBytes(value);
    }
}
