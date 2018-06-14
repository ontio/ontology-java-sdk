package demo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;

import java.util.ArrayList;
import java.util.List;

public class AuthDemo {

    public static void main(String[] args){
        OntSdk ontSdk;
        String password = "111111";
        String abi = "{\"hash\":\"0x4d0d780599010f943c37c795a22f6161d49436cf\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"token\",\"type\":\"Array\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"foo\",\"parameters\":[{\"name\":\"operation\",\"type\":\"ByteArray\"},{\"name\":\"token\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"init\",\"parameters\":[],\"returntype\":\"Boolean\"}],\"events\":[]}";
        Account payer;
        try {
            ontSdk = getOntSdk();
//            System.out.println(Helper.toHexString("initContractAdmin".getBytes()));
//            System.exit(0);
            // 8007c33f29a892e3a36e2cfec657eff1d7431e8f
            String privatekey0 = "523c5fcf74823831756f0bcb3634234f10b3beb1c05595058534577752ad2d9f";
            String privatekey1 ="83614c773f668a531132e765b5862215741c9148e7b2f9d386b667e4fbd93e39";
            com.github.ontio.account.Account acct0 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0), ontSdk.defaultSignScheme);

            com.github.ontio.account.Account account = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);

            payer = ontSdk.getWalletMgr().createAccount(password);
            com.github.ontio.account.Account payerAcct = ontSdk.getWalletMgr().getAccount(payer.address,password,payer.getSalt());
            Identity identity = null;
            Identity identity2 = null;
            Identity identity3 = null;
            List<Identity> dids = ontSdk.getWalletMgr().getWallet().getIdentities();
            if(ontSdk.getWalletMgr().getWallet().getIdentities().size() < 3){
               // Identity identity1 = ontSdk.getWalletMgr().importIdentity("",password,"".getBytes(),acct0.getAddressU160().toBase58());
                identity = ontSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey0);

                ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
              //  ontSdk.nativevm().ontId().sendRegister(identity1,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                identity2 = ontSdk.getWalletMgr().createIdentity(password);
                ontSdk.nativevm().ontId().sendRegister(identity2,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);

                identity3 = ontSdk.getWalletMgr().createIdentity(password);
                ontSdk.nativevm().ontId().sendRegister(identity3,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);

                ontSdk.getWalletMgr().writeWallet();

                Thread.sleep(6000);
            }else {
                identity = ontSdk.getWalletMgr().getWallet().getIdentity(dids.get(0).ontid);
                identity2 = ontSdk.getWalletMgr().getWallet().getIdentity(dids.get(1).ontid);
                identity3 = ontSdk.getWalletMgr().getWallet().getIdentity(dids.get(2).ontid);
            }



            System.out.println("ontid1:" +dids.get(0).ontid+" "+Helper.toHexString(dids.get(0).ontid.getBytes()));
            System.out.println("ontid2:" +dids.get(1).ontid);
            System.out.println("ontid3:" +dids.get(2).ontid);
            Account account1 = ontSdk.getWalletMgr().createAccount(password);
            System.out.println("####" + account1.address);

//            System.out.println("ddo1:" + ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid));
//            System.out.println("ddo2:" + ontSdk.nativevm().ontId().sendGetDDO(dids.get(1).ontid));
//            System.out.println("ddo3:" + ontSdk.nativevm().ontId().sendGetDDO(dids.get(2).ontid));

            String contractAddr = "b93f1d81a00f95d09228f1f8934a71dd0e89999f";

            if(false){
                 identity = ontSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey1);
                ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                System.out.println(Helper.toHexString(identity.ontid.getBytes()));

            }
            //Identity identity = ontSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey1);
            System.out.println(account.getAddressU160().toBase58());
            System.out.println(identity.ontid);
            System.out.println(Helper.toHexString(account.getAddressU160().toArray()));
            System.out.println(Helper.toHexString(identity.ontid.getBytes()));

            if(false){
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "init";
                AbiFunction func = abiinfo.getFunction(name);
                func.name = name;
                System.out.println(func);
                func.setParamsValue();
                //Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(contractAddr),null,null,0,0,func, true);
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(contractAddr),acct0,acct0,30000,0,func, false);
                System.out.println(obj);
            }

            if(false){

//                String txhash = ontSdk.nativevm().auth().sendInit(dids.get(0).ontid,password,codeaddress,account,ontSdk.DEFAULT_GAS_LIMIT,0);

                //String txhash = ontSdk.nativevm().auth().sendTransfer(identity.ontid,password,identity.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),identity2.ontid,account,ontSdk.DEFAULT_GAS_LIMIT,0);

              //  String txhash = ontSdk.nativevm().auth().assignFuncsToRole(identity2.ontid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),"role",new String[]{"foo"},account,ontSdk.DEFAULT_GAS_LIMIT,0);
                String txhash = ontSdk.nativevm().auth().assignOntIdsToRole(identity2.ontid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),"role",new String[]{identity2.ontid},account,ontSdk.DEFAULT_GAS_LIMIT,0);
                //String txhash = ontSdk.nativevm().auth().delegate(identity2.ontid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),identity3.ontid,"role",6000,1,account,ontSdk.DEFAULT_GAS_LIMIT,0);
               //String txhash = ontSdk.nativevm().auth().withdraw(identity2.ontid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),identity3.ontid,"role",account,ontSdk.DEFAULT_GAS_LIMIT,0);
              //  String txhash = ontSdk.nativevm().auth().verifyToken(identity2.ontid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),"foo");
//                Thread.sleep(6000);
//                Object object = ontSdk.getConnect().getSmartCodeEvent(txhash);
//                System.out.println(object);


//     String txhash2 = ontSdk.nativevm().auth().withdraw(dids.get(0).ontid,password,contractAddr,dids.get(1).ontid,"role",1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object object2 = ontSdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(object2);
            }
            if(true){
                ontSdk.nativevm().auth().queryAuth(contractAddr,identity2.ontid);
            }
            if(false){
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "foo";
                AbiFunction func = abiinfo.getFunction(name);
                func.name = name;
                System.out.println(func);
                func.setParamsValue(identity2.ontid.getBytes(),Long.valueOf(1));

                acct0 = ontSdk.getWalletMgr().getAccount(identity2.ontid,password,identity2.controls.get(0).getSalt());
                System.out.println("pk:"+Helper.toHexString(acct0.serializePublicKey()));
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(contractAddr),acct0,account,30000,0,func, true);
                System.out.println(obj);
            }

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
        String wsUrl = ip + ":" + "20335";

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
