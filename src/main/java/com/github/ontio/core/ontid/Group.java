package com.github.ontio.core.ontid;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class Group implements Serializable {
    Object[] members;
    int threshold;


    public Group(Object[] members, int threshold) {
        this.members = members;
        this.threshold = threshold;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        BigInteger l = BigInteger.valueOf(this.members.length);
        writer.writeVarBytes(Helper.BigIntToNeoBytes(l));
        for (Object obj : this.members) {
            if (obj instanceof byte[]) {
                writer.writeVarBytes((byte[])obj);
            } else if (obj instanceof Group) {
                ByteArrayOutputStream ms = new ByteArrayOutputStream();
                BinaryWriter writer2 = new BinaryWriter(ms);
                ((Group) obj).serialize(writer2);
                writer.writeVarBytes(ms.toByteArray());
            }
        }
        BigInteger th = BigInteger.valueOf(this.threshold);
        writer.writeVarBytes(Helper.BigIntToNeoBytes(th));
    }
}

