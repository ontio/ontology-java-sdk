package com.github.ontio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OntSdkTest {
    private OntSdk ontSdk;

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