package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Identity;

import java.util.ArrayList;
import java.util.List;

public class NativeOntIdDemo {

    public static void main(String[] args) {

        String pwd = "111111";

        try {
            OntSdk ontSdk = getOntSdk();

            if(ontSdk.getWalletMgr().getIdentitys().size() < 2){
                Identity identity = ontSdk.getWalletMgr().createIdentity(pwd);

                ontSdk.nativevm().ontId().sendRegister(identity,pwd,0);

                Thread.sleep(6000);
            }


            List<Identity> dids = new ArrayList<>();
            dids = ontSdk.getWalletMgr().getIdentitys();


            String ddo = ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid);

            System.out.println("ddo:" + ddo);

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

        wm.openWalletFile("NativeOntIdDemo.json");
        return wm;
    }
}
