package com.github.ontio.core.scripts;

import com.github.ontio.common.Address;
import com.github.ontio.sdk.exception.SDKException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class WasmScriptBuilderTest {

    private WasmScriptBuilder builder;

    @Before
    public void setUp() {
        builder = new WasmScriptBuilder();
    }

    @Test
    public void pushBool() {
        List<Boolean> booleanList = Arrays.asList(true, false);
        List<String> targetList = Arrays.asList("01", "00");
        for (int i = 0; i < booleanList.size(); i++) {
            builder.push(booleanList.get(i));
            Assert.assertEquals(targetList.get(i), builder.toHexString());
            builder.reset();
        }
    }

    @Test
    public void pushInt() {
        List<Integer> intList = Arrays.asList(
                -2147483648,
                -32768,
                0,
                1,
                2,
                32768,
                2147483647
        );
        List<String> targetResult = Arrays.asList(
                "00000080ffffffffffffffffffffffff",
                "0080ffffffffffffffffffffffffffff",
                "00000000000000000000000000000000",
                "01000000000000000000000000000000",
                "02000000000000000000000000000000",
                "00800000000000000000000000000000",
                "ffffff7f000000000000000000000000"
        );
        for (int i = 0; i < intList.size(); i++) {
            builder.push(intList.get(i));
            Assert.assertEquals(targetResult.get(i), builder.toHexString());
            builder.reset();
        }
    }

    @Test
    public void pushByteArray() {
        List<byte[]> stringList = Arrays.asList(
                "Hello, world!".getBytes(),
                "Ontology".getBytes(),
                "!@#$%^&*()_+1234567890-=".getBytes(),
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".getBytes()
        );
        List<String> targetResult = Arrays.asList(
                "0d48656c6c6f2c20776f726c6421",
                "084f6e746f6c6f6779",
                "1821402324255e262a28295f2b313233343536373839302d3d",
                "3e313233343536373839304142434445464748494a4b4c4d4e4f505152535455565758595a6162636465666768696a6b6c6d6e6f707172737475767778797a"
        );
        for (int i = 0; i < stringList.size(); i++) {
            builder.push(stringList.get(i));
            Assert.assertEquals(targetResult.get(i), builder.toHexString());
            builder.reset();
        }
    }

    @Test
    public void pushLong() {
        List<Long> longList = Arrays.asList(
                -9223372036854775808L,
                -267L,
                -2L,
                0L,
                1L,
                2L,
                9007199254740993L,
                9223372036854775807L
        );
        List<String> targetResult = Arrays.asList(
                "0000000000000080ffffffffffffffff",
                "f5feffffffffffffffffffffffffffff",
                "feffffffffffffffffffffffffffffff",
                "00000000000000000000000000000000",
                "01000000000000000000000000000000",
                "02000000000000000000000000000000",
                "01000000000020000000000000000000",
                "ffffffffffffff7f0000000000000000"
        );
        for (int i = 0; i < longList.size(); i++) {
            builder.push(longList.get(i));
            Assert.assertEquals(targetResult.get(i), builder.toHexString());
            builder.reset();
        }
    }

    @Test
    public void pushBigInteger() {
        List<BigInteger> bigIntegerList = Arrays.asList(
                new BigInteger("2").pow(127).subtract(new BigInteger("1")),
                new BigInteger("2").pow(127).negate(),
                new BigInteger("267").negate(),
                new BigInteger("2").negate(),
                new BigInteger("0"),
                new BigInteger("1"),
                new BigInteger("2")
        );
        List<String> targetResult = Arrays.asList(
                "ffffffffffffffffffffffffffffff7f",
                "00000000000000000000000000000080",
                "f5feffffffffffffffffffffffffffff",
                "feffffffffffffffffffffffffffffff",
                "00000000000000000000000000000000",
                "01000000000000000000000000000000",
                "02000000000000000000000000000000"
        );
        for (int i = 0; i < bigIntegerList.size(); i++) {
            builder.push(bigIntegerList.get(i));
            Assert.assertEquals(targetResult.get(i), builder.toHexString());
            builder.reset();
        }
    }

    @Test
    public void pushStr() {
        List<String> stringList = Arrays.asList(
                "Hello, world!",
                "Ontology",
                "!@#$%^&*()_+1234567890-=",
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        );
        List<String> targetResult = Arrays.asList(
                "0d48656c6c6f2c20776f726c6421",
                "084f6e746f6c6f6779",
                "1821402324255e262a28295f2b313233343536373839302d3d",
                "3e313233343536373839304142434445464748494a4b4c4d4e4f505152535455565758595a6162636465666768696a6b6c6d6e6f707172737475767778797a"
        );
        for (int i = 0; i < stringList.size(); i++) {
            builder.push(stringList.get(i));
            Assert.assertEquals(targetResult.get(i), builder.toHexString());
            builder.reset();
        }
    }

    @Test
    public void pushAddress() throws SDKException {
        List<Address> addressList = Arrays.asList(
                Address.decodeBase58("AS7MjVEicEsJ4zjEfm2LoKoYoFsmapD7rT"),
                Address.decodeBase58("AFmseVrdL9f9oyCzZefL9tG6UbviEH9ugK"),
                Address.decodeBase58("Ad4pjz2bqep4RhQrUAzMuZJkBC3qJ1tZuT"),
                Address.decodeBase58("AK98G45DhmPXg4TFPG1KjftvkEaHbU8SHM")
        );
        List<String> targetResult = Arrays.asList(
                "71609b2c2f7b9447b089ad1da31586f42ca9eb10",
                "0000000000000000000000000000000000000007",
                "e98f4998d837fcdd44a50561f7f32140c7c6c260",
                "24ed4f965d3a5a76f5d0e87633c0b76941fc8827"
        );
        for (int i = 0; i < addressList.size(); i++) {
            builder.push(addressList.get(i));
            Assert.assertEquals(targetResult.get(i), builder.toHexString());
            builder.reset();
        }
    }

    @Test
    public void pushVarUInt() {
        List<Long> varUIntList = Arrays.asList(
                0L,
                1L,
                254L,
                65536L,
                65537L,
                4294967295L,
                4294967296L,
                1152921504606846975L
        );
        List<String> targetResult = Arrays.asList(
                "00",
                "01",
                "fdfe00",
                "fe00000100",
                "fe01000100",
                "feffffffff",
                "ff0000000001000000",
                "ffffffffffffffff0f",
                "ffffffffffffffffff",
                "ffffffffffffffffffff",
                "ffffffffffffffffffffffff"
        );
        for (int i = 0; i < varUIntList.size(); i++) {
            builder.pushVarUInt(varUIntList.get(i));
            Assert.assertEquals(targetResult.get(i), builder.toHexString());
            builder.reset();
        }
    }

    @Test
    public void pushWithoutLen() {
        List<Integer> lengthArray = Arrays.asList(1, 2, 4, 8, 16, 32, 64, 128, 256, 1024, 2048);
        lengthArray.forEach(length -> {
            byte[] val = new byte[length];
            Arrays.fill(val, (byte) 0xFF);
            builder.pushWithoutLen(val);
            Assert.assertEquals(length * 2, builder.toHexString().length());
            builder.reset();
        });
    }
}
