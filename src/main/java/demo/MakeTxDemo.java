package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @date 2018/3/30
 */
public class MakeTxDemo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            String password = "passwordtest";
            if(false) {
                Account info1 = null;
                Account info2 = null;
                Account info3 = null;
                if (ontSdk.getWalletMgr().getAccounts().size() < 3) {
                    info1 = ontSdk.getWalletMgr().createAccountFromPriKey("passwordtest", "9a31d585431ce0aa0aab1f0a432142e98a92afccb7bcbcaff53f758df82acdb3");
                    info2 = ontSdk.getWalletMgr().createAccount("passwordtest");
                    info3 = ontSdk.getWalletMgr().createAccount("passwordtest");
                    ontSdk.getWalletMgr().writeWallet();
                }
                info1 = ontSdk.getWalletMgr().getAccounts().get(0);
                info2 = ontSdk.getWalletMgr().getAccounts().get(1);
                Transaction tx = ontSdk.getOntAssetTx().makeTransfer("ont", info1.address, "passwordtest", info2.address, 100L);
                ontSdk.signTx(tx, info1.address, password);
                System.out.println(tx.toHexString());
                ontSdk.getConnectMgr().sendRawTransaction(tx.toHexString());

            }

            if(true) {
                String attri = "attri";
                Identity ident = null;
                if (ontSdk.getWalletMgr().getIdentitys().size() == 0) {
                    ident = ontSdk.getOntIdTx().sendRegister("passwordtest");
                } else {
                    ident = ontSdk.getWalletMgr().getIdentitys().get(0);
                }
                Map recordMap = new HashMap();
                recordMap.put("key0", "world0");
                recordMap.put("key1", 1);
                recordMap.put("keyNum", 1234589);
                recordMap.put("key2", false);
                String ontid = ident.ontid;
                ontSdk.setCodeAddress("80e7d2fc22c24c466f44c7688569cc6e6d6c6f92");
                Transaction tx = ontSdk.getOntIdTx().makeUpdateAttribute(ontid, "passwordtest", attri.getBytes(), "Json".getBytes(), JSON.toJSONString(recordMap).getBytes());
                ontSdk.signTx(tx, ontid, password);
                ontSdk.getConnectMgr().sendRawTransaction(tx.toHexString());

                //Thread.sleep(6000);
                //String ddo = ontSdk.getOntIdTx().sendGetDDO(ontid);
               // System.out.println(ddo);
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
        String rpcUrl = ip + ":" + "20386";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("OntAssetDemo.json");
        return wm;
    }
}
