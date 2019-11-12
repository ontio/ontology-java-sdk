package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.network.exception.RestfulException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.Oep4;

import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @date 2019/11/7
 */
public class Oep4MultiTheadDemo {
    static String ip1 = "http://polarisErr.ont.io";
    static String ip2 = "http://polaris2.ont.io";
    static String ip3 = "http://polaris3.ont.io";
    static String ip4 = "http://polaris4.ont.io";
    static List<String> nodes = Arrays.asList(ip1, ip2, ip3, ip4);
    static String curIp = ip1;

    public static void main(String[] args) {
        try {
            OntSdk ontSdk = Oep4MultiTheadDemo.getOntSdk();
            for (int i = 0; i < 1; i++) {
                startThread(ontSdk, "Thread 1", "55e02438c938f6f4eb15a9cb315b26d0169b7fd7");
                startThread(ontSdk, "Thread 2", "547d89289f75648cbda8c1c8ccf4e83ebd01240a");
                startThread(ontSdk, "Thread 3", "25277b421a58cfc2ef5836767e54eb7abdd31afd");
                startThread(ontSdk, "Thread 4", "9f612aff420d11dc781be892545346607d13fd8f");
                startThread(ontSdk, "Thread 5", "e32a9cfcb91737e27246493d8a067d438d1f650e");
            }
            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startThread(OntSdk ontSdk, String threadName, String contractAddress) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                String tmpIp = "";
                for (int i = 0; i < 10000; i++) {
                    try {
                        tmpIp = curIp;  // this is important
                        Oep4 oep4 = new Oep4(ontSdk);
                        oep4.setContractAddress(contractAddress);
                        String tokenname = oep4.queryName();
                        String total = oep4.queryTotalSupply();
                        System.out.println(threadName + ": " + tokenname + " " + total);
                        Thread.sleep(10);
                    } catch (RestfulException e) {
                        System.err.println(e.getMessage());
                        switchNode(ontSdk,tmpIp);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }
        };
        thread.start();
    }

    public static void switchNode(OntSdk ontSdk,String reqIp) {
        try {
            if(!reqIp.equals(curIp)){
                return;
            }
            for (int i = 0; i < nodes.size(); i++) {
                String ip = nodes.get(i);
                if (i == nodes.size() - 1) {
                    curIp = nodes.get(0);
                    break;
                } else if (ip.equals(curIp)) {
                    curIp = nodes.get(i + 1);
                    break;
                }
            }
            String restUrl = curIp + ":" + "20334";
            ontSdk.setRestful(restUrl);
            ontSdk.setDefaultConnect(ontSdk.getRestful());
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
        String restUrl = curIp + ":" + "20334";
        String rpcUrl = curIp + ":" + "20336";
        String wsUrl = curIp + ":" + "20335";
        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.openWalletFile("AccountDemo.json");
        return wm;
    }
}
