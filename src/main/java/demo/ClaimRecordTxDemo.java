package demo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.sdk.wallet.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimRecordTxDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();

            ontSdk.neovm().ontId().setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");

            List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();
            if (dids.size() < 2) {
                ontSdk.neovm().ontId().sendRegister("passwordtest","payer",0);
                ontSdk.neovm().ontId().sendRegister("passwordtest","payer",0);
                dids = ontSdk.getWalletMgr().getIdentitys();
                Thread.sleep(6000);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", dids.get(0).ontid);
            map.put("Subject", dids.get(1).ontid);


            ontSdk.nativevm().ontId().setCodeAddress("");
            Map clmRevMap = new HashMap();
            clmRevMap.put("typ","AttestContract");
            clmRevMap.put("addr",dids.get(1).ontid.replace(Common.didont,""));
            String claim = ontSdk.nativevm().ontId().createOntIdClaim(dids.get(0).ontid,"passwordtest", "claim:context", map, map,clmRevMap,0);
            System.out.println(claim);

            JSONObject jsonObject = JSON.parseObject(claim);


            System.out.println("ClaimId:" + jsonObject.getString("Id"));

            ontSdk.neovm().claimRecord().setCodeAddress("804d601325908f3fa43c2b11d79a56ef835afb73");

            String commitRes = ontSdk.neovm().claimRecord().sendCommit(dids.get(1).ontid,"passwordtest",jsonObject.getString("Id"),0);
            System.out.println("commitRes:" + commitRes);
            Thread.sleep(6000);

            String getstatusRes = ontSdk.neovm().claimRecord().sendGetStatus(dids.get(1).ontid,"passwordtest",jsonObject.getString("Id"));
            byte[] getstatusResBytes = Helper.hexToBytes(getstatusRes);
            System.out.println("getstatusResBytes:" + new String(getstatusResBytes));
            Thread.sleep(6000);

            String revokeRes = ontSdk.neovm().claimRecord().sendRevoke(dids.get(1).ontid,"passwordtest",jsonObject.getString("Id"),0);
            System.out.println("revokeRes:" + revokeRes);
            Thread.sleep(6000);

            String getstatusRes2 = ontSdk.neovm().claimRecord().sendGetStatus(dids.get(1).ontid,"passwordtest",jsonObject.getString("Id"));
            byte[] getstatusResBytes2 = Helper.hexToBytes(getstatusRes2);
            System.out.println("getstatusResBytes2:" + new String(getstatusResBytes2));

            System.exit(0);


//            boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(claim);
//            System.out.println(b);

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

        wm.openWalletFile("ClaimRecordTxDemo.json");

        return wm;
    }
}

