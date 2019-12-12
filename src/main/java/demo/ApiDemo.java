package demo;

import com.github.ontio.OntSdk;

/**
 * @Description:
 * @date 2019/12/12
 */
public class ApiDemo {

    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            if (true) {
                System.out.println(ontSdk.getConnect().getBalance("AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe"));
                System.out.println(ontSdk.getConnect().getNodeSyncStatus());
                System.exit(0);
            }
        }catch (Exception ex){
        }
    }
    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://polaris1.ont.io";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRpc());
        wm.openWalletFile("wallet2.dat");

        return wm;
    }
}
