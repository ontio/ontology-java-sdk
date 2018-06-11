package com.github.ontio.core.scripts;

import com.github.ontio.common.Helper;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class ScriptBuilderTest {

    ScriptBuilder scriptBuilder;

    @Before
    public void setUp(){
        scriptBuilder = new ScriptBuilder();
    }

    @Test
    public void add() {
        ScriptBuilder sb = scriptBuilder.add("test".getBytes());
        assertNotNull(sb);

    }

    @Test
    public void pushNum(){
        scriptBuilder.pushNum((short)17);
        byte[] aa = scriptBuilder.toArray();
        String bb = Helper.toHexString(aa);
        System.out.println(bb);

        ScriptBuilder scriptBuilder2 = new ScriptBuilder();
        scriptBuilder2.push(BigInteger.valueOf(17));
        byte[] aa2 = scriptBuilder2.toArray();
        String bb2 = Helper.toHexString(aa2);

    }

    @Test
    public void push() {
        ScriptBuilder sb = scriptBuilder.push(true);
        assertNotNull(sb);
        assertNotNull(scriptBuilder.push("test".getBytes()));
        assertNotNull(scriptBuilder.push(new BigInteger("11")));
    }


    @Test
    public void pushPack() {
        assertNotNull(scriptBuilder.pushPack());

    }
}