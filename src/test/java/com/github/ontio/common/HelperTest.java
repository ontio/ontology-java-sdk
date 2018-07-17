package com.github.ontio.common;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class HelperTest {

    @Test
    public void bigInt2Bytes() {
        BigInteger bigInteger = BigInteger.valueOf(1000000000000L);
        String aa = Helper.toHexString(Helper.BigIntToNeoBytes(bigInteger));
        System.out.println(aa);
        BigInteger bb = Helper.BigIntFromNeoBytes(Helper.hexToBytes(aa));
        assertTrue(bigInteger.equals(bb));

    }
}