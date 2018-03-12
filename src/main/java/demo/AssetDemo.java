package demo;

import ontology.OntSdk;
import ontology.sdk.info.account.AccountInfo;

import java.util.Date;

import static ontology.common.Common.print;

/**
 * Created by zx on 2018/1/17.
 */
public class AssetDemo {

    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();

            //String cliamHash = ontSdk.getAssetTx().claimTx(ontSdk.getWalletMgr().getAccounts().get(0).address,"passwordtest","a9f1bf985b1ad4cec8ae372f79879523402711ec70f12d175bdc8daf418eb57d");
            //System.out.println(cliamHash);
            //System.exit(0);

            if(ontSdk.getWalletMgr().getAccounts().size() < 2){
                ontSdk.getWalletMgr().createAccount("passwordtest");
                ontSdk.getWalletMgr().createAccount("passwordtest");
                ontSdk.getWalletMgr().writeWallet();
            }
            //AssetInfo assetinfo = ontSdk.getAssetTx().getAssetInfo("a2663da8fe0b8b7a5995f385cc3a708274da1bab7b7942dd5e5ca622f53aa623");
            //TransactionInfo txinfo = ontSdk.getAssetTx().getTransactionInfo("a2663da8fe0b8b7a5995f385cc3a708274da1bab7b7942dd5e5ca622f53aa623");
//            System.out.println(assetinfo);
//            System.exit(0);
            AccountInfo acct0 = ontSdk.getWalletMgr().getAccountInfo(ontSdk.getWalletMgr().getAccounts().get(0).address,"passwordtest");
            AccountInfo acct1 = ontSdk.getWalletMgr().getAccountInfo(ontSdk.getWalletMgr().getAccounts().get(1).address,"passwordtest");
            System.out.println(acct0.address);

            String hash = ontSdk.getAssetTx().registerTransaction(acct0.address, "passwordtest","JF005", 1000000L, new Date().toString(), acct0.address);
            System.out.println(hash);
            //System.exit(0);

            Thread.sleep(6000);
            System.out.println(acct0.encryptedprikey);
            String assetid = hash;
            String hashIssue = ontSdk.getAssetTx().issueTransaction(acct0.address,"passwordtest",assetid,100,acct0.address,"no");
            System.out.println(hashIssue);
            //System.exit(0);
            Thread.sleep(6000);

            //79b6e2bfb51a0e8f8bfa5823d8896510386ec76738434c836fa96eefe0f7ba08
            String hashTransfer = ontSdk.getAssetTx().transferTransaction(acct0.address, "passwordtest",assetid, 20L, acct1.address, "no");
            //ontoSdk.getConnectManager().getRawTransaction(Helper.reverse("e1c6e42ba3be652328780ce243cea9498204eb00e4fd515af4fdc58b72e9cf38"));
            System.out.println(hashTransfer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OntSdk getOntSdk() throws Exception {

//        String url = "http://127.0.0.1:20334";
        String url = "http://101.132.193.149:21334";
        OntSdk wm = OntSdk.getInstance();
        wm.setBlockChainConfig(url, "");
        //配置 ontid 文件
        wm.openWalletFile("AssetDemo.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        return wm;
    }
}
