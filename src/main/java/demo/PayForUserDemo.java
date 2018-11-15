package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;

/**
 *
 *
 */
public class PayForUserDemo {
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
    public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";
    public static String privatekey6 = "6c2c7eade4c5cb7c9d4d6d85bfda3da62aa358dd5b55de408d6a6947c18b9279";
    public static String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";
    public static String privatekey8 = "87a209d232d6b4f3edfcf5c34434aa56871c2cb204c263f6b891b95bc5837cac";
    public static String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct3 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey3), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct4 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey4), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct5 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey5), ontSdk.defaultSignScheme);


            if(false){ //TODO How pay Fee for user?
                long gaslimit = 20000;
                long gasprice = 500;
                String recvAddr = acct3.getAddressU160().toBase58();
                com.github.ontio.account.Account sender = acct1;
                com.github.ontio.account.Account payerAcct = acct2;
                String oep4abi = "";
                AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
                AbiFunction func = abiinfo.getFunction("Transfer");
                func.name = "transfer";
                func.setParamsValue(sender.getAddressU160().toArray(), Address.decodeBase58(recvAddr).toArray(), 10);
                byte[] params = BuildParams.serializeAbiFunction(func);
                String payer = payerAcct.getAddressU160().toBase58();

                //TODO 1. make transaction and user signature in frontend.
                Transaction txSend = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(ontSdk.neovm().oep4().getContractAddress()), null, params, payer,gaslimit, gasprice);
                ontSdk.addSign(txSend,sender);

                //TODO 2. send data to backend
                String data = txSend.toHexString();

                //TODO 3. backend add payer signature and send transaction
                Transaction txRecv =  Transaction.deserializeFrom(Helper.hexToBytes(data));
                ontSdk.addSign(txRecv,payerAcct);
                Object obj = ontSdk.getConnect().sendRawTransactionPreExec(txRecv.toHexString());

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://polaris1.ont.io";
//        String ip = "http://dappnode1.ont.io";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("MutiSignDemo.json");
        return wm;
    }
}
