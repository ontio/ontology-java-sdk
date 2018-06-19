package demo;

import com.github.ontio.OntSdk;

import java.util.Map;

public class SignServerDemo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            String txHex = "00d1f8d253d500000000000000003075000000000000c6aa6cf361b3470ac4ee8350b821644bf1aeaec47600c66b14c6aa6cf361b3470ac4ee8350b821644bf1aeaec46a7cc81478e7342fe3823d37be1c890b6fca1213bc53f0536a7cc80864000000000000006a7cc86c07617070726f766514ff000000000000000000000000000000000000010068164f6e746f6c6f67792e4e61746976652e496e766f6b650000";
            Map map = (Map)ontSdk.getSignServer().sendSigRawTx(txHex);
            System.out.println(map.get("signed_tx"));
            String[] signs = new String[]{"1202039b196d5ed74a4d771ade78752734957346597b31384c3047c1946ce96211c2a7",
                    "120203428daa06375b8dd40a5fc249f1d8032e578b5ebb5c62368fc6c5206d8798a966"};
            ontSdk.getSignServer().sendMultiSigRawTx(txHex,2,signs);
            ontSdk.getSignServer().sendSigTransferTx("ont","TU5exRFVqjRi5wnMVzNoWKBq9WFncLXEjK","TA5SgQXTeKWyN4GNfWGoXqioEQ4eCDFMqE",10,30000,0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://139.219.108.204";
        String ip = "http://127.0.0.1";
//        String ip = "http://101.132.193.149";
        String url = ip + ":" + "20000/cli";
        OntSdk wm = OntSdk.getInstance();
        wm.setSignServer(url);
        return wm;
    }
}
