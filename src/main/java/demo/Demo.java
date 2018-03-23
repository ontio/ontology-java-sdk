package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.core.payload.InvokeCodeTransaction;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import static com.github.ontio.common.Common.print;

/**
 * Created by zx on 2018/1/25.
 */
public class Demo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            //System.out.println(ontSdk.getConnectMgr().getBalance("TA5CF29d8T68nALGeQy7BnT37wgjMJNSLA"));
//            System.out.println(ontSdk.getConnectMgr().getBlock(2));
//            System.out.println(ontSdk.getConnectMgr().getBlock("1771dcef251fdbab5851d20ee268ad527a6ff314ba15d827490c6c92002b68c9").transactions[1].sigs[0].M);
//            System.out.println(ontSdk.getConnectMgr().getBlockHeight());
//            System.out.println(ontSdk.getConnectMgr().getBlockJson(1));
//            System.out.println(ontSdk.getConnectMgr().getBlockJson("1771dcef251fdbab5851d20ee268ad527a6ff314ba15d827490c6c92002b68c9"));
//            System.out.println(ontSdk.getConnectMgr().getGenerateBlockTime());
            //System.out.println(ontSdk.getConnectMgr().getNodeCount());
//            System.out.println(((InvokeCodeTransaction)ontSdk.getConnectMgr().getRawTransaction("dbb261e34730050e43d4122766c407c4768e376222862845ffcf8517cc50a223")).fee[0].payer.toBase58());
            System.exit(0);


            ontSdk.getWalletMgr().createAccount("123456");
            System.out.println(ontSdk.getWalletMgr().getWallet());
            System.out.println(ontSdk.getWalletMgr().openWallet());
            System.exit(0);
            System.out.println(ontSdk.getWalletMgr().getWallet().getAccounts().get(0));
            ontSdk.getWalletMgr().getWallet().removeAccount(ontSdk.getWalletMgr().getWallet().getAccounts().get(0).address);
            ontSdk.getWalletMgr().writeWallet();
            System.out.println(ontSdk.getWalletMgr().getWallet());
            ontSdk.getWalletMgr().getWallet().setName("name");

            System.exit(0);
            Account acct = ontSdk.getWalletMgr().createAccount("password");
            Identity identity = ontSdk.getWalletMgr().createIdentity("password");
            //Block block = ontSdk.getConnectManager().getBlock(757);
            System.out.println(ontSdk.getConnectMgr().getNodeCount());
          //  System.out.println(ontSdk.getConnectManager().getGenerateBlockTime());
            //System.out.println(block.transactions[0].type);
           // ontSdk.getOepMgr().getAccount(ontSdk.getOepMgr().getAccounts().get(0).address,"1234567");

            Account info = ontSdk.getWalletMgr().createAccount("123456");
            ontSdk.getWalletMgr().writeWallet();
         //   ontSdk.getOepMgr().createOntId("123456");
          //  AccountInfo info2 = ontSdk.getWalletMgr().getAccountInfo(info.address,"123456");
          //  System.out.println(info2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
//        String url = "http://54.222.182.88:20334";
        String url = "http://127.0.0.1:20386";
//        String url = "http://101.132.193.149:20334";
        OntSdk wm = OntSdk.getInstance();
        wm.setRpcConnection(url);
        wm.openWalletFile("Demo3.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        return wm;
    }
}
