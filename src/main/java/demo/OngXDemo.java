package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.sidechain.smartcontract.ongx.Swap;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sidechain.smartcontract.ongx.OngX;

import java.util.Base64;

public class OngXDemo {

    public static void main(String[] args) throws Exception {
//        String mainChainUrl = "http://139.219.128.220:20336";
        String sideChainUrl = "http://23.99.137.227:30336";
        sideChainUrl = "http://139.219.128.220:30336";
        String menghangSideChain = "http://138.91.6.125:30336";
        OntSdk sdk = OntSdk.getInstance();
        sdk.openWalletFile("ongx.dat");
        sdk.setSideChainRpc(menghangSideChain);
//        sdk.setRpc("http://139.219.128.220:20336");
//        sdk.setRpc("http://127.0.0.1:20336");
//        sdk.setRpc(sideChainUrl);

        String password = "111111";
        Account account = sdk.getWalletMgr().getAccount("AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe",password);
        Identity identity = sdk.getWalletMgr().getWallet().getIdentity("did:ont:Abrc5byDEZm1CnQb3XjAosEt34DD4w5Z1o");
        String sideChainContractAddr = "0000000000000000000000000000000000000008";

        //梦航
//        Account adminOntIdAcct = getAccount("cCQnie0Dd8aQPyY+9UBFw2x2cLn2RMogKqhM8OkyjJNrNTvlcVaYGRENfy2ErF7Q","passwordtest","ARiwjLzjzLKZy8V43vm6yUcRG9b56DnZtY","3e1zvaLjtVuPrQ1o7oJsQA==");
//        String adminPrivateKey =Helper.toHexString(adminOntIdAcct.serializePrivateKey());
        Identity adminIndentity = sdk.getWalletMgr().getWallet().getIdentity("did:ont:ARiwjLzjzLKZy8V43vm6yUcRG9b56DnZtY");
        //梦航
        Account account1 = getAccount("wR9S/JYwMDfCPWFGEy5DEvWfU14k9suZuL4+woGtfhZJf5+KyL9VJqMi/wGTOd1i","passwordtest","AZqk4i7Zhfhc1CRUtZYKrLw4YTSq4Y9khN","ZaIL8DxNaQ91fkMHAdiBjQ==");
        Account account2 = getAccount("PCj/a4zUgYnOBNZUVEaXBK61Sq4due8w2RUzrumO3Bm0hZ/3v4mlDiXYYvmmBZUk","passwordtest","ARpjnrnHEjXhg4aw7vY6xsY6CfQ1XEWzWC","wlz1h439j0GwsWhGBByMxg==");
        Account account3 = getAccount("4U6qYhRUxGYTcvDvBKKCu2C1xUyd0A+pHXsK1YVY1Hbxd8TcbyvmfOcqx7N+f+BH","passwordtest","AQs2BmzzFVk7pQPfTQQi9CTEz43ejSyBnt","AFDFoZAlLGJdB4yVQqYVhw==");
        Account account4 = getAccount("i6n+FTACzRF5y0oeo6Wm3Zbv68bfjmyRyNfKB5IArK76RCG8b/JgRqnHgMtHixFx","passwordtest","AKBSRLbFNvUrWEGtKxNTpe2ZdkepQjYKfM","FkTZ6czRPAqHnSpEqVEWwA==");
        Account account5 = getAccount("IoEbJXMPlxNLrAsDYKGD4I6oFYgJl1j603c8oHQl+82yET+ibKgJdZjgdw39pr2K","passwordtest","AduX7odaWGipkdvzBwyaTgsumRbRzhhiwe","lc7ofKCBkNUmjTLrZYmStA==");
        Account account6 = getAccount("6hynBJVTAhmMJt9bIYSDoz+GL5EFaUGhn3Pd6HsF+RQ1tFyZoFRhT+JNMGAb+B6a","passwordtest","ANFfWhk3A5iFXQrVBHKrerjDDapYmLo5Bi","DTmbW9wzGA8pi4Dcj3/Cpg==");
        Account account7 = getAccount("EyXxszzKh09jszQXMIFJTmbujnojOzYzPU4cC0wOpuegDgVcRFllATQ81zD0Rp8s","passwordtest","AK3YRcRvKrASQ6nTfW48Z4iMZ2sDTDRiMC","jbwUF7JxgsiJq5QAy5dfug==");
        Address multiAddress = Address.addressFromMultiPubKeys(5,account1.serializePublicKey(),account2.serializePublicKey(),account3.serializePublicKey(),account4.serializePublicKey(),account5.serializePublicKey(),account6.serializePublicKey(),account7.serializePublicKey());
        Account[] accounts = new Account[]{account1,account2,account3,account4,account5,account6,account7};
        Account[] accounts1 = new Account[]{account1,account2,account3,account4,account5};
        byte[][] pks = new byte[accounts.length][];
        for(int i=0;i<pks.length;i++){
            pks[i] = accounts[i].serializePublicKey();
        }

        sdk.setSideChainId(3092255979L);

        boolean ongxSetSyncAddr = false;
        boolean ongxSwap = true;
        boolean ongSwap = false;

        if(ongxSetSyncAddr){
            String txhash = sdk.sidechainVm().ongX().ongxSetSyncAddr(accounts1,pks,5,account.getAddressU160().toBase58(),account,20000,0);
//            String txhash = sdk.sidechainVm().ongX().ongxSetSyncAddr(account,account.getAddressU160().toBase58(),account,20000,0);
            System.out.println("txhash: " + txhash);
            Thread.sleep(6000);
            System.out.println(sdk.getSideChainConnectMgr().getSmartCodeEvent(txhash));
            return;
        }
        if(false){
            System.out.println(sdk.sidechainVm().ongX().queryBalanceOf(account.getAddressU160().toBase58()));
            return;
        }
        if(false){
//            sdk.setRpc(mainChainUrl);
//            System.out.println(sdk.getConnect().getBlockHeight());
            sdk.setRpc(sideChainUrl);
            System.out.println(sdk.getSideChainConnectMgr().getBlockHeight());
//            return;
        }
        if(false){
            System.out.println(sdk.sidechainVm().ongX().queryBalanceOf(account.getAddressU160().toBase58()));
            return;
        }
        if(ongxSwap){
            Swap swap = new Swap(account.getAddressU160(),100);
            String txhash = sdk.sidechainVm().ongX().ongxSwap(account, swap,account,20000,0);
            System.out.println("txhash: " + txhash);
            Thread.sleep(6000);
            System.out.println(sdk.getSideChainConnectMgr().getSmartCodeEvent(txhash));
            System.out.println(sdk.sidechainVm().ongX().queryBalanceOf(account.getAddressU160().toBase58()));
            return;
        }
        if(ongSwap){
            Swap swap = new Swap(account.getAddressU160(),(long)1000*1000000000);
            String txhash = sdk.sidechainVm().ongX().ongSwap(account,new Swap[]{swap},account,20000,0);
            System.out.println("txhash: " + txhash);
            Thread.sleep(6000);
            System.out.println(sdk.getSideChainConnectMgr().getSmartCodeEvent(txhash));
        }
        if(false){
//            子链启动的时候调用，
            String txhash = sdk.sidechainVm().ongX().ongxSetSyncAddr(accounts,pks,5,account.getAddressU160().toBase58(),account1,20000,0);
            Thread.sleep(6000);
            System.out.println(sdk.getSideChainConnectMgr().getSmartCodeEvent(txhash));
            return;
        }

    }

    public static Account getAccount(String enpri,String password,String address,String salt) throws Exception {
        String privateKey = Account.getGcmDecodedPrivateKey(enpri,password,address, Base64.getDecoder().decode(salt),16384, SignatureScheme.SHA256WITHECDSA);
        Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
//        System.out.println(Helper.toHexString(account.serializePublicKey()));
        return account;
    }
}
