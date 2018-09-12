package com.github.ontio.common;

import org.junit.Test;

import java.math.BigInteger;

import static com.github.ontio.common.Helper.BigIntFromNeoBytes;
import static com.github.ontio.common.Helper.BigIntToNeoBytes;
import static org.junit.Assert.*;

public class HelperTest {

    @Test
    public void bigInt2Bytes() {
        assertArrayEquals(new byte[] {-85, -86, -86, -86, -86, -86, -85, -128}, BigIntToNeoBytes(new BigInteger("-9175052165852779861")));

        assertArrayEquals(new byte[] {85, 85, 85, 85, 85, 85, 84, 127}, BigIntToNeoBytes(new BigInteger("9175052165852779861")));

        assertArrayEquals(new byte[] {85, 85, 85, 85, 85, 85, 84, -128}, BigIntToNeoBytes(new BigInteger("-9199634313818843819")));

        assertArrayEquals(new byte[] {-85, -86, -86, -86, -86, -86, -85, 127}, BigIntToNeoBytes(new BigInteger("9199634313818843819")));

        assertArrayEquals(new byte[] {16, 31, -128}, BigIntToNeoBytes(new BigInteger("-8380656")));

        assertArrayEquals(new byte[] {-16, -32, 127}, BigIntToNeoBytes(new BigInteger("8380656")));

        assertArrayEquals(new byte[] {16, 31, 127, -1}, BigIntToNeoBytes(new BigInteger("-8446192")));

        assertArrayEquals(new byte[] {-16, -32, -128, 0}, BigIntToNeoBytes(new BigInteger("8446192")));

        assertArrayEquals(new byte[0], BigIntToNeoBytes(new BigInteger("-0")));

        assertArrayEquals(new byte[0], BigIntToNeoBytes(new BigInteger("0")));
    }

    @Test
    public void bytes2BigInt() {
        assertEquals(BigIntFromNeoBytes(new byte[] {-85, -86, -86, -86, -86, -86, -85, -128}), new BigInteger("-9175052165852779861"));

        assertEquals(BigIntFromNeoBytes(new byte[] {85, 85, 85, 85, 85, 85, 84, 127}), new BigInteger("9175052165852779861"));

        assertEquals(BigIntFromNeoBytes(new byte[] {85, 85, 85, 85, 85, 85, 84, -128}), new BigInteger("-9199634313818843819"));

        assertEquals(BigIntFromNeoBytes(new byte[] {-85, -86, -86, -86, -86, -86, -85, 127}), new BigInteger("9199634313818843819"));

        assertEquals(BigIntFromNeoBytes(new byte[] {16, 31, -128}), new BigInteger("-8380656"));

        assertEquals(BigIntFromNeoBytes(new byte[] {-16, -32, 127}), new BigInteger("8380656"));

        assertEquals(BigIntFromNeoBytes(new byte[] {16, 31, 127, -1}), new BigInteger("-8446192"));

        assertEquals(BigIntFromNeoBytes(new byte[] {-16, -32, -128, 0}), new BigInteger("8446192"));

        assertEquals(BigIntFromNeoBytes(new byte[0]), new BigInteger("0"));
    }
}