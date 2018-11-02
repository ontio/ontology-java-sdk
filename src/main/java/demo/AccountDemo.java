package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.ECC;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            com.github.ontio.account.Account account = ontSdk.getWalletMgr().getAccount("AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe","111111");

            if(false){
                ontSdk.nativevm().ong().withdrawOng(account,account.getAddressU160().toBase58(),53620575000000000L,account,20000,0);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getBalance(account.getAddressU160().toBase58()));
                return;
            }

            if(true){
                System.out.println(ontSdk.getConnect().getBalance("AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe"));

                ontSdk.nativevm().ont().sendTransfer(account,"APrfMuKrAQB5sSb5GF8tx96ickZQJjCvwG",10000,account,20000,0);
                Thread.sleep(6000);
                System.out.println(ontSdk.nativevm().ong().unboundOng(account.getAddressU160().toBase58()));

                return;
            }
            byte[] saltt = Base64.getDecoder().decode("0X3NC1UHQGltHc4ikzgzmA==");
            String prikeyg = com.github.ontio.account.Account.getGcmDecodedPrivateKey("7a1ccOWFQUGl0HQmc+PSLeKMwbVZ45/YDHTH/+um4O1z/YAWuv+vsr9zusvYXWbj", "1","ANH5bHrrt111XwNEnuPZj6u95Dd6u7G4D6",saltt,16384,ontSdk.defaultSignScheme);
            com.github.ontio.account.Account a = new com.github.ontio.account.Account(Helper.hexToBytes(prikeyg),ontSdk.defaultSignScheme);
            System.out.println(Helper.toHexString(a.serializePrivateKey()));
            System.out.println(a.getAddressU160().toBase58());
            //com.github.ontio.account.Account b = new com.github.ontio.account.Account(false,a.serializePublicKey());

            //System.out.println(Helper.toHexString(b.serializePublicKey()));
            System.out.println( a.exportGcmEncryptedPrikey("1",saltt,16384));
            //            ontSdk.getWalletMgr().createAccount("password");
//            ontSdk.getWalletMgr().writeWallet();
            //ontSdk.getWalletMgr().getAccount("AUxEWKBM7zaU8iPSdymNSaZt7Dt9yB1KU6","1", Base64.getDecoder().decode("q6FCsP3XKxaeZaj15QZRqA=="));
           // ontSdk.getWalletMgr().getAccount("AHvSop5MbUX6pnqbXnFC5t3yjqVV5DiL7w","password", Base64.getDecoder().decode("ylsxIy8xq0uh4KjjbhxVLw=="));
          // ontSdk.getWalletMgr().getAccount("ANRoMGmxSLtWyzcDcnfCVnJw3FXdNuC9Vq","passwordtest", Base64.getDecoder().decode("ACm4B8Jr1oBPu++e7YIHow=="));
            System.exit(0);
            if(true){
                ontSdk.getWalletMgr().createAccount("1");
                System.exit(0);
            }

            byte[] salt0 = java.util.Base64.getDecoder().decode("+AX/Aa8VXp0h74PZySZ9RA==");
            String key0 = "+TDw5opWl5HfGEWUpxblVa5BqVKF2962DoCwi1GYidwWMKvOj7mqaUVx3k/utGLx";
            System.out.println(Helper.toHexString(salt0)+" "+salt0.length);
            System.out.println(Helper.toHexString(java.util.Base64.getDecoder().decode(key0)));
            String prikey0 = com.github.ontio.account.Account.getGcmDecodedPrivateKey(key0,"1","APrfMuKrAQB5sSb5GF8tx96ickZQJjCvwG", salt0,16384,ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct11 = new com.github.ontio.account.Account(Helper.hexToBytes(prikey0), ontSdk.defaultSignScheme);
            System.out.println(acct11.getAddressU160().toBase58());
           // System.exit(0);
            if (false){
                AccountInfo info0 = ontSdk.getWalletMgr().createAccountInfo("passwordtest");
                AccountInfo info = ontSdk.getWalletMgr().createAccountInfoFromPriKey("passwordtest","e467a2a9c9f56b012c71cf2270df42843a9d7ff181934068b4a62bcdd570e8be");
                System.out.println(info.addressBase58);
                Account accountInfo = ontSdk.getWalletMgr().importAccount("3JZLD/X45qSFjmRRvRVhcEjKgCJQDPWOsjx2dcTEj58=", "passwordtest",info.addressBase58,new byte[]{});

                com.github.ontio.account.Account acct0 = ontSdk.getWalletMgr().getAccount(info.addressBase58, "passwordtest",new byte[]{});
            }
            System.out.println();
            if(true){

                byte[] salt = salt0;
//                salt = ECC.generateKey(16);
                com.github.ontio.account.Account acct = new com.github.ontio.account.Account(Helper.hexToBytes("a1a38ccff49fa6476e737d66ef9f18c7507b50eb4804ed8e077744a4a2a74bb6"),ontSdk.defaultSignScheme);
                String key = acct.exportGcmEncryptedPrikey("1",salt,16384);
                System.out.println(key);
                System.out.println(acct.getAddressU160().toBase58());
                String prikey = com.github.ontio.account.Account.getGcmDecodedPrivateKey(key, "1",acct.getAddressU160().toBase58(),salt,16384,ontSdk.defaultSignScheme);
                System.out.println(prikey);
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("wallet2.dat");

        return wm;
    }
}
