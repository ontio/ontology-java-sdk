package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Identity;

public class RecordTxDemo {


    public static void main(String[] args){
        try {
            OntSdk ontSdk = getOntSdk();

            if(ontSdk.getWalletMgr().getWallet().getIdentities().size() < 1) {

                ontSdk.getWalletMgr().createIdentity("passwordtest");
                ontSdk.getWalletMgr().writeWallet();
            }


            Identity id = ontSdk.getWalletMgr().getWallet().getIdentities().get(0);

            String hash = ontSdk.neovm().record().sendPut(id.ontid,"passwordtest",new byte[]{},"key","value-test",0,0);
            System.out.println(hash);
            Thread.sleep(6000);
            String res = ontSdk.neovm().record().sendGet(id.ontid,"passwordtest",new byte[]{},"key");
            System.out.println("result:"+res);

            //System.out.println(ontSdk.getConnectMgr().getSmartCodeEvent(hash));

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

        wm.openWalletFile("RecordTxDemo.json");

        wm.neovm().record().setContractAddress("80f6bff7645a84298a1a52aa3745f84dba6615cf");
        return wm;
    }
}
