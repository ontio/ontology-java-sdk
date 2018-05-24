package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeOntIdDemo {

    public static void main(String[] args) {

        String password = "111111";

        try {
            OntSdk ontSdk = getOntSdk();

            Account payerAcc = ontSdk.getWalletMgr().createAccount(password);



//            Identity iden = ontSdk.getWalletMgr().createIdentity(password);
//            ontSdk.nativevm().ontId().sendRegister(iden,password,payerAcc.address,password,0);

//            Transaction tx22 = ontSdk.nativevm().ontId().makeRegister(iden.ontid,password,payerAcc.address,0);
//            ontSdk.signTx(tx22,iden.ontid.replace(Common.didont,""),password);
//            ontSdk.addSign(tx22,payerAcc.address,password);
//            ontSdk.getConnectMgr().sendRawTransaction(tx22);
//
//            Thread.sleep(6000);
//
//            System.out.println(payerAcc.address);
//            String ddon = ontSdk.nativevm().ontId().sendGetDDO(iden.ontid);
//            System.out.println("ddon:" + ddon);
//
//            System.exit(0);

            if(ontSdk.getWalletMgr().getIdentitys().size() < 2){
                Identity identity = ontSdk.getWalletMgr().createIdentity(password);
                Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,payerAcc.address,0,0);
                ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
                ontSdk.addSign(tx,payerAcc.address,password);
                ontSdk.getConnect().sendRawTransaction(tx);

                Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);
                ontSdk.nativevm().ontId().sendRegister(identity2,password,payerAcc.address,password,0,0);

                ontSdk.getWalletMgr().writeWallet();
                Thread.sleep(6000);
            }

//            System.exit(0);
            List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();

            System.out.println("dids.get(0).ontid:" + dids.get(0).ontid);
            System.out.println("dids.get(1).ontid:" + dids.get(1).ontid);

            String ddo1 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid);
            String publicKeys = ontSdk.nativevm().ontId().sendGetPublicKeys(dids.get(0).ontid);
//            String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(1).ontid);

            Map attrs = new HashMap<>();
            attrs.put("key1","value1");
//            ontSdk.nativevm().ontId().sendAddAttributes(dids.get(0).ontid,password,attrs,payerAcc.address,password,0);
            ontSdk.nativevm().ontId();

            System.out.println("ddo1:" + ddo1);
            System.out.println("publicKeys:" + publicKeys);
            System.out.println("publicKeysState:" + ontSdk.nativevm().ontId().sendGetKeyState(dids.get(0).ontid,1));
            System.out.println("attributes:" + ontSdk.nativevm().ontId().sendGetAttributes(dids.get(0).ontid));
//            System.out.println("ddo2:" + ddo2);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://polaris1.ont.io";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("NativeOntIdDemo.json");
        return wm;
    }
}
