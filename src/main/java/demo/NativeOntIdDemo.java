package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
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

            if(ontSdk.getWalletMgr().getIdentitys().size() < 3){
                Identity identity = ontSdk.getWalletMgr().createIdentity(password);
                Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,payerAcc.address,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
                ontSdk.addSign(tx,payerAcc.address,password);
                ontSdk.getConnect().sendRawTransaction(tx);

                Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);
                ontSdk.nativevm().ontId().sendRegister(identity2,password,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

                Identity identity3 = ontSdk.getWalletMgr().createIdentity(password);
                Attribute[] attributes = new Attribute[1];
                attributes[0] = new Attribute("key1".getBytes(),"String".getBytes(),"value1".getBytes());
                ontSdk.nativevm().ontId().sendRegisterWithAttrs(identity3,password,attributes,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.getWalletMgr().writeWallet();
                Thread.sleep(6000);
            }

//
            List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();

            System.out.println("dids.get(0).ontid:" + dids.get(0).ontid);
//            System.out.println("dids.get(1).ontid:" + dids.get(1).ontid);
//            System.out.println("dids.get(2).ontid:" + dids.get(2).ontid);

            String ddo1 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid);
//            String publicKeys = ontSdk.nativevm().ontId().sendGetPublicKeys(dids.get(0).ontid);
//            String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(1).ontid);
//            String ddo3 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(2).ontid);

            System.out.println("ddo1:" + ddo1);
//            System.out.println("ddo2:" + ddo2);
//            System.out.println("ddo3:" + ddo3);

            IdentityInfo info2 = ontSdk.getWalletMgr().getIdentityInfo(dids.get(1).ontid,password);
            IdentityInfo info3 = ontSdk.getWalletMgr().getIdentityInfo(dids.get(2).ontid,password);
//            String txhash = ontSdk.nativevm().ontId().sendAddPubKey(dids.get(0).ontid,password,info2.pubkey,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            Account account = ontSdk.getWalletMgr().createAccountFromPriKey(password, privatekey0);
            String privatekey1 = "2ab720ff80fcdd31a769925476c26120a879e235182594fbb57b67c0743558d7";
            Account account2 = ontSdk.getWalletMgr().createAccountFromPriKey(password, privatekey1);

            com.github.ontio.account.Account acct = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0),SignatureScheme.SHA256WITHECDSA);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0),SignatureScheme.SHA256WITHECDSA);
            Address multiAddr = Address.addressFromMultiPubKeys(2,acct.serializePublicKey(),acct2.serializePublicKey());
//ontSdk.nativevm().ontId().sendAddPubKey(dids.get(0).ontid,password,info3.pubkey,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//            ontSdk.nativevm().ontId().sendRemovePubKey(dids.get(0).ontid,account.address,password,info2.pubkey,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//ontSdk.nativevm().ontId().sendAddPubKey(dids.get(0).ontid,account.address,password,info2.pubkey,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//            String txhash2 = ontSdk.nativevm().ontId().sendAddRecovery(dids.get(0).ontid,password,multiAddr.toBase58(),payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//ontSdk.nativevm().ontId().sendChangeRecovery(dids.get(0).ontid,account2.address,account.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
            Transaction tx = ontSdk.nativevm().ontId().makeAddPubKey(dids.get(0).ontid,multiAddr.toBase58(),null,info2.pubkey,payerAcc.address,ontSdk.DEFAULT_GAS_LIMIT,0);
            ontSdk.signTx(tx,new com.github.ontio.account.Account[][]{{acct,acct2}});
            ontSdk.addSign(tx,payerAcc.address,password);
            ontSdk.getConnect().sendRawTransaction(tx.toHexString());
            Attribute[] attributes = new Attribute[2];
            attributes[0] = new Attribute("key2".getBytes(),"String".getBytes(),"value6".getBytes());
            attributes[1] = new Attribute("key3".getBytes(),"String".getBytes(),"value3".getBytes());
            Identity identity = ontSdk.getWalletMgr().createIdentity(password);
//            ontSdk.nativevm().ontId().sendRegisterWithAttrs(identity,password,attributes,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//            ontSdk.nativevm().ontId().sendAddAttributes(dids.get(0).ontid,password,attributes,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
            Thread.sleep(6000);
            String ddo4 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid);
            System.out.println("ddo4:" + ddo4);
//            System.out.println(ontSdk.nativevm().ontId().sendGetAttributes(dids.get(0).ontid));
//            System.out.println(ontSdk.nativevm().ontId().sendGetKeyState(dids.get(0).ontid,1));
//            System.out.println(ontSdk.nativevm().ontId().sendGetPublicKeys(dids.get(0).ontid));
//            Object obj = ontSdk.getConnect().getSmartCodeEvent(txhash);
//            System.out.println("obj:" + obj);
//            Object obj2 = ontSdk.getConnect().getSmartCodeEvent(txhash);
//            System.out.println("obj2:" + obj2);

            System.exit(0);

//            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
//            Account account = ontSdk.getWalletMgr().createAccountFromPriKey("passwordtest", privatekey0);
//
////            String txhash = ontSdk.nativevm().ontId().sendAddRecovery(dids.get(0).ontid,password,account.address,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//
//            AccountInfo info = ontSdk.getWalletMgr().getAccountInfo(account.address,"passwordtest");
////String txhash = ontSdk.nativevm().ontId().sendAddPubKey(dids.get(0).ontid,password,info.pubkey,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//            System.out.println(info.pubkey);
//            String txhash = ontSdk.nativevm().ontId().sendAddPubKey(dids.get(0).ontid,info.addressBase58,"passwordtest",info.pubkey,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//
//            Thread.sleep(6000);
//
//            Object obj = ontSdk.getConnect().getSmartCodeEvent(txhash);
//
//            System.out.println("obj:"+ obj);
//
//            System.exit(0);
//
//            Attribute[] attributes = new Attribute[1];
//            attributes[0] = new Attribute("key1".getBytes(),"value1".getBytes(),"String".getBytes());
//            ontSdk.nativevm().ontId().sendAddAttributes(dids.get(0).ontid,password,attributes,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
//
//            System.exit(0);
//
//            System.out.println("ddo1:" + ddo1);
//            System.out.println("publicKeys:" + publicKeys);
//            System.out.println("publicKeysState:" + ontSdk.nativevm().ontId().sendGetKeyState(dids.get(0).ontid,1));
//            System.out.println("attributes:" + ontSdk.nativevm().ontId().sendGetAttributes(dids.get(0).ontid));
//            System.out.println("ddo2:" + ddo2);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://polaris1.ont.io";
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
