package demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.governance.AuthorizeInfo;
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
            String password = "111111";
            String privatekey1 = "54ca4db481966046b15f8d15ff433e611c49ab8e68a279ebf579e4cfd108196d";
            Account payerAcct = new Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);

            String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";
            String privatekey8 = "87a209d232d6b4f3edfcf5c34434aa56871c2cb204c263f6b891b95bc5837cac";
            String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";

            Account account9 = new Account(Helper.hexToBytes(privatekey9),SignatureScheme.SHA256WITHECDSA);

            String prikey = "75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf";
//            prikey = "523c5fcf74823831756f0bcb3634234f10b3beb1c05595058534577752ad2d9f";
            String adminPrivateKey = "957419a5ceaf5bd40e83e0fc59e71b0d7fef68149e3ea99f79149afc441549cd";
//            Account adminOntIdAcct = getAccount("FbB9GrorxmLn7AgX6d5+/hhp6zGb7OwXzYYhbPuCqy8NvMJgTlAaRNUa4yfcG0Wf","passwordtest","AMAx993nE6NEqZjwBssUfopxnnvTdob9ij","W3i7TqkbdQ6OQineVzL47A==");
            //梦航
            Account adminOntIdAcct = getAccount("cCQnie0Dd8aQPyY+9UBFw2x2cLn2RMogKqhM8OkyjJNrNTvlcVaYGRENfy2ErF7Q","passwordtest","ARiwjLzjzLKZy8V43vm6yUcRG9b56DnZtY","3e1zvaLjtVuPrQ1o7oJsQA==");
            adminPrivateKey =Helper.toHexString(adminOntIdAcct.serializePrivateKey());
            String adminPrivateKey2 = "ca53fa4f53ed175e39da86f4e02cd87638652cdbdcdae594c81d2e2f2f673745";
            Account account = new Account(Helper.hexToBytes(prikey),SignatureScheme.SHA256WITHECDSA);
            Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
            Account account7 = new Account(Helper.hexToBytes(privatekey7),SignatureScheme.SHA256WITHECDSA);
            Address multiAddress;
            Account account1;
            Account account2;
            Account account3;
            Account account4;
            Account account5;
            Account account6;
            Account account7s;
            if(false){
                account1 = getAccount("YfOr9im4rOciy3cV7JkVo9QCfrRT4IGLa/CZKUJfL29pM6Zi1oVEM67+8MezMIro","1","AXmQDzzvpEtPkNwBEFsREzApTTDZFW6frD","RCIo60eCJAwzkTYmIfp3GA==");
                account2 = getAccount("gpgMejEHzawuXG+ghLkZ8/cQsOJcs4BsFgFjSaqE7SC8zob8hqc6cDNhJI/NBkk+","1","AY5W6p4jHeZG2jjW6nS1p4KDUhcqLkU6jz","tuLGZOimilSnypT91WrenQ==");
                account3 = getAccount("guffI05Eafq9F0j3/eQxHWGo1VN/xpeIkXysEPeH51C2YHYCNnCWTWAdqDB7lonl","1","ALZVrZrFqoSvqyi38n7mpPoeDp7DMtZ9b6","oZPg+5YotRWStVsRMYlhfg==");
                account4 = getAccount("fAknSuXzMMC0nJ2+YuTpTLs6Hl5Dc0c2zHZBd2Q7vCuv8Wt97uYz1IU0t+AtrWts","1","AMogjmLf2QohTcGST7niV75ekZfj44SKme","0BVIiUf46rb/e5dVZIwfrg==");
                account5 = getAccount("IufXVQfrL3LI7g2Q7dmmsdoF7BdoI/vHIsXAxd4qkqlkGBYj3pcWHoQgdCF+iVOv","1","AZzQTkZvjy7ih9gjvwU8KYiZZyNoy6jE9p","zUtzh0B4UW0wokzL+ILdeg==");
                account6 = getAccount("PYEJ1c79aR7bxdzvBlj3lUMLp0VLKQHwSe+/OS1++1qa++gBMJJmJWJXUP5ZNhUs","1","AKEqQKmxCsjWJz8LPGryXzb6nN5fkK1WDY","uJhjsfcouCGZQUdHO2TZZQ==");
                account7s = getAccount("ZG/SfHRArUkopwhQS1MW+a0fvQvyN1NnwonU0oZH8y1bGqo5T+dQz3rz1qsXqFI2","1","AQNpGWz4oHHFBejtBbakeR43DHfen7cm8L","6qiU9bgK/+1T2V8l14mszg==");

                multiAddress = Address.addressFromMultiPubKeys(5,account1.serializePublicKey(),account2.serializePublicKey(),account3.serializePublicKey(),account4.serializePublicKey(),account5.serializePublicKey(),account6.serializePublicKey(),account7s.serializePublicKey());

            } else {
                //梦航
                account1 = getAccount("wR9S/JYwMDfCPWFGEy5DEvWfU14k9suZuL4+woGtfhZJf5+KyL9VJqMi/wGTOd1i","passwordtest","AZqk4i7Zhfhc1CRUtZYKrLw4YTSq4Y9khN","ZaIL8DxNaQ91fkMHAdiBjQ==");
                account2 = getAccount("PCj/a4zUgYnOBNZUVEaXBK61Sq4due8w2RUzrumO3Bm0hZ/3v4mlDiXYYvmmBZUk","passwordtest","ARpjnrnHEjXhg4aw7vY6xsY6CfQ1XEWzWC","wlz1h439j0GwsWhGBByMxg==");
                account3 = getAccount("4U6qYhRUxGYTcvDvBKKCu2C1xUyd0A+pHXsK1YVY1Hbxd8TcbyvmfOcqx7N+f+BH","passwordtest","AQs2BmzzFVk7pQPfTQQi9CTEz43ejSyBnt","AFDFoZAlLGJdB4yVQqYVhw==");
                account4 = getAccount("i6n+FTACzRF5y0oeo6Wm3Zbv68bfjmyRyNfKB5IArK76RCG8b/JgRqnHgMtHixFx","passwordtest","AKBSRLbFNvUrWEGtKxNTpe2ZdkepQjYKfM","FkTZ6czRPAqHnSpEqVEWwA==");
                account5 = getAccount("IoEbJXMPlxNLrAsDYKGD4I6oFYgJl1j603c8oHQl+82yET+ibKgJdZjgdw39pr2K","passwordtest","AduX7odaWGipkdvzBwyaTgsumRbRzhhiwe","lc7ofKCBkNUmjTLrZYmStA==");
                account6 = getAccount("6hynBJVTAhmMJt9bIYSDoz+GL5EFaUGhn3Pd6HsF+RQ1tFyZoFRhT+JNMGAb+B6a","passwordtest","ANFfWhk3A5iFXQrVBHKrerjDDapYmLo5Bi","DTmbW9wzGA8pi4Dcj3/Cpg==");
                account7s = getAccount("EyXxszzKh09jszQXMIFJTmbujnojOzYzPU4cC0wOpuegDgVcRFllATQ81zD0Rp8s","passwordtest","AK3YRcRvKrASQ6nTfW48Z4iMZ2sDTDRiMC","jbwUF7JxgsiJq5QAy5dfug==");
                multiAddress = Address.addressFromMultiPubKeys(5,account1.serializePublicKey(),account2.serializePublicKey(),account3.serializePublicKey(),account4.serializePublicKey(),account5.serializePublicKey(),account6.serializePublicKey(),account7s.serializePublicKey());

            }

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
//            System.out.println("account:" + account.getAddressU160().toBase58());
//            System.out.println("account:" + sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            if(false){
                sdk.nativevm().ont().sendTransfer(account,account9.getAddressU160().toBase58(),100000000,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                System.out.println("account" + sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
                System.out.println("account" + sdk.nativevm().ong().unboundOng(account.getAddressU160().toBase58()));
                sdk.nativevm().ong().withdrawOng(account,account9.getAddressU160().toBase58(),640000000000L,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
            }

            if(false){
                Identity identity = sdk.getWalletMgr().getWallet().getIdentity(Common.didont+adminOntIdAcct.getAddressU160().toBase58());
                String txhash = sdk.nativevm().ontId().sendRegister(identity,password,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);

                System.out.println(sdk.nativevm().ontId().sendGetDDO(Common.didont+adminOntIdAcct.getAddressU160().toBase58()));

                return;
            }
            List<Identity> dids = sdk.getWalletMgr().getWallet().getIdentities();
            Identity identity = dids.get(0);


            Identity adminOntid = sdk.getWalletMgr().createIdentityFromPriKey(password,adminPrivateKey);
//            Identity adminOntid = sdk.getWalletMgr().getWallet().getIdentity(Common.didont+adminOntIdAcct.getAddressU160().toBase58());
            Account adminAccount = new Account(Helper.hexToBytes(adminPrivateKey),SignatureScheme.SHA256WITHECDSA);
            Account adminAccount2 = new Account(Helper.hexToBytes(adminPrivateKey2),SignatureScheme.SHA256WITHECDSA);
            System.out.println("account:" + sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            System.out.println("account:" + account.getAddressU160().toBase58());
            System.out.println("account1:" + sdk.getConnect().getBalance(account1.getAddressU160().toBase58()));

            if(false){
                String contractAddr = "0000000000000000000000000000000000000007";
//                Identity adminOntid = sdk.getWalletMgr().getWallet().getIdentity("did:ont:AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve");
//                String txhash = sdk.nativevm().auth().assignFuncsToRole(adminOntid.ontid,password,adminOntid.controls.get(0).getSalt(),1,contractAddr,"role",new String[]{"registerCandidate"},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                String txhash = sdk.nativevm().auth().assignOntIdsToRole(adminOntid.ontid,password,adminOntid.controls.get(0).getSalt(),1,contractAddr,"role",new String[]{identity.ontid},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);

                Object obj2 = sdk.nativevm().auth().verifyToken(identity.ontid,password,identity.controls.get(0).getSalt(),1,contractAddr,"registerCandidate");
                System.out.println(obj2);

            }
            if(false){
                sdk.nativevm().ong().sendTransfer(account1,Address.parse("0000000000000000000000000000000000000007").toBase58(),10000000000L,account1,20000,0);
                Thread.sleep(6000);

            }
            System.out.println("governance balance:" + sdk.getConnect().getBalance(Address.parse("0000000000000000000000000000000000000007").toBase58()));
            if(true){
                String res = sdk.nativevm().governance().getSplitFeeAddress(account1.getAddressU160().toBase58());
                System.out.println("SplitFeeAddress:" + res);
            }
            if(false){
                sdk.getWalletMgr().importAccount("blDuHRtsfOGo9A79rxnJFo2iOMckxdFDfYe2n6a9X+jdMCRkNUfs4+C4vgOfCOQ5",password,account.getAddressU160().toBase58(),Base64.getDecoder().decode("0hAaO6CT+peDil9s5eoHyw=="));

                String txhash = sdk.nativevm().governance().registerCandidate(account1,"02a98c15099d599b55c87834fd41bf9ad55408050b32bdc6ea88d6358717e19525",10000,identity.ontid,password,identity.controls.get(0).getSalt(),1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().unRegisterCandidate(account,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);

            }
            if(false){
                String txhash = sdk.nativevm().governance().withdrawFee(account1,account1,20000,0);
//                String txhash = sdk.nativevm().governance().unAuthorizeForPeer(account1,new String[]{"02a98c15099d599b55c87834fd41bf9ad55408050b32bdc6ea88d6358717e19525"},new long[]{20000},account1,20000,0);
//                String txhash = sdk.nativevm().governance().authorizeForPeer(account1,new String[]{"02a98c15099d599b55c87834fd41bf9ad55408050b32bdc6ea88d6358717e19525"},new long[]{2000000},account1,20000,0);
//                String txhash = sdk.nativevm().governance().changeMaxAuthorization(account1,"02a98c15099d599b55c87834fd41bf9ad55408050b32bdc6ea88d6358717e19525",20000,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().setPeerCost(account1,"02a98c15099d599b55c87834fd41bf9ad55408050b32bdc6ea88d6358717e19525",90,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().unRegisterCandidate(account,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);
            }

            if(false){
//                sdk.nativevm().governance().approveCandidate(multiAddress,5,new Account[]{account1,account2,account3,account4,account5},new byte[][]{account6.serializePublicKey(),account7s.serializePublicKey()},
//                "02a98c15099d599b55c87834fd41bf9ad55408050b32bdc6ea88d6358717e19525",account,sdk.DEFAULT_GAS_LIMIT,0);
//
                String txhash = sdk.nativevm().governance().commitDpos(multiAddress,5,new Account[]{account1,account2,account3,account4,account5},new byte[][]{account6.serializePublicKey(),account7s.serializePublicKey()},
                        account,sdk.DEFAULT_GAS_LIMIT,0);

//                sdk.nativevm().governance().rejectCandidate(multiAddress,5,new Account[]{account1,account2,account3,account4,account5},new byte[][]{account6.serializePublicKey(),account7s.serializePublicKey()},
//                        "0205bc592aa9121428c4144fcd669ece1fa73fee440616c75624967f83fb881050",account,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
//                System.out.println("txevent:" + sdk.getConnect().getSmartCodeEvent(txhash));
            }
            if(false){
//                Identity adminOntid = sdk.getWalletMgr().getWallet().getIdentity("did:ont:AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve");
//                String txhash = sdk.nativevm().governance().approveCandidate(adminAccount2,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                  String txhash = sdk.nativevm().governance().rejectCandidate(adminAccount2,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
// String txhash = sdk.nativevm().governance().voteForPeer(account,new String[]{Helper.toHexString(account8.serializePublicKey())},new long[]{100},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//String txhash = sdk.nativevm().governance().unVoteForPeer(account,new String[]{Helper.toHexString(account8.serializePublicKey())},new long[]{300},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                  String txhash = sdk.nativevm().governance().quitNode(account,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                String txhash = sdk.nativevm().governance().withdraw(account,new String[]{"03e1e09221c9f513df76273f3cec0d033ee6056b159300d7b1072fc7020eadccbb"},new long[]{9999},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().commitDpos(adminAccount2,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
//                System.out.println("account9" +sdk.getConnect().getBalance( account9.getAddressU160().toBase58()));
            }
            if(true) {
                System.out.println("account:" + sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
                String res = sdk.nativevm().governance().getPeerInfoAll();
                JSONObject jsr = JSONObject.parseObject(res);
//                System.out.println(Helper.toHexString(account7.serializePublicKey()));
                AuthorizeInfo voteInfo= sdk.nativevm().governance().getAuthorizeInfo("02a98c15099d599b55c87834fd41bf9ad55408050b32bdc6ea88d6358717e19525",account.getAddressU160());
                if(voteInfo != null) {
                    System.out.println("voteInfo:" + voteInfo.json());
                }

                String aa = sdk.nativevm().governance().getPeerAttributes("02a98c15099d599b55c87834fd41bf9ad55408050b32bdc6ea88d6358717e19525");
                System.out.println("PeerAttributes:" + aa);

//                System.out.println("peerInfo:" + jsr.getString(Helper.toHexString(account9.serializePublicKey())));
                System.out.println("peerInfo2:" + sdk.nativevm().governance().getPeerInfo("02a98c15099d599b55c87834fd41bf9ad55408050b32bdc6ea88d6358717e19525"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Account getAccount(String enpri,String password,String address,String salt) throws Exception {
        String privateKey = Account.getGcmDecodedPrivateKey(enpri,password,address,Base64.getDecoder().decode(salt),16384,SignatureScheme.SHA256WITHECDSA);
        Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
//        System.out.println(Helper.toHexString(account.serializePublicKey()));
        return account;
    }



    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://127.0.0.1";
        String ip = "http://polaris1.ont.io";
        ip= "http://139.219.128.220";
//        String ip = "http://139.219.128.60";
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
