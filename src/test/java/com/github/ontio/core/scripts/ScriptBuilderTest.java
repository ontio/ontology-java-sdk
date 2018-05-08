package com.github.ontio.core.scripts;

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