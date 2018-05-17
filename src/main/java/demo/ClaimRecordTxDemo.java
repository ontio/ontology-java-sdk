package demo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.sdk.wallet.Identity;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimRecordTxDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();

            String password = "111111";

            ontSdk.nativevm().ontId().setCodeAddress("ff00000000000000000000000000000000000003");

            List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();
            if (dids.size() < 2) {
                Identity identity = ontSdk.getWalletMgr().createIdentity(password);

                ontSdk.nativevm().ontId().sendRegister(identity,password,0);

                Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);

                ontSdk.nativevm().ontId().sendRegister(identity2,password,0);

                dids = ontSdk.getWalletMgr().getIdentitys();
                Thread.sleep(6000);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", dids.get(0).ontid);
            map.put("Subject", dids.get(1).ontid);

            Map clmRevMap = new HashMap();
            clmRevMap.put("typ","AttestContract");
            clmRevMap.put("addr",dids.get(1).ontid.replace(Common.didont,""));

            String claim = ontSdk.nativevm().ontId().createOntIdClaim(dids.get(0).ontid,password, "claim:context", map, map,clmRevMap,System.currentTimeMillis()/1000 +100000);
            System.out.println(claim);

            boolean b = ontSdk.nativevm().ontId().verifyOntIdClaim(claim);
            System.out.println(b);

            System.exit(0);

            String[] claims = claim.split("\\.");

            JSONObject payload = JSONObject.parseObject(new String(Base64.getDecoder().decode(claims[1].getBytes())));

            System.out.println("ClaimId:" + payload.getString("jti"));

            ontSdk.neovm().claimRecord().setCodeAddress("806256c36653d4091a3511d308aac5c414b2a444");

            String commitRes = ontSdk.neovm().claimRecord().sendCommit(dids.get(1).ontid,password,payload.getString("jti"),0);
            System.out.println("commitRes:" + commitRes);
            Thread.sleep(6000);

            String getstatusRes = ontSdk.neovm().claimRecord().sendGetStatus(dids.get(1).ontid,password,payload.getString("jti"));
            byte[] getstatusResBytes = Helper.hexToBytes(getstatusRes);
            System.out.println("getstatusResBytes:" + new String(getstatusResBytes));
            Thread.sleep(6000);

            String revokeRes = ontSdk.neovm().claimRecord().sendRevoke(dids.get(1).ontid,password,payload.getString("jti"),0);
            System.out.println("revokeRes:" + revokeRes);
            Thread.sleep(6000);

            String getstatusRes2 = ontSdk.neovm().claimRecord().sendGetStatus(dids.get(1).ontid,password,payload.getString("jti"));
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

