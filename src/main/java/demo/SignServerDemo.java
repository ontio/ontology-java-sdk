package demo;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Identity;

import java.util.Map;

public class SignServerDemo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            String password = "111111";
            String privateKey = "75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf";
            String privateKey2 = "ca53fa4f53ed175e39da86f4e02cd87638652cdbdcdae594c81d2e2f2f673745";
            Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
            if(false){

                Account account2 = new Account(Helper.hexToBytes(privateKey2),SignatureScheme.SHA256WITHECDSA);
                System.out.println("account:" +  ontSdk.getConnect().getBalance(account.getAddressU160().toBase58()));
                System.out.println("account2:" +  ontSdk.getConnect().getBalance(account2.getAddressU160().toBase58()));
                Object obj = ontSdk.getSignServer().sendSigTransferTx("ont",account.getAddressU160().toBase58(),account2.getAddressU160().toBase58(),10,30000,0);

                String signedTx = ((JSONObject)obj).getString("signed_tx");
                Transaction tx = Transaction.deserializeFrom(Helper.hexToBytes(signedTx));
                ontSdk.getConnect().sendRawTransaction(signedTx);
                Thread.sleep(6000);
                System.out.println("account:" +  ontSdk.getConnect().getBalance(account.getAddressU160().toBase58()));
                System.out.println("account2:" +  ontSdk.getConnect().getBalance(account2.getAddressU160().toBase58()));
            }

            if(true){
                Identity identity = ontSdk.getWalletMgr().createIdentity(password);
                Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,identity.controls.get(0).getSalt(),account.getAddressU160().toBase58(),ontSdk.DEFAULT_GAS_LIMIT,0);

//                ontSdk.getSignServer().sendSigNativeInvokeTx(ontSdk.nativevm().ontId().getContractAddress(),,"1.0",ontSdk.DEFAULT_GAS_LIMIT,0,);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://139.219.108.204";
        String ip = "http://127.0.0.1";
//        String ip = "http://101.132.193.149";
        String url = ip + ":" + "20000/cli";
        OntSdk wm = OntSdk.getInstance();
        wm.setSignServer(url);
        wm.setRpc("http://127.0.0.1:20336");
        return wm;
    }
}
