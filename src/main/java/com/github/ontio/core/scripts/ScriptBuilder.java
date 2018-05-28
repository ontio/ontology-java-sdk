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

package com.github.ontio.core.scripts;

import java.io.*;
import java.math.BigInteger;
import java.nio.*;

import com.github.ontio.common.UIntBase;

/**
 */
public class ScriptBuilder implements AutoCloseable {
    private ByteArrayOutputStream ms = new ByteArrayOutputStream();

    public ScriptBuilder add(ScriptOp op) {
        return add(op.getByte());
    }

    private ScriptBuilder add(byte op) {
        ms.write(op);
        return this;
    }

    public ScriptBuilder add(byte[] script) {
        ms.write(script, 0, script.length);
        return this;
    }

    @Override
    public void close() {
        try {
            ms.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ScriptBuilder push(boolean b) {
        if (b == true) {
            return add(ScriptOp.OP_1);
        }
        return add(ScriptOp.OP_0);
    }

    public ScriptBuilder push(BigInteger number) {
        if (number.equals(BigInteger.ONE.negate())) {
            return add(ScriptOp.OP_1NEGATE);
        }
        if (number.equals(BigInteger.ZERO)) {
            return add(ScriptOp.OP_0);
        }
        if (number.compareTo(BigInteger.ZERO) > 0 && number.compareTo(BigInteger.valueOf(16)) <= 0) {
            return add((byte) (ScriptOp.OP_1.getByte() - 1 + number.byteValue()));
        }
        if (number.longValue() < 0 || number.longValue() >= Long.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putLong(number.longValue());
        return push(byteBuffer.array());
    }

    public ScriptBuilder push(byte[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        if (data.length <= (int) ScriptOp.OP_PUSHBYTES75.getByte()) {
            ms.write((byte) data.length);
            ms.write(data, 0, data.length);
        } else if (data.length < 0x100) {
            add(ScriptOp.OP_PUSHDATA1);
            ms.write((byte) data.length);
            ms.write(data, 0, data.length);
        } else if (data.length < 0x10000) {
            add(ScriptOp.OP_PUSHDATA2);
            ms.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) data.length).array(), 0, 2);
            ms.write(data, 0, data.length);
        } else if (data.length < 0x100000000L) {
            add(ScriptOp.OP_PUSHDATA4);
            ms.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data.length).array(), 0, 4);
            ms.write(data, 0, data.length);
        } else {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public ScriptBuilder push(UIntBase hash) {
        return push(hash.toArray());
    }

    public ScriptBuilder pushPack() {
        return add(ScriptOp.OP_PACK);
    }

    public byte[] toArray() {
        return ms.toByteArray();
    }
}
