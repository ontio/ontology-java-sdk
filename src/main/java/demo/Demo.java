package demo;

import ontology.OntSdk;
import ontology.account.Acct;
import ontology.account.KeyType;
import ontology.core.contract.ContractParameterType;
import ontology.sdk.info.account.AccountInfo;
import ontology.sdk.wallet.Account;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static ontology.common.Common.print;

/**
 * Created by zx on 2018/1/25.
 */
public class Demo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            ontSdk.getWalletMgr().createAccount("123456");
            System.out.println(ontSdk.getWalletMgr().getWallet());
            System.out.println(ontSdk.getWalletMgr().openWallet());
            System.exit(0);
            System.out.println(ontSdk.getWalletMgr().getWallet().getAccounts().get(0));
            ontSdk.getWalletMgr().getWallet().removeAccount(ontSdk.getWalletMgr().getWallet().getAccounts().get(0).address);
            ontSdk.getWalletMgr().writeWallet();
            System.out.println(ontSdk.getWalletMgr().getWallet());
            System.exit(0);
            //Block block = ontSdk.getConnectManager().getBlock(757);
            System.out.println(ontSdk.getConnectMgr().getNodeCount());
          //  System.out.println(ontSdk.getConnectManager().getGenerateBlockTime());
            //System.out.println(block.transactions[0].type);
           // ontSdk.getOepMgr().getAccount("1234567",ontSdk.getOepMgr().getAccounts().get(0).address);

            Account info = ontSdk.getWalletMgr().createAccount("123456");
            ontSdk.getWalletMgr().writeWallet();
         //   ontSdk.getOepMgr().createOntId("123456");
          //  AccountInfo info2 = ontSdk.getWalletMgr().getAccountInfo("123456", info.address);
          //  System.out.println(info2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
        String url = "http://54.222.182.88:20334";
//        String url = "http://127.0.0.1:20334";
//        String url = "http://101.132.193.149:20334";
        OntSdk wm = OntSdk.getInstance();
        wm.setBlockChainConfig(url, "");
        wm.openWalletFile("Demo3.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        return wm;
    }
}
