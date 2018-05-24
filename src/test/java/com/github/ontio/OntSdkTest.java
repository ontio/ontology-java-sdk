package com.github.ontio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OntSdkTest {
    private OntSdk ontSdk;
    public static String URL = "http://127.0.0.1:20334";
    public static String PRIVATEKEY = "54ca4db481966046b15f8d15ff433e611c49ab8e68a279ebf579e4cfd108196d";//有钱的账号的私钥
    public static String PASSWORD = "111111";//有钱账号的密码

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        OntSdk ontSdk = OntSdk.getInstance();
        assertNotNull(ontSdk);
        assertSame(ontSdk,this.ontSdk);
    }
}