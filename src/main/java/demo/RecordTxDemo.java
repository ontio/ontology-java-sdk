package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Identity;

public class RecordTxDemo {


    public static void main(String[] args){
        try {
            OntSdk ontSdk = getOntSdk();

            if(ontSdk.getWalletMgr().getIdentitys().size() < 1) {

                ontSdk.getWalletMgr().createIdentity("passwordtest");
                ontSdk.getWalletMgr().writeWallet();
            }


            Identity id = ontSdk.getWalletMgr().getIdentitys().get(0);

//            String res = ontSdk.getRecordTx().sendPut(id.ontid,"passwordtest","key","value");

            String res = ontSdk.getRecordTx().sendGet(id.ontid,"passwordtest","key");
            System.out.println(res);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20384";
        String rpcUrl = ip + ":" + "20386";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("RecordTxDemo.json");

        wm.setCodeAddress("803ca638069742da4b6871fe3d7f78718eeee78a");
        return wm;
    }
}
