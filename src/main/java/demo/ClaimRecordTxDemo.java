package demo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;
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

            Account payerAccInfo = ontSdk.getWalletMgr().createAccount(password);
            com.github.ontio.account.Account payerAcc = ontSdk.getWalletMgr().getAccount(payerAccInfo.address,password,payerAccInfo.getSalt());


            if (ontSdk.getWalletMgr().getWallet().getIdentities().size() < 2) {
                Identity identity = ontSdk.getWalletMgr().createIdentity(password);

                ontSdk.nativevm().ontId().sendRegister(identity.ontid,"",null,payerAcc,payerAcc,ontSdk.DEFAULT_GAS_LIMIT,0);

                Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);

                ontSdk.nativevm().ontId().sendRegister(identity2.ontid,"",null,payerAcc,payerAcc,ontSdk.DEFAULT_GAS_LIMIT,0);

                ontSdk.getWalletMgr().writeWallet();

                Thread.sleep(6000);
            }

            List<Identity> dids = ontSdk.getWalletMgr().getWallet().getIdentities();


            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", dids.get(0).ontid);
            map.put("Subject", dids.get(1).ontid);

            Map clmRevMap = new HashMap();
            clmRevMap.put("typ","AttestContract");
            clmRevMap.put("addr",dids.get(1).ontid.replace(Common.didont,""));

            String claim = ontSdk.nativevm().ontId().createOntIdClaim(dids.get(0).ontid,password,dids.get(0).controls.get(0).getSalt(), "claim:context", map, map,clmRevMap,System.currentTimeMillis()/1000 +100000);
            System.out.println(claim);

            boolean b = ontSdk.nativevm().ontId().verifyOntIdClaim(claim);
            System.out.println(b);

//            System.exit(0);

            Account account = ontSdk.getWalletMgr().importAccount("blDuHRtsfOGo9A79rxnJFo2iOMckxdFDfYe2n6a9X+jdMCRkNUfs4+C4vgOfCOQ5","111111","AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve",Base64.getDecoder().decode("0hAaO6CT+peDil9s5eoHyw=="));
            AccountInfo info = ontSdk.getWalletMgr().getAccountInfo(account.address,"111111",account.getSalt());
            com.github.ontio.account.Account account1 = new com.github.ontio.account.Account(Helper.hexToBytes("75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf"),SignatureScheme.SHA256WITHECDSA);


            String[] claims = claim.split("\\.");

            JSONObject payload = JSONObject.parseObject(new String(Base64.getDecoder().decode(claims[1].getBytes())));

            System.out.println("ClaimId:" + payload.getString("jti"));

//            ontSdk.neovm().claimRecord().setContractAddress("9a4c79ee4379a0b5d10db03553ca7e61e17a8977");
            //
//            String getstatusRes9 = ontSdk.neovm().claimRecord().sendGetStatus(payload.getString("jti"));
//            System.out.println("getstatusResBytes:" + getstatusRes9);

            String commitHash = ontSdk.neovm().claimRecord().sendCommit(dids.get(0).ontid,password,dids.get(0).controls.get(0).getSalt(),dids.get(1).ontid,payload.getString("jti"),account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            System.out.println("commitRes:" + commitHash);
            Thread.sleep(6000);
            Object obj = ontSdk.getConnect().getSmartCodeEvent(commitHash);
            System.out.println(obj);


            System.out.println(Helper.toHexString(dids.get(0).ontid.getBytes()));
            System.out.println(Helper.toHexString(dids.get(1).ontid.getBytes()));
            System.out.println(Helper.toHexString(payload.getString("jti").getBytes()));
            System.out.println(payload.getString("jti"));


            String getstatusRes = ontSdk.neovm().claimRecord().sendGetStatus(payload.getString("jti"));
            System.out.println("getstatusResBytes:" + getstatusRes);
            Thread.sleep(6000);

//            System.exit(0);

            String revokeHash = ontSdk.neovm().claimRecord().sendRevoke(dids.get(0).ontid,password,dids.get(0).controls.get(0).getSalt(),payload.getString("jti"),account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            System.out.println("revokeRes:" + revokeHash);
            Thread.sleep(6000);
            System.out.println(ontSdk.getConnect().getSmartCodeEvent(revokeHash));


            String getstatusRes2 = ontSdk.neovm().claimRecord().sendGetStatus(payload.getString("jti"));

            System.out.println("getstatusResBytes2:" + getstatusRes2);

            System.exit(0);


//            boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(claim);
//            System.out.println(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://polaris1.ont.io";
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

