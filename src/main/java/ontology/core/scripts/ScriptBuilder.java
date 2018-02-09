package ontology.core.scripts;

import java.io.*;
import java.math.BigInteger;
import java.nio.*;

import ontology.common.UIntBase;

/**
 *  脚本生成器
 */
public class ScriptBuilder implements AutoCloseable {
    private ByteArrayOutputStream ms = new ByteArrayOutputStream();

    /**
     *  添加操作符
     *  <param name="op">操作符</param>
     *  <returns>返回添加操作符之后的脚本生成器</returns>
     */
    public ScriptBuilder add(ScriptOp op) {
        return add(op.getByte());
    }

    private ScriptBuilder add(byte op) {
        ms.write(op);
        return this;
    }

    /**
     *  添加一段脚本
     *  <param name="script">脚本</param>
     *  <returns>返回添加脚本之后的脚本生成器</returns>
     */
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
        if(b == true) {
            return add(ScriptOp.OP_1);
        }
        return add(ScriptOp.OP_0);
    }
    /**
     *  添加一段脚本，该脚本的作用是将一个整数压入栈中
     *  <param name="number">要压入栈中的整数</param>
     *  <returns>返回添加脚本之后的脚本生成器</returns>
     */
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
        return push(number.toByteArray());
    }

    /**
     *  添加一段脚本，该脚本的作用是将一个字节数组压入栈中
     *  <param name="data">要压入栈中的字节数组</param>
     *  <returns>返回添加脚本之后的脚本生成器</returns>
     */
    public ScriptBuilder push(byte[] data) {
        if (data == null) {
        	throw new NullPointerException();
        }
        if (data.length <= (int)ScriptOp.OP_PUSHBYTES75.getByte()) {
            ms.write((byte)data.length);
            ms.write(data, 0, data.length);
        } else if (data.length < 0x100) {
            add(ScriptOp.OP_PUSHDATA1);
            ms.write((byte)data.length);
            ms.write(data, 0, data.length);
        } else if (data.length < 0x10000) {
            add(ScriptOp.OP_PUSHDATA2);
			ms.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short)data.length).array(), 0, 2);
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

    /**
     *  添加一段脚本，该脚本的作用是将一个散列值压入栈中
     *  <param name="hash">要压入栈中的散列值</param>
     *  <returns>返回添加脚本之后的脚本生成器</returns>
     */
    public ScriptBuilder push(UIntBase hash) {
        return push(hash.toArray());
    }

    public ScriptBuilder pushPack() {
        return add(ScriptOp.OP_PACK);
    }
    /**
     *  获取脚本生成器中包含的脚本代码
     */
    public byte[] toArray() {
        return ms.toByteArray();
    }
}
