package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @date 2018/3/28
 */
public class AccountDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            AccountInfo info = ontSdk.getWalletMgr().createAccountFromPriKey("passwordtest","e467a2a9c9f56b012c71cf2270df42843a9d7ff181934068b4a62bcdd570e8be");
            System.out.println(info.addressBase58);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {

        String url = "http://127.0.0.1:20384";
//        String url = "http://101.132.193.149:20334";
        OntSdk wm = OntSdk.getInstance();
        wm.setRestfulConnection(url);
        wm.openWalletFile("AccountDemo.json");

        return wm;
    }
}
