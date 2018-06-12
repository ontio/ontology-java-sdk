package demo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.abi.AbiInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.neovm.BuildParams;

import java.util.ArrayList;
import java.util.List;

public class AuthDemo {

    public static void main(String[] args){
        OntSdk ontSdk;
        String password = "111111";
        String abi = "{\"hash\":\"0x4d0d780599010f943c37c795a22f6161d49436cf\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"token\",\"type\":\"Array\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"foo\",\"parameters\":[{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Boolean\"},{\"name\":\"init\",\"parameters\":[],\"returntype\":\"Boolean\"}],\"events\":[]}";
        Account payer;
        try {
            ontSdk = getOntSdk();
            // 8007c33f29a892e3a36e2cfec657eff1d7431e8f
            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            String privatekey1 ="83614c773f668a531132e765b5862215741c9148e7b2f9d386b667e4fbd93e39";
            com.github.ontio.account.Account acct0 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0), ontSdk.defaultSignScheme);

            com.github.ontio.account.Account account = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);

            payer = ontSdk.getWalletMgr().createAccount(password);
            com.github.ontio.account.Account payerAcct = ontSdk.getWalletMgr().getAccount(payer.address,password,new byte[]{});
            if(ontSdk.getWalletMgr().getIdentitys().size() < 3){
                Identity identity1 = ontSdk.getWalletMgr().importIdentity(acct0.exportCtrEncryptedPrikey(password,16384),password,new byte[]{},acct0.getAddressU160().toBase58());
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
            System.out.println("ontid3:" +dids.get(2).ontid);
            Account account1 = ontSdk.getWalletMgr().createAccount(password);
            System.out.println("####" + account1.address);

//            System.out.println("ddo1:" + ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid));
//            System.out.println("ddo2:" + ontSdk.nativevm().ontId().sendGetDDO(dids.get(1).ontid));
//            System.out.println("ddo3:" + ontSdk.nativevm().ontId().sendGetDDO(dids.get(2).ontid));

            String contractAddr = "4d0d780599010f943c37c795a22f6161d49436cf";

            if(false){
                Identity identity = ontSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey1);
                ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                System.out.println(Helper.toHexString(identity.ontid.getBytes()));

            }
            Identity identity = ontSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey1);
            System.out.println(account.getAddressU160().toBase58());
            System.out.println(identity.ontid);
            System.out.println(Helper.toHexString(account.getAddressU160().toArray()));
            System.out.println(Helper.toHexString(identity.ontid.getBytes()));

            if(true){
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "init";
                AbiFunction func = abiinfo.getFunction(name);
                func.name = name;
                System.out.println(func);
                func.setParamsValue();
                System.out.println("hello:"+Helper.toHexString("hello".getBytes()));
                System.out.println("world:"+Helper.toHexString("world".getBytes()));
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("7aa897563a10b5aaa42039f39e3be6d824babddf"),null,null,0,0,func, true);

            }
            if(false){

//                String txhash = ontSdk.nativevm().auth().sendInit(dids.get(0).ontid,password,codeaddress,account,ontSdk.DEFAULT_GAS_LIMIT,0);

//                String txhash = ontSdk.nativevm().auth().sendTransfer(identity.ontid,password,contractAddr,dids.get(0).ontid,1,account,ontSdk.DEFAULT_GAS_LIMIT,0);
                String txhash = ontSdk.nativevm().auth().assignFuncsToRole(dids.get(0).ontid,password,contractAddr,"role",new String[]{"foo"},1,account,ontSdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = ontSdk.nativevm().auth().assignOntIDsToRole(dids.get(0).ontid,password,contractAddr,"role",new String[]{dids.get(1).ontid},1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = ontSdk.nativevm().auth().delegate(dids.get(1).ontid,password,contractAddr,dids.get(2).ontid,"role",6000,1,1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//String txhash = ontSdk.nativevm().auth().withdraw(dids.get(1).ontid,password,contractAddr,dids.get(2).ontid,"role",1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

                String txhash = ontSdk.nativevm().auth().verifyToken(dids.get(2).ontid,password,new byte[]{},codeaddress,"foo",1,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object object = ontSdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(object);


//     String txhash2 = ontSdk.nativevm().auth().withdraw(dids.get(0).ontid,password,contractAddr,dids.get(1).ontid,"role",1,payer.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//                Thread.sleep(6000);
//                Object object2 = ontSdk.getConnect().getSmartCodeEvent(txhash2);
//                System.out.println(object2);
            }
            if(true){
                List list = new ArrayList<Object>();
                list.add("init".getBytes());
                List temp = new ArrayList();
                temp.add(acct0.getAddressU160().toBase58().getBytes());
                list.add(temp);
                byte[] params = BuildParams.createCodeParamsScript(list);

                ontSdk.vm().setCodeAddress(codeaddress);
                Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(codeaddress,null,params,payer.address,1000000000,0);
                ontSdk.signTx(tx,payer.address,password,new byte[]{});
                boolean b = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
                if(b){
                    System.out.println(b);
                }

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
