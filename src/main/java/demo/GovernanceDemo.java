package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;

public class GovernanceDemo {

    public static void main(String[] args){
        OntSdk sdk;
        try {
            sdk = getOntSdk();
            String password = "111111";
            String privatekey1 = "54ca4db481966046b15f8d15ff433e611c49ab8e68a279ebf579e4cfd108196d";
            com.github.ontio.sdk.wallet.Account payerAcc = sdk.getWalletMgr().createAccountFromPriKey(password, privatekey1);
            Account payerAcct = new Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);
System.out.println(Helper.toHexString(payerAcct.serializePublicKey()));
System.exit(0);
//            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            String privatekey0 = "f1442d5e7f4e2061ff9a6884d6d05212e2aa0f6a6284f0a28ae82a29cdb3d656";
            String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=", password, "TA4nUbnjX5UGVxkumhfndc7wyemrxdMtn8", 16384, SignatureScheme.SHA256WITHECDSA);
            Account account = new Account(Helper.hexToBytes(prikey),SignatureScheme.SHA256WITHECDSA);

            System.out.println(sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
            System.out.println(sdk.nativevm().ong().unclaimOng(account.getAddressU160().toBase58()));

            sdk.getWalletMgr().importAccount("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,"TA4nUbnjX5UGVxkumhfndc7wyemrxdMtn8");

//            System.exit(0);
//            AccountInfo info = sdk.getWalletMgr().getAccountInfo(account.getAddressU160().toBase58(),password);
            String txhash = sdk.nativevm().governance().registerCandidate(account,Helper.toHexString(account.serializePublicKey()),100000,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//            String txhash = sdk.nativevm().governance().approveCandidate(Helper.toHexString(account.serializePublicKey()),payerAcct.getAddressU160().toBase58(),password,sdk.DEFAULT_GAS_LIMIT,0);

//            Thread.sleep(6000);
//            Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
//            System.out.println(obj);

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
