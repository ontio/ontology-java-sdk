package com.github.ontio.core.payload;

import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.io.BinaryReader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class InvokeWasmCodeTest {
    @Test
    public void deserialize() throws IOException {
        byte[] bs = Helper.hexToBytes("00d2757ecf5ef40100000000000000127a0000000000c849169e39c9cfac6d4a82a3b21e0eb8c566f6d7fda2025e0f5b1dcea427947f4e8f4824fb73908d5041b2fd8b020962617463685f61646414288fb05ca7a85bf4172f288a9f5f35ee0ca5abbe6e10117ae814cd20be059d8ac4d14c8fa03483fbd7e5b98d84cd8b6fda5d19ba09c5d212845fe67d0a5242a017d33f25f6e68ed510580a5938c8322792acf1040c143acb0f659c97f2220b5155f330f2770d2fb3b34cffea292ef8f2e1d72296a8fb0f58f18d215b5cda953646f24e54d749389e41b3774a7f06848c22804ffea65863c77622e8f13c3dc0feef8736a191fd1170b218a973a3745bf7e8e1021980a1f8cd2b37969d645cc894924a5c93c86896f2a68aae36a80e133fd0edebcf455ac21465ecea12557845823fab61b6c047a9bb4fa881ef20c44992666b5eb9245296b05a18a0c8f2d4b7433e90c34c2435c631814bcacb3cafc6ba7ba94409a2f257becd3549aa1e2fe441b28efae906a665c13c0753d09a2adeebb28b943dd971bebbaa5c585d7b83c617e5f61e4d6a00f2dd97e7991916a869ed9d148369a99dd73d648ef6e7793ea39615247ae2cdf5721f902f70df6d96c345ae8dba7e6c52aa1a5d367fd6c57412eb065fb8126bfdc65519bf2a49c5bb685cd318bccb3f05ef58e2ac2bdb0dc58f8079cfd710d6bf3aed6b9f3594695609245556c468da6a9db033856096cfdcb561a16f9954b40372c257e736118b94fd482df0ab65cbcbf309cc8e168789eedfbfe8fd722097f944e9928ae217e5af9734c7f8457117c6b8ee300dd772533fa5f94511dc34914100f8fa953246915f3a98c69e911b522e9adf9ac26295209aeb0efa831acfc21885a5480a0b71542167038871bc1851e506fdd1aca85b3a5d717606d7b479a2781b137cf1cf8cff9d45365015111d562dbcc975ad3f1edf4dce0b80ff8787d2915db41de8e86442485d8c4279711e5e8837d4ff76cf76430d503600014140fdb08e2548222f01cff1226fbf562efa5e88af988877e6a26494e6ba6606637c73802af2eb4b5c1b76ddeec88961d5aa31693ffafb6a2300c9b770f762f17a11232102795dd24e035bd3072708920e833654ccd672d4f646358939778b279cf84e6abaac");
        Transaction tx = Transaction.deserializeFrom(bs);
        System.out.println(tx.txType);
        System.out.println((InvokeWasmCode)tx);
    }
}