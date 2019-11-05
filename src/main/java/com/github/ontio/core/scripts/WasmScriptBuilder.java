package com.github.ontio.core.scripts;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.exception.SDKRuntimeException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

public class WasmScriptBuilder implements AutoCloseable{

    private ByteArrayOutputStream ms;

    private static BigInteger maxInt128 = new BigInteger("2").pow(127).subtract(BigInteger.ONE);

    private static BigInteger minInt128 = new BigInteger("2").pow(127).negate();

    WasmScriptBuilder() {
        this.ms = new ByteArrayOutputStream();
    }

    @Override
    public void close() {
        try {
            ms.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    void reset() {
        ms.reset();
    }

    public static byte[] createWasmInvokeCode(String contractHash, String method, List<Object> params) {
        try {
            WasmScriptBuilder headBuilder = new WasmScriptBuilder();
            headBuilder.pushWithoutLen(Helper.reverse(Helper.hexToBytes(contractHash)));
            return Helper.addBytes(headBuilder.toByteArray(), createWasmCodeParamsScript(method, params));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SDKRuntimeException("Create wasm invoke code failed");
        }
    }

    private static byte[] createWasmCodeParamsScript(String method, List<Object> list) {
        WasmScriptBuilder builder = new WasmScriptBuilder();
        builder.push(method);
        list.forEach(builder::pushVmParam);
        return builder.packAsArray();
    }

    private void pushVmParam(Object val) {
        if (val instanceof byte[]) {
            push((byte[]) val);
        } else if (val instanceof Address) {
            push(((Address) val));
        } else if (val instanceof String) {
            push((String) val);
        } else if (val instanceof Boolean) {
            push((Boolean) val);
        } else if (val instanceof Integer) {
            push((Integer) val);
        } else if (val instanceof Long) {
            push((Long) val);
        } else if (val instanceof List) {
            List listVal = (List) val;
            pushVarUInt((long) listVal.size());
            for (Object param : listVal) {
                pushVmParam(param);
            }
        } else {
            throw new SDKRuntimeException("Invalid data");
        }
    }

    public byte[] toByteArray() {
        return ms.toByteArray();
    }

    public String toHexString() {
        return Helper.toHexString(toByteArray());
    }

    private byte[] packAsArray() {
        WasmScriptBuilder finalBuilder = new WasmScriptBuilder();
        finalBuilder.push( this.toByteArray());
        return finalBuilder.toByteArray();
    }

    void push(Boolean val) {
        if (val) {
            ms.write(0x01);
        } else {
            ms.write(0x00);
        }
    }

    void pushWithoutLen(byte[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        ms.write(data, 0, data.length);
    }

    void push(byte[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        pushVarUInt((long) data.length);
        ms.write(data, 0, data.length);
    }

    void push(Address val) {
        byte[] byteVal = val.toArray();
        ms.write(byteVal, 0, byteVal.length);
    }

    void push(String data) {
        if (data == null) {
            throw new NullPointerException();
        }
        push(data.getBytes());
    }

    void push(Integer data) {
        if (data == null) {
            throw new SDKRuntimeException("NULL Pointer exception");
        }
        push(BigInteger.valueOf(data));
    }

    void push(Long data) {
        if (data == null) {
            throw new SDKRuntimeException("NULL Pointer exception");
        }
        push(BigInteger.valueOf(data));
    }

    void push(BigInteger data) {
        if (data == null) {
            throw new SDKRuntimeException("NULL Pointer exception");
        }
        if (data.compareTo(minInt128) < 0 || data.compareTo(maxInt128) > 0) {
            throw new SDKRuntimeException("Out of range");
        }
        byte[] bytesData = Helper.reverse(data.toByteArray());
        int int128Length = 16;
        if (bytesData.length > int128Length) {
            throw new SDKRuntimeException("Data in bytes should be less equal then 16");
        }
        byte[] finalResult = new byte[int128Length];
        if (data.compareTo(BigInteger.ZERO) < 0) {
            Arrays.fill(finalResult, (byte) 0xFF);
        }
        System.arraycopy(bytesData, 0, finalResult, 0, bytesData.length);
        ms.write(finalResult, 0, finalResult.length);
    }

    void pushVarUInt(Long data) {
        if (data == null) {
            throw new NullPointerException();
        }
        if (data < 0) {
            throw new RuntimeException();
        } else if (data < 0XFD) {
            byte[] bytes = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(data).array();
            ms.write(bytes, 0, 1);
        } else if (data <= 0xFFFF) {
            byte[] bytes = Helper.addBytes(new byte[]{(byte) 0xFD}, ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(data).array());
            ms.write(bytes, 0, 3);
        } else if (data <= 0xFFFFFFFFL) {
            byte[] bytes = Helper.addBytes(new byte[]{(byte) 0xFE}, ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(data).array());
            ms.write(bytes, 0, 5);
        } else {
            byte[] bytes = Helper.addBytes(new byte[]{(byte) 0xFF}, ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(data).array());
            ms.write(bytes, 0, 9);
        }
    }

}
