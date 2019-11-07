package com.github.ontio.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.math.BigInteger;

import static com.github.ontio.common.Helper.BigIntFromNeoBytes;
import static com.github.ontio.common.Helper.BigIntToNeoBytes;
import static com.github.ontio.common.Helper.parseBalancesArray;
import static org.junit.Assert.*;

public class HelperTest {
    @Test
    public void parseBalancesArrayWithNestedArrayAndEmptyBalances()
    {
        String jsonString = "[[\"6f555344\", \"\"], [\"6f4b4553\", \"\"], [\"6f425344\", \"\"]]";
        JSONArray jsonArray = JSONObject.parseArray(jsonString);

        String expectedJsonString = "[[\"6f555344\", \"0\"], [\"6f4b4553\", \"0\"], [\"6f425344\", \"0\"]]";
        JSONArray expected = JSONObject.parseArray(expectedJsonString);

        String resultJsonString = Helper.parseBalancesArray(jsonArray);
        JSONArray result = JSONObject.parseArray(resultJsonString);

        assertEquals(expected, result);
    }

    private String getHexBalance(String number)
    {
        return Helper.toHexString(Helper.BigIntToNeoBytes(new BigInteger(number)));
    }

    @Test
    public void parseBalancesArrayWithNestedArrayAndNonZeroBalances()
    {
        String firstBalance = getHexBalance("111");
        String secondBalance = getHexBalance("222");
        String thirdBalance = getHexBalance("333");

        String jsonStringToFormat = "[[\"6f555344\", \"%s\"], [\"6f4b4553\", \"%s\"], [\"6f425344\", \"%s\"]]";
        String jsonString = String.format(jsonStringToFormat, firstBalance, secondBalance, thirdBalance);
        JSONArray jsonArray = JSONObject.parseArray(jsonString);

        String expectedJsonString = "[[\"6f555344\", \"111\"], [\"6f4b4553\", \"222\"], [\"6f425344\", \"333\"]]";
        JSONArray expected = JSONObject.parseArray(expectedJsonString);

        String resultJsonString = Helper.parseBalancesArray(jsonArray);
        JSONArray result = JSONObject.parseArray(resultJsonString);

        assertEquals(expected, result);
    }

    private String getHexSymbol(String symbol)
    {
        return Helper.toHexString(symbol.getBytes());
    }

    @Test
    public void parseBalancesArrayEmptyBalances()
    {
        String jsonString = "[\"\", \"\", \"\"]";
        JSONArray jsonArray = JSONObject.parseArray(jsonString);

        String firstIndexHexSymbol = getHexSymbol("00");
        String secondIndexHexSymbol = getHexSymbol("01");
        String thirdIndexHexSymbol = getHexSymbol("02");

        String expectedJsonStringToFormat = "[[\"%s\", \"0\"], [\"%s\", \"0\"], [\"%s\", \"0\"]]";
        String expectedJsonString = String.format(expectedJsonStringToFormat, firstIndexHexSymbol, secondIndexHexSymbol,
                                                  thirdIndexHexSymbol);
        JSONArray expected = JSONObject.parseArray(expectedJsonString);

        String resultJsonString = Helper.parseBalancesArray(jsonArray);
        JSONArray result = JSONObject.parseArray(resultJsonString);

        assertEquals(expected, result);
    }

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