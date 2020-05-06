package com.github.ontio.core.ontid;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

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
        BigInteger th = BigInteger.valueOf(this.members.length);
        writer.writeVarBytes(Helper.BigIntToNeoBytes(th));
        for (Object obj : this.members) {
            if (obj instanceof byte[]) {
                writer.writeVarBytes((byte[])obj);
            } else if (obj instanceof Group) {
                BinaryWriter writer = new BinaryWriter();
                ((Group) obj).serialize(writer);
                writer.writeVarBytes(writer);
            }
        }
    }
}

