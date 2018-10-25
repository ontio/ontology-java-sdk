package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.sidechaingovernance.NodeToSideChainParams;
import com.github.ontio.core.sidechaingovernance.RegisterSideChainParam;
import com.github.ontio.core.sidechaingovernance.SwapParam;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.nativevm.SideChainGovernance;

import java.util.Base64;

public class SideChainGovernanceDemo {
    public static void main(String[] args) throws Exception {
        OntSdk sdk = OntSdk.getInstance();
        sdk.openWalletFile("wallet.dat");
        sdk.setRpc("http://139.219.128.220:20336");
        SideChainGovernance governance = new SideChainGovernance(sdk);
        String password = "111111";
        Account account = sdk.getWalletMgr().getAccount("AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe",password);
        Identity identity = sdk.getWalletMgr().getWallet().getIdentity("did:ont:Abrc5byDEZm1CnQb3XjAosEt34DD4w5Z1o");
        String sideChainContractAddr = "0000000000000000000000000000000000000008";
        if(false){
            System.out.println(governance.getSideChain("123456"));
//            System.out.println(sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            return;
        }

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
        byte[][] pks = new byte[accounts.length][];
        for(int i=0;i<pks.length;i++){
            pks[i] = accounts[i].serializePublicKey();
        }
        if(false){
//            sdk.nativevm().ontId().sendRegister(identity,password,account,20000,0);
//            sdk.nativevm().ontId().sendRegister(adminIndentity,password,account,20000,0);

//            Transaction tx = sdk.nativevm().ont().makeTransfer(multiAddress.toBase58(),account.getAddressU160().toBase58(),10000, account.getAddressU160().toBase58(),200000,0);
//            Transaction tx = sdk.nativevm().ong().makeWithdrawOng(multiAddress.toBase58(),account.getAddressU160().toBase58(),49700149999100000L,account.getAddressU160().toBase58(),20000,0);
//            for(int i=0;i<5;i++){
//                sdk.addMultiSign(tx, 5, pks, accounts[i]);
//            }
//            sdk.addSign(tx, account);
//            sdk.getConnect().sendRawTransaction(tx.toHexString());
//            System.out.println(tx.hash().toHexString());
//            System.out.println(sdk.nativevm().ong().unboundOng(multiAddress.toBase58()));

//            Thread.sleep(6);
            System.out.println(sdk.getConnect().getBalance(multiAddress.toBase58()));
            System.out.println(sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            return;
        }
        if(false){
//            String txhash = sdk.nativevm().auth().assignFuncsToRole(adminIndentity.ontid,password,adminIndentity.controls.get(0).getSalt(),1,sideChainContractAddr,"role",new String[]{"registerSideChain"},account,20000,0);
            String txhash = sdk.nativevm().auth().assignOntIdsToRole(adminIndentity.ontid,password,adminIndentity.controls.get(0).getSalt(),1,
                    sideChainContractAddr,"role",new String[]{identity.ontid},account,20000,0);

            System.out.println(txhash);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            return;
        }
        if(false){
            String res = sdk.nativevm().auth().verifyToken(identity.ontid,password,identity.controls.get(0).getSalt(),1,sideChainContractAddr,
                    "registerSideChain");
            System.out.println(res);
            return;
        }
        if(false){

            RegisterSideChainParam param = new RegisterSideChainParam("123456", account.getAddressU160(),1,1000,1000,identity.ontid.getBytes(),1);
            String txhash = governance.registerSideChain(account,param, identity,password, account,20000,0);
            System.out.println("txhash: " + txhash);
            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            return;
        }
        if(false){
//            String txhash = governance.rejectSideChain(accounts, pks,5,"123456",account,20000,0);
            String txhash = governance.approveSideChain(accounts, pks,5,"123456",account,20000,0);
            System.out.println(txhash);
            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
        }
        if(false){
            SwapParam param = new SwapParam("123456",account.getAddressU160(), 1000);
            String txhash = governance.ongSwap(account,param,account,20000,0);
            System.out.println("txhash:" + txhash);
            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
        }
        if(false){
            String txhash = governance.setGlobalParams(accounts,pks,5,account.getAddressU160(),account,20000,0);
            System.out.println(txhash);
            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            return;
        }
        if(false){
            SwapParam param = new SwapParam("123456",account.getAddressU160(), 1000);
            String txhash = governance.ongxSwap(account,param,account,20000,0);
            System.out.println(txhash);
            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
        }
        if(true){
            String privatekey = Account.getGcmDecodedPrivateKey("gSiSguflRJN5bItiP4Jo0zZJRhj3bbO9Pj1gSAztLKAfnB6bZ5ohqpo6JZuzV70m","passwordtest","AZonXUcUgzWb2KYdSiLapgqCMEfWGCDTw5",Base64.getDecoder().decode("ZAIkGt7qn7drlGAZ20MVQw=="),16384,SignatureScheme.SHA256WITHECDSA);
            Account account8 = new Account(Helper.hexToBytes(privatekey),SignatureScheme.SHA256WITHECDSA);
            NodeToSideChainParams params = new NodeToSideChainParams("0253ccfd439b29eca0fe90ca7c6eaa1f98572a054aa2d1d56e72ad96c466107a85",account8.getAddressU160(),"123456");
            String txhash = governance.registerNodeToSideChain(account8,params,account8,20000,0);
            System.out.println("txhash:" + txhash);
            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
        }
        if(true){
            System.out.println(governance.getSideChain("123456"));
        }

    }

    public static Account getAccount(String enpri,String password,String address,String salt) throws Exception {
        String privateKey = Account.getGcmDecodedPrivateKey(enpri,password,address,Base64.getDecoder().decode(salt),16384,SignatureScheme.SHA256WITHECDSA);
        Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
//        System.out.println(Helper.toHexString(account.serializePublicKey()));
        return account;
    }
}
