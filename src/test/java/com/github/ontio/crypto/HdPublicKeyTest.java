package com.github.ontio.crypto;

import com.github.ontio.crypto.bip32.HdPublicKey;
import org.junit.Assert;
import org.junit.Test;

public class HdPublicKeyTest {
    @Test
    public void TestBase58() {


        HdPublicKey masterPubKey = HdPublicKey.base58Decode("xpub661MyMwAqRbcFipovJxHFBNJ9YZDn4NdPVcf6bFnwqo5RwgGAf1yEcGVRVwNmhBRmcvzforP5aWQefsNveyCxPdJ4L6oydpm9XLZ7PLCBXd");
        Assert.assertEquals("03908a0ce4ccc0e706e09905db4f04eb150bc42fb6cd96a7a2c8396873fa973262", masterPubKey.toHexString());

    }
}
