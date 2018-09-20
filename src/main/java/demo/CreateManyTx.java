package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;

import java.io.*;

/**
 *
 *
 */
public class CreateManyTx {
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String filePath = "txs.txt";
    public static void main(String[] args){

        try {
            OntSdk ontSdk = getOntSdk();
            com.github.ontio.account.Account payerAcct = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), SignatureScheme.SHA256WITHECDSA);
            if(true) {  //open file, make registry ontid transaction, save tx to file.
                File file = new File(filePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                for (int i = 0; i < 3; i++) {
                    com.github.ontio.account.Account account = new com.github.ontio.account.Account(SignatureScheme.SHA256WITHECDSA);
                    String ontid = Common.didont + account.getAddressU160().toBase58();
                    Transaction tx = ontSdk.nativevm().ontId().makeRegister(ontid, Helper.toHexString(account.serializePublicKey()), payerAcct.getAddressU160().toBase58(), 20000, 500);
                    ontSdk.addSign(tx, account);
                    ontSdk.addSign(tx, payerAcct);
                    System.out.println("PrivateKey:"+Helper.toHexString(account.serializePrivateKey())+",txhash:"+tx.hash().toString());

                    fos.write(tx.toHexString().getBytes());
                    fos.write(",".getBytes());
                    fos.write(tx.hash().toString().getBytes());
                    fos.write("\n".getBytes());
                }
            }
            if(true){ //read transaction from file, send transaction to node
                FileReader fr = new FileReader(filePath);
                BufferedReader bf = new BufferedReader(fr);
                String txHex = null;
                while ((txHex=bf.readLine())!=null){
                    txHex = txHex.split(",")[0];
                    Object obj = ontSdk.getConnect().sendRawTransactionPreExec(txHex);//change to sendRawTransaction
                    System.out.println(obj);
                }
            }
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

        wm.openWalletFile("ClaimDemo.json");

        return wm;
    }
}
