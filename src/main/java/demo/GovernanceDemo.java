package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Identity;

import java.util.List;
import java.util.Map;

public class GovernanceDemo {

    public static void main(String[] args){
        OntSdk sdk;
        try {
            sdk = getOntSdk();
            String password = "111111";
            String privatekey1 = "54ca4db481966046b15f8d15ff433e611c49ab8e68a279ebf579e4cfd108196d";
            Account payerAcct = new Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);

            String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";

            String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=", password, "TA4nUbnjX5UGVxkumhfndc7wyemrxdMtn8", 16384, SignatureScheme.SHA256WITHECDSA);
            Account account = new Account(Helper.hexToBytes(prikey),SignatureScheme.SHA256WITHECDSA);

            if(sdk.getWalletMgr().getIdentitys().size() < 2){
                Identity identity = sdk.getWalletMgr().createIdentity(password);
                String txhash = sdk.nativevm().ontId().sendRegister(identity,password,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);

                Identity identity2 = sdk.getWalletMgr().createIdentity(password);
                String txhash2 = sdk.nativevm().ontId().sendRegister(identity2,password,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);
                sdk.getWalletMgr().writeWallet();
            }
            if(false){
                sdk.nativevm().ont().sendTransfer(account,payerAcct.getAddressU160().toBase58(),10,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
                System.out.println(sdk.nativevm().ong().unclaimOng(account.getAddressU160().toBase58()));
                sdk.nativevm().ong().claimOng(account,account.getAddressU160().toBase58(),1360000000000L,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
            }

            if(false){
                Identity identity = sdk.getWalletMgr().importIdentity("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,account.getAddressU160().toBase58());
                String txhash = sdk.nativevm().ontId().sendRegister(identity,password,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);
            }
            List<Identity> dids = sdk.getWalletMgr().getIdentitys();
            Identity identity = dids.get(0);
            System.out.println(sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            if(false){
                String contractAddr = "ff00000000000000000000000000000000000007";
                Identity adminOntid = sdk.getWalletMgr().importIdentity("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,account.getAddressU160().toBase58());
//                String txhash = sdk.nativevm().auth().assignFuncsToRole(Common.didont+account.getAddressU160().toBase58(),password,contractAddr,"role",new String[]{"registerCandidate"},1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                String txhash = sdk.nativevm().auth().assignOntIDsToRole(adminOntid.ontid,password,contractAddr,"role",new String[]{identity.ontid,dids.get(1).ontid},1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);
            }
            Account account1 = new Account(Helper.hexToBytes(privatekey9),SignatureScheme.SHA256WITHECDSA);
            if(false){
                sdk.getWalletMgr().importAccount("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,"TA4nUbnjX5UGVxkumhfndc7wyemrxdMtn8");

                String txhash = sdk.nativevm().governance().registerCandidate(account,Helper.toHexString(account1.serializePublicKey()),100000,identity.ontid,password,1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);

                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);


            }
            if(false){
                Identity adminOntid = sdk.getWalletMgr().importIdentity("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,account.getAddressU160().toBase58());
//                String txhash = sdk.nativevm().governance().approveCandidate(adminOntid.ontid,password,Helper.toHexString(account1.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().quitNode(account,Helper.toHexString(account1.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                String txhash = sdk.nativevm().governance().withdraw(account,new String[]{Helper.toHexString(account1.serializePublicKey())},new long[]{100000},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().commitDpos(adminOntid.ontid,password,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                System.out.println(sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            }
            System.out.println(Helper.toHexString(account1.serializePublicKey()));
            String res = sdk.nativevm().governance().getPeerPoolMap();
            System.out.println(JSON.toJSONString(res));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://139.219.129.26";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRpc());
        wm.openWalletFile("GovernanceDemo.json");
        return wm;
    }
}
