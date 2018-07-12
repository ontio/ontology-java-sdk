package demo.neo;


import com.alibaba.fastjson.JSON;
import com.github.neo.core.*;
import com.github.neo.core.transaction.InvocationTransaction;
import com.github.neo.core.transaction.TransferTransaction;
import com.github.ontio.common.*;
import com.github.ontio.crypto.ECC;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.network.exception.RpcException;
import org.bouncycastle.math.ec.ECPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * neo sdk Demo
 */
public class DeployDemo {
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
    public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";
    public static String privatekey6 = "6c2c7eade4c5cb7c9d4d6d85bfda3da62aa358dd5b55de408d6a6947c18b9279";
    public static String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";
    public static String privatekey8 = "87a209d232d6b4f3edfcf5c34434aa56871c2cb204c263f6b891b95bc5837cac";
    public static String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";
    public static String privatekey10 = "e23237f33bf18ebed6fc648e765b56ed8ee9017fff1528f178fb9157284c153c";
    public static String contractAddr = "5bb169f915c916a5e30a3c13a5e0cd228ea26826";
    public static String nodeUrl = "http://seed2.neo.org:20332";
    public static String assetid = "c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b";

    public static void main(String[] args) throws Exception {
        System.out.println("Hi NEO, Nep-5 smartcontract invoke test!");
        com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), SignatureScheme.SHA256WITHECDSA);
        com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), SignatureScheme.SHA256WITHECDSA);
        com.github.ontio.account.Account acct3 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey3), SignatureScheme.SHA256WITHECDSA);
        com.github.ontio.account.Account acct4 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey4), SignatureScheme.SHA256WITHECDSA);
        com.github.ontio.account.Account acct5 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey5), SignatureScheme.SHA256WITHECDSA);
        com.github.ontio.account.Account acct6 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey6), SignatureScheme.SHA256WITHECDSA);
        com.github.ontio.account.Account acct7 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey7), SignatureScheme.SHA256WITHECDSA);
        Address multiSignAddr = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());
        System.out.println("acct1:" + acct1.getAddressU160().toBase58());
        System.out.println("acct2:" + acct2.getAddressU160().toBase58());
        System.out.println("acct3:" + acct3.getAddressU160().toBase58());
        System.out.println("acct4:" + acct4.getAddressU160().toBase58());
        System.out.println("acct5:" + acct5.getAddressU160().toBase58());
        System.out.println("acct6:" + acct6.getAddressU160().toBase58());
        System.out.println("acct7:" + acct7.getAddressU160().toBase58());
        System.out.println("multiSignAddr:" + multiSignAddr.toBase58());

        if (false) { //deploy
            //http://docs.neo.org/zh-cn/sc/reference/fw/dotnet/neo/Contract/Create.html

            String gasAsset = "602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7";
            com.github.ontio.account.Account senderAcct = acct1;
            Address senderAddr = acct1.getAddressU160();

            InvocationTransaction tx = new InvocationTransaction();
            byte[] params = Helper.hexToBytes("59c56b6144746b00617400936b766b94797451936c766b9479937400948c6c766b9472756203007400948c6c766b947961748c6c766b946d746c768c6b946d746c768c6b946d6c75666c766b00527ac40202026c766b51527ac4526c766b52527ac4006c766b53527ac412e58aa0e6b395e59088e7baa6e7a4bae4be8b6c766b54527ac401316c766b55527ac40563687269736c766b56527ac40d6368726973406162632e636f6d6c766b57527ac44c67e8bf99e698afe4b880e4b8aae58aa0e6b395e59088e7baa6efbc8ce4bca0e585a5e4b8a4e4b8aae695b4e59e8befbc8ce59088e7baa6e5afb9e4b8a4e4b8aae695b4e59e8be8bf9be8a18ce79bb8e58aa0efbc8ce8bf94e59b9ee4b880e4b8aae695b4e59e8b2e6c766b58527ac46c766b00c36c766b51c36c766b52c36c766b53c36c766b54c36c766b55c36c766b56c36c766b57c36c766b58c361587951795a727551727557795279597275527275567953795872755372755579547957727554727568134e656f2e436f6e74726163742e43726561746575616c7566");

            tx.script = params;
            tx.gas = new Fixed8(0);
            tx.version = 1;

            tx.attributes = new TransactionAttribute[1];
            tx.attributes[0] = new TransactionAttribute();
            tx.attributes[0].usage = TransactionAttributeUsage.DescriptionUrl;
            tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
//            tx.attributes[1] = new TransactionAttribute();
//            tx.attributes[1].usage = TransactionAttributeUsage.Script;
//            tx.attributes[1].data = (senderAcct.getAddressU160().toArray());

            tx.outputs = new TransactionOutput[0];
//            tx.outputs[0] = new TransactionOutput();
//            tx.outputs[0].assetId = UInt256.parse(gasAsset);
//            tx.outputs[0].value = Fixed8.parse(String.valueOf(10));
//            tx.outputs[0].scriptHash = senderAcct.getAddressU160();

            tx.inputs = new TransactionInput[1];
            TransactionInput txinput = new TransactionInput(UInt256.parse("7dc84f9a2b7871a0932b171bbfb7b8c65de6b64a12663f1ec97a0969535d6260"), 0);
            tx.inputs[0] = txinput;


            tx.scripts = new Program[1];
            tx.scripts[0] = new Program();
            tx.scripts[0].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(senderAcct, SignatureScheme.SHA256WITHECDSA)});
            tx.scripts[0].code = Program.ProgramFromPubKey(senderAcct.serializePublicKey());

            //send tx to neo node
            Object obj = sendRawTransaction(nodeUrl, tx.toHexString());
            System.out.println(tx.hash().toString());
            System.out.println(obj);
            System.exit(0);
        }

    }


    public static Object sendRawTransaction(String url, String sData) throws Exception {
        Object result = call(url, "sendrawtransaction", new Object[]{sData});
        return result;
    }

    public static Object getBalance(String url, String contractAddr, String addr) throws Exception {
        Object result = call(url, "getstorage", new Object[]{contractAddr, addr});
        return result;
    }

    public static Object call(String url, String method, Object... params) throws RpcException, IOException {
        Map req = makeRequest(method, params);
        Map response = (Map) send(url, req);
        System.out.println(JSON.toJSONString(response));
        if (response == null) {
            throw new RpcException(0, ErrorCode.ConnectUrlErr(url + "response is null. maybe is connect error"));
        } else if (response.get("result") != null) {
            return response.get("result");
        } else if (response.get("Result") != null) {
            return response.get("Result");
        } else if (response.get("error") != null) {
            throw new RpcException(0, JSON.toJSONString(response));
        } else {
            throw new IOException();
        }
    }

    private static Map makeRequest(String method, Object[] params) {
        Map request = new HashMap();
        request.put("jsonrpc", "2.0");
        request.put("method", method);
        request.put("params", params);
        request.put("id", 1);
        System.out.println(String.format("POST %s,%s", nodeUrl, JSON.toJSONString(request)));
        return request;
    }


    public static Object send(String url, Object request) throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            try (OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream())) {
                w.write(JSON.toJSONString(request));
            }
            try (InputStreamReader r = new InputStreamReader(connection.getInputStream())) {
                StringBuffer temp = new StringBuffer();
                int c = 0;
                while ((c = r.read()) != -1) {
                    temp.append((char) c);
                }
                //System.out.println("result:"+temp.toString());
                return JSON.parseObject(temp.toString(), Map.class);
            }
        } catch (IOException e) {
        }
        return null;
    }
}
