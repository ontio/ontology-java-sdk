package demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Identity;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class GovernanceDemo {

    public static void main(String[] args){
        OntSdk sdk;
        try {
            sdk = getOntSdk();


            System.out.println(sdk.getConnect().getBalance(Address.parse("0000000000000000000000000000000000000007").toBase58()));
            System.out.println(sdk.nativevm().ong().unclaimOng(Address.parse("0000000000000000000000000000000000000007").toBase58()));
            System.exit(0);
            String password = "111111";
            String privatekey1 = "54ca4db481966046b15f8d15ff433e611c49ab8e68a279ebf579e4cfd108196d";
            Account payerAcct = new Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);

            String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";
            String privatekey8 = "87a209d232d6b4f3edfcf5c34434aa56871c2cb204c263f6b891b95bc5837cac";
            String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";

            Account account9 = new Account(Helper.hexToBytes(privatekey9),SignatureScheme.SHA256WITHECDSA);

            String prikey = "75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf";
//            prikey = "523c5fcf74823831756f0bcb3634234f10b3beb1c05595058534577752ad2d9f";
            Account account = new Account(Helper.hexToBytes(prikey),SignatureScheme.SHA256WITHECDSA);
            Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
            Account account7 = new Account(Helper.hexToBytes(privatekey7),SignatureScheme.SHA256WITHECDSA);
            if(sdk.getWalletMgr().getWallet().getIdentities().size() < 2){
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
                sdk.nativevm().ont().sendTransfer(account,account9.getAddressU160().toBase58(),100000000,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                System.out.println("account" + sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
                System.out.println("account" + sdk.nativevm().ong().unclaimOng(account.getAddressU160().toBase58()));
                sdk.nativevm().ong().claimOng(account,account9.getAddressU160().toBase58(),640000000000L,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
            }

            if(false){
                Identity identity = sdk.getWalletMgr().createIdentityFromPriKey(password,prikey);
                String txhash = sdk.nativevm().ontId().sendRegister(identity,password,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);
            }
            List<Identity> dids = sdk.getWalletMgr().getWallet().getIdentities();
            Identity identity = dids.get(0);
            System.out.println("identity:" + identity.ontid);
            System.out.println("account9:" + sdk.getConnect().getBalance(account9.getAddressU160().toBase58()));
            if(false){
                String contractAddr = "0000000000000000000000000000000000000007";
                Identity adminOntid = sdk.getWalletMgr().getWallet().getIdentity("did:ont:AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve");
//                String txhash = sdk.nativevm().auth().assignFuncsToRole(adminOntid.ontid,password,adminOntid.controls.get(0).getSalt(),1,contractAddr,"role",new String[]{"registerCandidate"},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().auth().assignOntIdsToRole(adminOntid.ontid,password,adminOntid.controls.get(0).getSalt(),1,contractAddr,"role",new String[]{identity.ontid},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//

                Object obj = sdk.nativevm().auth().verifyToken(identity.ontid,password,identity.controls.get(0).getSalt(),1,contractAddr,"registerCandidate");
                System.out.println(obj);
//                Thread.sleep(6000);
//                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
//                System.out.println(obj);
            }
            Account account1 = new Account(Helper.hexToBytes(privatekey9),SignatureScheme.SHA256WITHECDSA);
            if(false){
                sdk.getWalletMgr().importAccount("blDuHRtsfOGo9A79rxnJFo2iOMckxdFDfYe2n6a9X+jdMCRkNUfs4+C4vgOfCOQ5",password,account.getAddressU160().toBase58(),Base64.getDecoder().decode("0hAaO6CT+peDil9s5eoHyw=="));

                String txhash = sdk.nativevm().governance().registerCandidate(account9,Helper.toHexString(account8.serializePublicKey()),100000,identity.ontid,password,identity.controls.get(0).getSalt(),1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);


            }
            if(false){
                Identity adminOntid = sdk.getWalletMgr().getWallet().getIdentity("did:ont:AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve");
//                String txhash = sdk.nativevm().governance().approveCandidate(adminOntid.ontid,password,adminOntid.controls.get(0).getSalt(),Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().voteForPeer(account9,new String[]{Helper.toHexString(account8.serializePublicKey())},new long[]{100},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//String txhash = sdk.nativevm().governance().unVoteForPeer(account9,new String[]{Helper.toHexString(account8.serializePublicKey())},new long[]{100},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                  String txhash = sdk.nativevm().governance().quitNode(account9,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                String txhash = sdk.nativevm().governance().withdraw(account9,new String[]{Helper.toHexString(account8.serializePublicKey())},new long[]{100},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().commitDpos(adminOntid.ontid,password,adminOntid.controls.get(0).getSalt(),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                System.out.println("account9" +sdk.getConnect().getBalance( account9.getAddressU160().toBase58()));
            }
            if(true) {
                System.out.println("account9:" + sdk.getConnect().getBalance(account9.getAddressU160().toBase58()));
                String res = sdk.nativevm().governance().getPeerPoolMap();
                JSONObject jsr = JSONObject.parseObject(res);
                System.out.println(jsr.getString(Helper.toHexString(account8.serializePublicKey())));
            }
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
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRpc());
        wm.openWalletFile("GovernanceDemo.json");
        return wm;
    }
}
