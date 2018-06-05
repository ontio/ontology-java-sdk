package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Identity;

public class GovernanceDemo {

    public static void main(String[] args){
        OntSdk sdk;
        try {
            sdk = getOntSdk();
            String password = "111111";
            String privatekey1 = "54ca4db481966046b15f8d15ff433e611c49ab8e68a279ebf579e4cfd108196d";
            Account payerAcct = new Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);

            String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=", password, "TA4nUbnjX5UGVxkumhfndc7wyemrxdMtn8", 16384, SignatureScheme.SHA256WITHECDSA);
            Account account = new Account(Helper.hexToBytes(prikey),SignatureScheme.SHA256WITHECDSA);

            if(sdk.getWalletMgr().getIdentitys().size() < 1){
                Identity identity = sdk.getWalletMgr().createIdentity(password);
                String txhash = sdk.nativevm().ontId().sendRegister(identity,password,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
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
                sdk.nativevm().ong().claimOng(account,account.getAddressU160().toBase58(),100000,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
            }

            if(false){
                Identity identity = sdk.getWalletMgr().importIdentity("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,account.getAddressU160().toBase58());
                String txhash = sdk.nativevm().ontId().sendRegister(identity,password,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);
            }
            Identity identity = sdk.getWalletMgr().getIdentitys().get(0);
            if(false){
                String contractAddr = "ff00000000000000000000000000000000000007";
                Identity adminOntid = sdk.getWalletMgr().importIdentity("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,account.getAddressU160().toBase58());
//                String txhash = sdk.nativevm().auth().assignFuncsToRole(Common.didont+account.getAddressU160().toBase58(),password,contractAddr,"role",new String[]{"registerCandidate"},1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                String txhash = sdk.nativevm().auth().assignOntIDsToRole(adminOntid.ontid,password,contractAddr,"role",new String[]{identity.ontid},1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);
            }
            if(true){
                sdk.getWalletMgr().importAccount("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,"TA4nUbnjX5UGVxkumhfndc7wyemrxdMtn8");
                String txhash = sdk.nativevm().governance().registerCandidate(account,Helper.toHexString(account.serializePublicKey()),100000,identity.ontid,password,1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//            String txhash = sdk.nativevm().governance().approveCandidate(Helper.toHexString(account.serializePublicKey()),payerAcct.getAddressU160().toBase58(),password,sdk.DEFAULT_GAS_LIMIT,0);

                Thread.sleep(6000);
                Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(obj);

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
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRpc());
        wm.openWalletFile("GovernanceDemo.json");
        return wm;
    }
}
