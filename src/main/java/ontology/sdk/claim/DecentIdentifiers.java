package ontology.sdk.claim;

import ontology.account.KeyType;
import ontology.crypto.Digest;
import ontology.common.Helper;
import ontology.account.Acct;
import ontology.sdk.info.account.AccountInfo;
import ontology.core.DataSignature;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zx on 2017/8/25.
 */
public class DecentIdentifiers {
    static String DIDprefix = "did:";
    static String DIDkey = "#key/";
    String Method;
    String context = "http://example.com/context";
    AccountInfo AcctInfo;
    ontology.account.Acct Acct;
    String Name;
    String CertHash;
    String Services = JSONObject.toJSONString(new Object());
    LinkedHashMap<String, Object> DDO = new LinkedHashMap<String, Object>();
    HashMap<String, Object> Cliam = new HashMap<String, Object>();
    String Algrithem;

    public DecentIdentifiers(String alg, AccountInfo acctInfo, ontology.account.Acct acct, String method, String certhash, AccountInfo bankAcctInfo, ontology.account.Acct bankAcct, LinkedHashMap<String, String> services, String name) {
        AcctInfo = acctInfo;
        Acct = acct;
        Name = name;
        CertHash = certhash;
        Algrithem = alg;
        Method = method;

        DDO.put("@context", context);
        DDO.put("id", DIDprefix + Method + ":" + AcctInfo.address);
        if (CertHash != null) {
            DDO.put("cert", createCert(bankAcctInfo, bankAcct, CertHash, Method));
        }
        Object[] owners = new Object[1];
        owners[0] = new Owner(Algrithem, AcctInfo, Acct, Method, 1).getJson();
        DDO.put("owner", owners);

        if (services != null && services.size() > 0) {
            Object obj = new Service(mapSort(services)).getJson();
            Services = JSONObject.toJSONString(obj);
            DDO.put("service", obj);
        }
        DataSignature sign = new DataSignature(Algrithem, Acct, getDDO());
        DDO.put("signature", new Signature(Algrithem, AcctInfo, Method, 1, sign.signature()).getJson());
    }

    public DecentIdentifiers(String alg, AccountInfo acctInfo, ontology.account.Acct acct, String method, String receiverDid, HashMap<String, Object> content) {
        AcctInfo = acctInfo;
        Acct = acct;
        Method = method;
        Algrithem = alg;
        HashMap<String, Object> claim = new HashMap<String, Object>();
        claim.put("id", receiverDid);
        if (content != null) {
            claim.putAll(content);
        }

        Cliam.put("claim", claim);
        Cliam.put("id", UUID.randomUUID().toString());
        Cliam.put("issued", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()));
        Cliam.put("issuer", getDID());
        String[] type = "Credential,ProofOfAuth".split(",");
        Cliam.put("type",type);
        DataSignature sign = new DataSignature(Algrithem, Acct, getClaim());
        Cliam.put("signature", new Signature(Algrithem, AcctInfo, Method, 1, sign.signature()).getJson());
    }

    public String getClaim() {
        return JSONObject.toJSONString(Cliam);
    }

    public LinkedHashMap<String, String> mapSort(LinkedHashMap<String, String> map) {
        LinkedHashMap<String, String> sortMap = new LinkedHashMap<String, String>();
        Object[] key = map.keySet().toArray();
        Arrays.sort(key);
        for (int i = 0; i < key.length; i++) {
            sortMap.put((String) key[i], (String) map.get(key[i]));
        }
        return sortMap;
    }
    public String getName() {
        return Name;
    }

    public String getServices() {
        return Services;
    }

    public String getAddr() {
        return AcctInfo.address;
    }

    public String getMethod() {
        return Method;
    }

    public String getDID() {
        return DIDprefix + Method + ":" + AcctInfo.address;
    }

    public String getDDO() {
        return JSONObject.toJSONString(DDO);
    }

    //    public void parse(String jsonStr){
