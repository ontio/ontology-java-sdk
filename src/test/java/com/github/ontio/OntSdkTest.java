package com.github.ontio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OntSdkTest {
    private OntSdk ontSdk;
    public static String URL = "http://polaris1.ont.io:20334";
    public static String PRIVATEKEY = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";//有钱的账号的私钥
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