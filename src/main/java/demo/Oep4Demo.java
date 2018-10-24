package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.asset.State;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;

import java.math.BigInteger;
import java.util.Base64;

public class Oep4Demo {
    public static String privatekey0 = "523c5fcf74823831756f0bcb3634234f10b3beb1c05595058534577752ad2d9f";
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
    public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";

    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            String privateKey = Account.getGcmDecodedPrivateKey("8p2q0vLRqyfKmFHhnjUYVWOm12kPm78JWqzkTOi9rrFMBz624KjhHQJpyPmiSSOa","111111","AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe",Base64.getDecoder().decode("KbiCUr53CZUfKG1M3Gojjw=="),16384,SignatureScheme.SHA256WITHECDSA);
//            Account account = sdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p", "xinhao");
            System.out.println(privateKey);
            Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
            com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct3 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey3), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct4 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey4), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct5 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey5), ontSdk.defaultSignScheme);

            Account acct = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0), ontSdk.defaultSignScheme);
//            System.out.println("recv:"+acct.getAddressU160().toBase58());
//            System.out.println("acct1:"+acct1.getAddressU160().toBase58());
//            System.out.println(Helper.toHexString(acct1.getAddressU160().toArray()));
//
//            showBalance(ontSdk,new Account[]{acct1,acct2,acct3});
//            System.out.println("------------------------------------------------------");

            if(false){
                System.out.println(acct.getAddressU160().toBase58());
                return;
            }

            if(false) {
//                long gasLimit = ontSdk.neovm().oep4().sendInitPreExec(acct,acct,30000,0);
//                System.out.println(gasLimit);
                String result = ontSdk.neovm().oep4().sendInit(acct,account,20000,500);
                System.out.println(result);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent(result));
                System.exit(0);
            }
            if(true){
                System.out.println(ontSdk.neovm().oep4().queryDecimals());
                System.out.println(ontSdk.neovm().oep4().queryName());
                System.out.println(ontSdk.neovm().oep4().querySymbol());
                System.out.println(ontSdk.neovm().oep4().queryTotalSupply());
                System.out.println(ontSdk.neovm().oep4().queryBalanceOf(account.getAddressU160().toBase58()));
                return;
            }

            if(false){
                showBalance(ontSdk,new Account[]{account,acct});
                String txhash = ontSdk.neovm().oep4().sendTransfer(account, acct.getAddressU160().toBase58(),1000,account,20000,500);
                Thread.sleep(6000);
                showBalance(ontSdk,new Account[]{account,acct});
                return;
            }
            if(false){
                showBalance(ontSdk,new Account[]{account,acct, acct1});
                Account[] accounts = new Account[]{account,acct};
                State state = new State(account.getAddressU160(),acct.getAddressU160(),10);
                State state2 = new State(account.getAddressU160(),acct1.getAddressU160(),10);
                String txhash = ontSdk.neovm().oep4().sendTransferMulti(accounts, new State[]{state,state2},account,20000,500);
                Thread.sleep(6000);
                showBalance(ontSdk,new Account[]{account,acct,acct1});
                return;
            }
            if(false){
                System.out.println(ontSdk.neovm().oep4().queryAllowance(account.getAddressU160().toBase58(),acct1.getAddressU160().toBase58()));

                String txhash = ontSdk.neovm().oep4().sendApprove(account,acct1.getAddressU160().toBase58(),1000,account,20000,500);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent(txhash));
                System.out.println(ontSdk.neovm().oep4().queryAllowance(account.getAddressU160().toBase58(),acct1.getAddressU160().toBase58()));
                return;
            }
            if(true){
                System.out.println(ontSdk.neovm().oep4().queryAllowance(account.getAddressU160().toBase58(),acct1.getAddressU160().toBase58()));

                String txhash = ontSdk.neovm().oep4().sendTransferFrom(acct1,account.getAddressU160().toBase58(),acct1.getAddressU160().toBase58(),1000,account,20000,500);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent(txhash));
                System.out.println(ontSdk.neovm().oep4().queryAllowance(account.getAddressU160().toBase58(),acct1.getAddressU160().toBase58()));
                return;
            }


            String multiAddr = Address.addressFromMultiPubKeys(2,acct.serializePublicKey(),acct2.serializePublicKey()).toBase58();
            System.out.println("multiAddr:"+multiAddr);
            if(false) {
//                long gasLimit = ontSdk.neovm().nep5().sendTransferPreExec(acct, acct1.getAddressU160().toBase58(), 9000000000L);
//                System.out.println(gasLimit);
                ontSdk.neovm().oep4().sendTransfer(acct1, acct2.getAddressU160().toBase58(), 1000000000L, acct, 20000, 0);
                Thread.sleep(6000);
                showBalance(ontSdk,new Account[]{acct1,acct2,acct3});

                System.exit(0);
            }
            if(false){
                ontSdk.neovm().oep4().sendApprove(acct1, acct2.getAddressU160().toBase58(), 1000000000L, acct, 20000, 0);
                Thread.sleep(6000);
                showBalance(ontSdk,new Account[]{acct1,acct2,acct3});
                System.exit(0);
            }
            if(true){
                ontSdk.neovm().oep4().queryAllowance(acct1.getAddressU160().toBase58(), acct2.getAddressU160().toBase58());
                Thread.sleep(6000);
                showBalance(ontSdk,new Account[]{acct1,acct2,acct3});
            }
            if(false){
                ontSdk.neovm().oep4().sendTransferFrom(acct1, acct2.getAddressU160().toBase58(),acct1.getAddressU160().toBase58(), 1000000000L, acct, 20000, 0);
                Thread.sleep(6000);
                showBalance(ontSdk,new Account[]{acct1,acct2,acct3});
                System.exit(0);
            }

            if(false){
                Account[] accounts = new Account[]{acct1,acct2};
                State[] states = new State[]{new State(acct1.getAddressU160(),acct3.getAddressU160(),100),new State(acct2.getAddressU160(),acct4.getAddressU160(),200)};
                String txhash = ontSdk.neovm().oep4().sendTransferMulti(accounts,states,acct1,20000,0);
                return;
            }

            if(false){ // sender is multi sign addr
                String balance = ontSdk.neovm().nep5().queryBalanceOf(multiAddr);
                System.out.println(new BigInteger(Helper.reverse(Helper.hexToBytes(balance))).longValue());

                Transaction tx = ontSdk.neovm().nep5().makeTransfer(multiAddr,acct1.getAddressU160().toBase58(),10000000L,acct,50000,0);
                ontSdk.addSign(tx,acct);
                ontSdk.addMultiSign(tx,2,new byte[][]{acct.serializePublicKey(),acct2.serializePublicKey()},acct);
                ontSdk.addMultiSign(tx,2,new byte[][]{acct.serializePublicKey(),acct2.serializePublicKey()},acct2);
                Object obj = ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
                System.out.println(obj);
                //   ontSdk.getConnect().sendRawTransaction(tx.toHexString());
                System.out.println(tx.hash().toString());
                System.exit(0);
            }

