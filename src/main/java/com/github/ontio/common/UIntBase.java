/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.common;

import java.io.IOException;
import java.nio.*;
import java.util.Arrays;

import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.io.*;

/**
 * Custom type base abstract class, it defines the storage and the serialization
 * and deserialization of actual data
 */
public abstract class UIntBase implements Serializable {
    protected byte[] data_bytes;

    protected UIntBase(int bytes, byte[] value) {
        if (value == null) {
            this.data_bytes = new byte[bytes];
            return;
        }
        if (value.length != bytes) {
            throw new IllegalArgumentException();
        }
        this.data_bytes = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UIntBase)) {
            return false;
        }
        UIntBase other = (UIntBase) obj;
        return Arrays.equals(this.data_bytes, other.data_bytes);
    }

    @Override
    public int hashCode() {
        return ByteBuffer.wrap(data_bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    @Override
    public byte[] toArray() {
        return data_bytes;
    }

    /**
     * 转为16进制字符串
     *
     * @return 返回16进制字符串
     */
    @Override
    public String toString() {
        return Helper.toHexString(data_bytes);
//        return Helper.toHexString(Helper.reverse(data_bytes));
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.write(data_bytes);
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        reader.read(data_bytes);
    }
}
