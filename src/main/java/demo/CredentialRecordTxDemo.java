package demo;


import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CredentialRecordTxDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();

            String password = "passwordtest";

            com.github.ontio.account.Account payer = ontSdk.getWalletMgr().getAccount(
                    "AUNB7xQuBVg8hnRfVz9pyAuZQUqPBiDxDF", password);

            Identity issuerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity("did:ont:AJ4C9aTYxTGUhEpaZdPjFSqCqzMCqJDRUd");
            Identity subjectIdentity = ontSdk.getWalletMgr().getWallet().getIdentity("did:ont:AVe4zVZzteo6HoLpdBwpKNtDXLjJBzB9fv");
//            String txhash = ontSdk.nativevm().ontId().sendRegister(issuerIdentity.ontid, payer, payer, 20000000, 500);
//            showEvent(ontSdk, "sendRegister", txhash);
//            txhash = ontSdk.nativevm().ontId().sendRegister(subjectIdentity.ontid, payer, payer, 20000000, 500);
//            showEvent(ontSdk, "sendRegister", txhash);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", issuerIdentity.ontid);
            map.put("Subject", subjectIdentity.ontid);

            Map clmRevMap = new HashMap();
            clmRevMap.put("typ", "AttestContract");
            clmRevMap.put("addr", subjectIdentity.ontid.replace(Common.didont, ""));

            ontSdk.neovm().credentialRecord().setContractAddress("52df370680de17bc5d4262c446f102a0ee0d6312");
            String claim = ontSdk.nativevm().ontId().createOntIdClaim(issuerIdentity.ontid, payer,
                    "claim:context", map, map, clmRevMap, System.currentTimeMillis() / 1000 + 100000);
            System.out.println(claim);
            String[] claims = claim.split("\\.");

            JSONObject payload = JSONObject.parseObject(new String(Base64.getDecoder().decode(claims[1].getBytes())));
            String claimId = payload.getString("jti");
            System.out.println(Helper.toHexString(issuerIdentity.ontid.getBytes()));
            System.out.println(Helper.toHexString(subjectIdentity.ontid.getBytes()));
            System.out.println(Helper.toHexString(claimId.getBytes()));
            System.out.println("ClaimId:" + claimId);

            boolean b = ontSdk.nativevm().ontId().verifyOntIdClaim(claim);
            System.out.println(b);

            Account account = ontSdk.getWalletMgr().importAccount(
                    "blDuHRtsfOGo9A79rxnJFo2iOMckxdFDfYe2n6a9X+jdMCRkNUfs4+C4vgOfCOQ5",
                    "111111", "AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve",
                    Base64.getDecoder().decode("0hAaO6CT+peDil9s5eoHyw=="));
            System.out.println(account.address);
            String commitHash = ontSdk.neovm().credentialRecord().sendCommit2(issuerIdentity.ontid, password,
                    issuerIdentity.controls.get(0).getSalt(), subjectIdentity.ontid, claimId, 1,
                    payer, ontSdk.DEFAULT_GAS_LIMIT, 500);
            System.out.println("commitRes:" + commitHash);
            Thread.sleep(6000);
            Object obj = ontSdk.getConnect().getSmartCodeEvent(commitHash);
            System.out.println(obj);

            String[] credibleOntId = new String[]{
                    issuerIdentity.ontid, subjectIdentity.ontid
            };
            boolean ontIdCredible = ontSdk.nativevm().ontId().verifyCredOntIdCredible(claim, credibleOntId);
            System.out.println("claim ontIdCredible: " + ontIdCredible);
            boolean notExpired = ontSdk.nativevm().ontId().verifyCredNotExpired(claim);
            System.out.println("claim notExpired: " + notExpired);
            boolean signatureValid = ontSdk.nativevm().ontId().verifyCredSignature(claim);
            System.out.println("claim signatureValid: " + signatureValid);
            boolean notRevoked = ontSdk.nativevm().ontId().verifyCredNotRevoked(claim);
            System.out.println("claim notRevoked: " + notRevoked);

            String res = ontSdk.neovm().credentialRecord().sendGetStatus2(claimId);
            System.out.println("before revoke: " + res);
            Thread.sleep(6000);
            String revokeHash = ontSdk.neovm().credentialRecord().sendRevoke2(subjectIdentity.ontid, password,
                    subjectIdentity.controls.get(0).getSalt(), claimId, 1, payer, ontSdk.DEFAULT_GAS_LIMIT,
                    500);
            System.out.println("revokeRes: " + revokeHash);
            Thread.sleep(6000);
            System.out.println(ontSdk.getConnect().getSmartCodeEvent(revokeHash));
            String revoked = ontSdk.neovm().credentialRecord().sendGetStatus2(claimId);
            System.out.println("after revoke: " + revoked);

            String removeHash = ontSdk.neovm().credentialRecord().sendRemove2(subjectIdentity.ontid, password,
                    subjectIdentity.controls.get(0).getSalt(), claimId, 1, payer, ontSdk.DEFAULT_GAS_LIMIT,
                    500);
            System.out.println("removeRes: " + removeHash);
            Thread.sleep(6000);
            System.out.println(ontSdk.getConnect().getSmartCodeEvent(removeHash));
            String removed = ontSdk.neovm().credentialRecord().sendGetStatus2(claimId);
            System.out.println("after remove: " + removed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://polaris1.ont.io";
//        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("wallet.json");

        return wm;
    }

    public static void showEvent(OntSdk ontSdk, String methodName, String txHash) throws InterruptedException,
            SDKException, ConnectorException, IOException {
        System.out.println("methodName: " + methodName + " txHash: " + txHash);
        Thread.sleep(6000);
        System.out.println("event: " + ontSdk.getRestful().getSmartCodeEvent(txHash));
        System.out.println("");
    }
}

