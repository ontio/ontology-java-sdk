package com.github.ontio.common;


import com.alibaba.fastjson.JSON;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class WalletQR {
    public static Map exportIdentityQRCode(Wallet walletFile, Identity identity) throws Exception {
        Control control = identity.controls.get(0);
        String address = identity.ontid.substring(8);
        Map map = new HashMap();
        map.put("type", "I");
        map.put("label",identity.label);
        map.put("key", control.key);
        map.put("parameters", control.parameters);
        map.put("algorithm", "ECDSA");
        map.put("scrypt", walletFile.getScrypt());
        map.put("address",address);
        map.put("salt", control.salt);
        return map;
    }

    public static Map exportAccountQRCode(Wallet walletFile,Account account) throws Exception {
        Map map = new HashMap();
        map.put("type", "A");
        map.put("label", account.label);
        map.put("key", account.key);
        map.put("parameters", account.parameters);
        map.put("algorithm", "ECDSA");
        map.put("scrypt", walletFile.getScrypt());
        map.put("address",account.address);
        map.put("salt", account.salt);
        return map;
    }
    public static String getPriKeyFromQrCode(String qrcode,String password){
        Map map = JSON.parseObject(qrcode,Map.class);
        String key = (String)map.get("key");
        String address = (String)map.get("address");
        String salt = (String)map.get("salt");
        int n = (int)((Map)map.get("scrypt")).get("n");
        try {
            return com.github.ontio.account.Account.getGcmDecodedPrivateKey(key,password,address, Base64.getDecoder().decode(salt),n, SignatureScheme.SHA256WITHECDSA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
