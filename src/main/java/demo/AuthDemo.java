package demo;


import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.neovm.BuildParams;

import java.util.ArrayList;
import java.util.List;

public class AuthDemo {

    public static void main(String[] args){
        OntSdk ontSdk;
        String password = "111111";
        Account payer;
        try {
            ontSdk = getOntSdk();

            // 8007c33f29a892e3a36e2cfec657eff1d7431e8f
            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            com.github.ontio.account.Account acct0 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0), ontSdk.signatureScheme);


            System.out.println(acct0.getAddressU160().toBase58().getBytes());

            Identity identity = ontSdk.getWalletMgr().createIdentity(password);
            payer = ontSdk.getWalletMgr().createAccount(password);

            List list = new ArrayList<Object>();
            list.add("foo".getBytes());
            List tmp = new ArrayList<Object>();
            tmp.add(new Object[]{1,2});
            list.add(tmp);
            byte[] params = BuildParams.createCodeParamsScript(list);
            String codeaddress = "8007c33f29a892e3a36e2cfec657eff1d7431e8f";
            Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(codeaddress,null,params, VmType.NEOVM.value(), payer.address,ontSdk.DEFAULT_GAS_LIMIT,0);

            Object res = ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());

            System.out.println(((JSONObject)res).getString("Result"));


//            ontSdk.nativevm().auth().sendTransfer(Common.didont+acct0.getAddressU160().toBase58(),password,"8007c33f29a892e3a36e2cfec657eff1d7431e8f",
//                    identity.ontid,1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static OntSdk getOntSdk() throws Exception {

//        String ip = "http://polaris1.ont.io";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String ip = "http://127.0.0.1";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRestful(restUrl);
//        wm.setRestful("http://polaris1.ont.io:20334");
//        wm.setRestful("http://192.168.50.121:9099");
        //
        wm.setDefaultConnect(wm.getRestful());
        wm.openWalletFile("AuthDemo.json");
        return wm;
    }
}
