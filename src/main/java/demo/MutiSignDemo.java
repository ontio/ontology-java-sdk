package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;

/**
 *
 *
 */
public class MutiSignDemo {
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
    public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";
    public static String privatekey6 = "6c2c7eade4c5cb7c9d4d6d85bfda3da62aa358dd5b55de408d6a6947c18b9279";
    public static String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";
    public static String privatekey8 = "87a209d232d6b4f3edfcf5c34434aa56871c2cb204c263f6b891b95bc5837cac";
    public static String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct3 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey3), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct4 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey4), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct5 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey5), ontSdk.defaultSignScheme);

            com.github.ontio.account.Account[] accounts = new com.github.ontio.account.Account[]{acct1,acct2,acct3};
            int M = 2;
            byte[][] pks = new byte[accounts.length][];
            for(int i=0;i<pks.length;i++){
                pks[i] = accounts[i].serializePublicKey();
            }
            Address sender = Address.addressFromMultiPubKeys(M, pks);
            Address recvAddr = acct5.getAddressU160();

            System.out.println("sender:" + sender.toBase58());
            System.out.println("recvAddr:" + recvAddr.toBase58());
            long amount = 100000;

            Transaction tx = ontSdk.nativevm().ont().makeTransfer(sender.toBase58(),recvAddr.toBase58(), amount,sender.toBase58(),30000,0);

            ontSdk.addMultiSign(tx,M,pks,acct1);

            String txHex1 = tx.toHexString();
            Transaction tx1 = Transaction.deserializeFrom(Helper.hexToBytes(txHex1));
            System.out.println(tx1.sigs[0].json());

            tx.sigs = null;
            ontSdk.addMultiSign(tx,M,pks,acct2);
            String txHex2 = tx.toHexString();
            Transaction tx2 = Transaction.deserializeFrom(Helper.hexToBytes(txHex2));
            System.out.println(tx2.sigs[0].json());

            tx.sigs = null;
            ontSdk.addMultiSign(tx,M,pks,acct3);
            String txHex3 = tx.toHexString();
            Transaction tx3 = Transaction.deserializeFrom(Helper.hexToBytes(txHex3));
            System.out.println(tx3.sigs[0].json());

            tx.sigs = null;
            ontSdk.addMultiSign(tx,M,pks,tx1.sigs[0].sigData[0]);
            ontSdk.addMultiSign(tx,M,pks,tx2.sigs[0].sigData[0]);
            ontSdk.addMultiSign(tx,M,pks,tx3.sigs[0].sigData[0]);
            String txHex4 = tx.toHexString();
            Transaction tx4 = Transaction.deserializeFrom(Helper.hexToBytes(txHex4));
            System.out.println(tx4.sigs[0].json());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://polaris1.ont.io";
//        String ip = "http://dappnode1.ont.io";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("MutiSignDemo.json");
        return wm;
    }
}
