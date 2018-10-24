package com.github.ontio.io;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;

import java.io.IOException;
import java.math.BigInteger;

public class utils {

    public static long readVarInt(BinaryReader reader) throws IOException {
        byte[] r = reader.readVarBytes();
        BigInteger b = Helper.BigIntFromNeoBytes(r);
        return b.longValue();
    }
    public static Address readAddress(BinaryReader reader) throws IOException {
        byte[] r = reader.readVarBytes();
        return new Address(r);
    }
}
