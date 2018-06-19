package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;

public class WalletDemo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            if (ontSdk.getWalletMgr().getWallet().getAccounts().size() > 0) {
                ontSdk.getWalletMgr().getWallet().clearAccount();
                ontSdk.getWalletMgr().getWallet().clearIdentity();
                ontSdk.getWalletMgr().writeWallet();
            }
            ontSdk.getWalletMgr().createAccounts(1, "passwordtest");
            ontSdk.getWalletMgr().writeWallet();

            System.out.println("init size: "+ontSdk.getWalletMgr().getWallet().getAccounts().size()+" " +ontSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(ontSdk.getWalletMgr().getWallet().toString());
            System.out.println(ontSdk.getWalletMgr().getWalletFile().toString());

            System.out.println();
            ontSdk.getWalletMgr().getWallet().removeAccount(ontSdk.getWalletMgr().getWallet().getAccounts().get(0).address);
            ontSdk.getWalletMgr().getWallet().setVersion("2.0");
            System.out.println("removeAccount size: "+ontSdk.getWalletMgr().getWallet().getAccounts().size()+" " +ontSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(ontSdk.getWalletMgr().getWallet().toString());
            System.out.println(ontSdk.getWalletMgr().getWalletFile().toString());

            System.out.println();
            ontSdk.getWalletMgr().resetWallet();
            System.out.println("resetWallet size: "+ontSdk.getWalletMgr().getWallet().getAccounts().size()+" " +ontSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(ontSdk.getWalletMgr().getWallet().toString());
            System.out.println(ontSdk.getWalletMgr().getWalletFile().toString());


            System.out.println();
            ontSdk.getWalletMgr().getWallet().removeAccount(ontSdk.getWalletMgr().getWallet().getAccounts().get(0).address);
            ontSdk.getWalletMgr().getWallet().setVersion("2.0");
            System.out.println("removeAccount size: "+ontSdk.getWalletMgr().getWallet().getAccounts().size()+" " +ontSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(ontSdk.getWalletMgr().getWallet().toString());
            System.out.println(ontSdk.getWalletMgr().getWalletFile().toString());

            //write wallet
            ontSdk.getWalletMgr().writeWallet();
            System.out.println();
            System.out.println("writeWallet size: "+ontSdk.getWalletMgr().getWallet().getAccounts().size()+" " +ontSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(ontSdk.getWalletMgr().getWallet().toString());
            System.out.println(ontSdk.getWalletMgr().getWalletFile().toString());
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

        wm.openWalletFile("WalletDemo.json");

        return wm;
    }
}