//        JSONObject.parse(jsonStr);
//    }
    public Object createCert(AccountInfo bankAcctInfo, ontology.account.Acct acct, String certhash, String method) {
        Object obj = new Cert(Algrithem,bankAcctInfo,acct, certhash, getDID(),method).getJson();
        return obj;
    }

    public String getCertHash() {
        return CertHash;
    }
    public static String getCertHash(String idcard) {
        String data = "01" + idcard;
        byte[] hash = data.getBytes();

        try {
            hash = Digest.sha256(hash);
            hash = Digest.sha256( Helper.toHexString(hash).getBytes());
            return Helper.toHexString(hash);
        } catch (Exception var4) {
            throw new RuntimeException("DDO certhash sha256 error");
        }
    }
    public static String getFullDID(String method, String addr, int key) {
        return DecentIdentifiers.DIDprefix + method + ":" + addr + DecentIdentifiers.DIDkey + String.valueOf(key);
    }

}


class Cert  {
    ontology.account.Acct Acct;
    LinkedHashMap<String, Object> certHash = new LinkedHashMap<String, Object>();

    Cert(String Algrithem, AccountInfo bankAcctInfo, ontology.account.Acct acct, String certhash, String did, String method) {
        Acct = acct;
        certHash.put("id", did);
        certHash.put("certHash", certhash);
        certHash.put("signature", null);
        DataSignature sign = new DataSignature(Algrithem, Acct, getData());
        certHash.put("signature", new Signature(Algrithem,bankAcctInfo, method, 1, sign.signature()).getJson());
    }

    public String getData() {
        String data = JSONObject.toJSONString(certHash);
        return data;
    }
    public Object getJson(){
        return certHash;
    }

}

class Service {
    Map<String, String> services = new HashMap<String, String>();

    Service(LinkedHashMap<String, String> services) {
        this.services = services;
    }

    public Object getJson() {
        return services;
    }
}

class Signature {
    public enum Alg {
        RSA, ECDSA, SM2
    }
    private String Type = "EcdsaKoblitzSignature2016";
    private String CreateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());//"2017-08-25T10:03:04Z";
    private String Creator;
    private byte[] Value;

    public Signature(String alg,AccountInfo acct, String method, int key, byte[] signatureValue) {
        Creator = DecentIdentifiers.getFullDID(method, acct.address, key);
        if(alg.equals(KeyType.SM2.name())){
            ConstructSignature(Alg.SM2);
        }else {
            ConstructSignature(Alg.ECDSA);
        }
        this.Value = signatureValue;
    }

    void  ConstructSignature(Alg alg) {
        switch (alg) {
            case RSA:
                Type = "RsaSignature2017";
                break;
            case ECDSA:
                Type = "EcdsaKoblitzSignature2016";
                break;
            case SM2:
                Type = "SM2Signature";
                break;
            default:
                break;
        }
    }

    public Object getJson() {
        Map<String, Object> signature = new LinkedHashMap<String, Object>();
        signature.put("type", Type);
        signature.put("created", CreateTime);
        signature.put("creator", Creator);
        signature.put("signatureValue", Value);
        return signature;
    }
}

class Owner {
    private String id;
    private String[] type = {"CryptographicKey", "EcDsaPublicKey"};
    private String publicKeyBase64 = "AtFwgCRVmpqDAowlx/C/ErCgHqjqnP8Hd0i8VNYk9i9l";
    private String curve = "EcdsaP256r1";

    public Owner(String alg, AccountInfo acctinfo, Acct acct, String method, int key) {
        id = DecentIdentifiers.getFullDID(method, acctinfo.address, key);
        publicKeyBase64 = new String(Base64.getEncoder().encode(acct.publicKey.getEncoded(true)));
        if(alg.equals(KeyType.SM2.name())){
            curve = KeyType.SM2.name();
            type[1] = "SM2PublicKey";
        }
    }

    public Object getJson() {
        Map<String, Object> owner = new LinkedHashMap<String, Object>();
        owner.put("id", id);
        owner.put("type", type);
        owner.put("publicKeyBase64", publicKeyBase64);
        owner.put("curve", curve);
        return owner;
    }
}