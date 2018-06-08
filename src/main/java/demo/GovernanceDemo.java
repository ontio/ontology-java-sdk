package demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
            String privatekey8 = "87a209d232d6b4f3edfcf5c34434aa56871c2cb204c263f6b891b95bc5837cac";
            String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";

            Account account9 = new Account(Helper.hexToBytes(privatekey9),SignatureScheme.SHA256WITHECDSA);

            String prikey = "f1442d5e7f4e2061ff9a6884d6d05212e2aa0f6a6284f0a28ae82a29cdb3d656";
            Account account = new Account(Helper.hexToBytes(prikey),SignatureScheme.SHA256WITHECDSA);
            Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
            Account account7 = new Account(Helper.hexToBytes(privatekey7),SignatureScheme.SHA256WITHECDSA);
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
                sdk.nativevm().ont().sendTransfer(account,account9.getAddressU160().toBase58(),10000000,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
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
            System.out.println("account:" + sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            if(false){
                String contractAddr = "ff00000000000000000000000000000000000007";
                Identity adminOntid = sdk.getWalletMgr().importIdentity("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,account.getAddressU160().toBase58());
//                String txhash = sdk.nativevm().auth().assignFuncsToRole(adminOntid.ontid,password,contractAddr,"role",new String[]{"registerCandidate"},1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                String txhash = sdk.nativevm().auth().assignOntIDsToRole(adminOntid.ontid,password,contractAddr,"role",new String[]{identity.ontid,dids.get(1).ontid},1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);
            }
            Account account1 = new Account(Helper.hexToBytes(privatekey9),SignatureScheme.SHA256WITHECDSA);
            if(false){
                sdk.getWalletMgr().importAccount("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,"TA4nUbnjX5UGVxkumhfndc7wyemrxdMtn8");

                String txhash = sdk.nativevm().governance().registerCandidate(account,Helper.toHexString(account7.serializePublicKey()),100000,identity.ontid,password,1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);

                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);


            }
            if(true){
                Identity adminOntid = sdk.getWalletMgr().importIdentity("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,account.getAddressU160().toBase58());
//                String txhash = sdk.nativevm().governance().approveCandidate(adminOntid.ontid,password,Helper.toHexString(account7.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().voteForPeer(account,new String[]{Helper.toHexString(account7.serializePublicKey())},new long[]{100},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//String txhash = sdk.nativevm().governance().unVoteForPeer(account,new String[]{Helper.toHexString(account7.serializePublicKey())},new long[]{100},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                  String txhash = sdk.nativevm().governance().quitNode(account,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                String txhash = sdk.nativevm().governance().withdraw(account,new String[]{Helper.toHexString(account7.serializePublicKey())},new long[]{100},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().commitDpos(adminOntid.ontid,password,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                System.out.println(sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            }
            System.out.println("account:" + sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            String res = sdk.nativevm().governance().getPeerPoolMap();
            JSONObject jsr = JSONObject.parseObject(res);
            System.out.println(jsr.getString(Helper.toHexString(account7.serializePublicKey())));
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