//            String balance = ontSdk.neovm().oep4().queryBalanceOf(acct.getAddressU160().toBase58());
//            System.out.println(new BigInteger(Helper.reverse(Helper.hexToBytes(balance))).longValue());
//            balance = ontSdk.neovm().oep4().queryBalanceOf(multiAddr);
//            System.out.println(new BigInteger(Helper.reverse(Helper.hexToBytes(balance))).longValue());
//            System.exit(0);



//            System.exit(0);


            String name = ontSdk.neovm().oep4().queryName();
            System.out.println(new String(Helper.hexToBytes(name)));
            String symbol = ontSdk.neovm().oep4().querySymbol();
            System.out.println(new String(Helper.hexToBytes(symbol)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showBalance(OntSdk ontSdk,Account[] accounts) throws Exception {
        for (int i=0;i<accounts.length;i++){
            long balance = ontSdk.neovm().oep4().queryBalanceOf(accounts[i].getAddressU160().toBase58());
            int a = i+1;
            System.out.println("account"+ a +":"+ balance);
        }
    }



    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://139.219.108.204";
        String ip = "http://127.0.0.1";
        ip = "http://polaris3.ont.io";
//        ip= "http://139.219.138.201";
//        String ip = "http://101.132.193.149";
//        String ip = "http://polaris1.ont.io";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.neovm().oep4().setContractAddress("0933f577ff7ad57e1eea69591998578b133d9c6f");
        wm.openWalletFile("nep5.json");


        return wm;
    }
}
