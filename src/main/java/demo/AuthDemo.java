package demo;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
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
            payer = ontSdk.getWalletMgr().createAccount(password);
            com.github.ontio.account.Account payerAcct = ontSdk.getWalletMgr().getAccount(payer.address,password);
            if(ontSdk.getWalletMgr().getIdentitys().size() < 3){
                Identity identity1 = ontSdk.getWalletMgr().importIdentity(acct0.exportCtrEncryptedPrikey(password,16384),password,acct0.getAddressU160().toBase58());
                Identity identity = ontSdk.getWalletMgr().createIdentity(password);

                ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.nativevm().ontId().sendRegister(identity1,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);
                ontSdk.nativevm().ontId().sendRegister(identity2,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.getWalletMgr().writeWallet();

                Thread.sleep(6000);
            }

            List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();

            System.out.println("ontid1:" +dids.get(0).ontid);
            System.out.println("ontid2:" +dids.get(1).ontid);
            System.out.println("ontid2:" +dids.get(2).ontid);

//            System.out.println("ddo1:" + ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid));
//            System.out.println("ddo2:" + ontSdk.nativevm().ontId().sendGetDDO(dids.get(1).ontid));
//            System.out.println("ddo3:" + ontSdk.nativevm().ontId().sendGetDDO(dids.get(2).ontid));

            String codeaddress = "805fb5252f20e474e386d44c6a9af79b7ad16891";

            //TMfz9VbV8QXAhR5kiPRWM2gmbeG26kR7G9
//            System.out.println(Address.parse("805fb5252f20e474e386d44c6a9af79b7ad16891").toBase58());
//            System.exit(0);
            String contractAddr = "805fb5252f20e474e386d44c6a9af79b7ad16891";


            if(true){

//                String txhash = ontSdk.nativevm().auth().sendInit(dids.get(0).ontid,password,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

//                String txhash = ontSdk.nativevm().auth().sendTransfer(dids.get(1).ontid,password,contractAddr,dids.get(0).ontid,1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = ontSdk.nativevm().auth().assignFuncsToRole(dids.get(0).ontid,password,contractAddr,"role",new String[]{"foo"},1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = ontSdk.nativevm().auth().assignOntIDsToRole(dids.get(0).ontid,password,contractAddr,"role",new String[]{dids.get(1).ontid},1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = ontSdk.nativevm().auth().delegate(dids.get(1).ontid,password,contractAddr,dids.get(2).ontid,"role",6000,1,1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//String txhash = ontSdk.nativevm().auth().withdraw(dids.get(1).ontid,password,contractAddr,dids.get(2).ontid,"role",1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
                String txhash = ontSdk.nativevm().auth().verifyToken(dids.get(2).ontid,password,contractAddr,"foo",1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object object = ontSdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(object);


//     String txhash2 = ontSdk.nativevm().auth().withdraw(dids.get(0).ontid,password,contractAddr,dids.get(1).ontid,"role",1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//                Thread.sleep(6000);
//                Object object2 = ontSdk.getConnect().getSmartCodeEvent(txhash2);
//                System.out.println(object2);
            }
            if(false){
                List list = new ArrayList<Object>();
                list.add("init".getBytes());
                List temp = new ArrayList();
                temp.add(acct0.getAddressU160().toBase58().getBytes());
                list.add(temp);
                byte[] params = BuildParams.createCodeParamsScript(list);

                ontSdk.vm().setCodeAddress(codeaddress);
                Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(codeaddress,null,params, VmType.NEOVM.value(), payer.address,10000000,0);
                ontSdk.signTx(tx,payer.address,password);
                boolean b = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
                if(b){
                    System.out.println(b);
                }
                Thread.sleep(6000);
                Object object = ontSdk.getConnect().getSmartCodeEvent(tx.hash().toHexString());
                System.out.println(object);
            }

            if(false){
                List list = new ArrayList<Object>();
                list.add("foo".getBytes());
                List tmp = new ArrayList<Object>();
                tmp.add(new Object[]{1,2});
                list.add(tmp);
                byte[] params = BuildParams.createCodeParamsScript(list);

                Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(codeaddress,null,params, VmType.NEOVM.value(), payer.address,ontSdk.DEFAULT_GAS_LIMIT,0);

                Object res = ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());

                System.out.println(((JSONObject)res).getString("Result"));

            }

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
