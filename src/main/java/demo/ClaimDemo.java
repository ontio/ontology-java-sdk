package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.ontio.common.Common.print;

/**
 * Created by zx on 2018/1/25.
 */
public class ClaimDemo {

    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();

            ontSdk.setCodeHash("89ff0f39193ddaeeeab9de4873b549f71bbe809c");

            List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();
            if(dids.size() < 2){
                ontSdk.getOntIdTx().register("passwordtest");
                ontSdk.getOntIdTx().register("passwordtest");
                dids = ontSdk.getWalletMgr().getIdentitys();
                Thread.sleep(6000);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", dids.get(0).ontid);
            map.put("Subject", dids.get(1).ontid);

            //密码是签发人的秘密，钱包文件ontid中必须要有该签发人。
            String claim = ontSdk.getOntIdTx().createOntIdClaim("passwordtest","claim:context",map,map);
            System.out.println(claim);
            boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(dids.get(0).ontid,"passwordtest",claim);
            System.out.println(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static OntSdk getOntSdk() throws Exception {

        String url = "http://127.0.0.1:20334";
//        String url = "http://101.132.193.149:20334";
        OntSdk wm = OntSdk.getInstance();
        wm.setRestfulConnection(url);
        wm.openWalletFile("ClaimDemo.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        return wm;
    }
}
