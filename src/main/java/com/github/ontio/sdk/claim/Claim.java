/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.sdk.claim;

import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.crypto.Digest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.crypto.SignatureScheme;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Claim
 */
public class Claim {
    private String context = "";
    private String id = UUID.randomUUID().toString();
    private Map<String, Object> claim = new HashMap<String, Object>();

    public Claim(SignatureScheme scheme, Account acct, String ctx, Map claimMap, Map metadata,String publicKeyId) {
        context = ctx;
        claim.put("Context", context);
        if (claimMap != null) {
            claim.put("Content", claimMap);
        }
        claim.put("Metadata", new MetaData(metadata).getJson());
        id = Helper.toHexString(Digest.sha256(JSON.toJSONString(claim).getBytes()));
        claim.put("Id", id);
        claim.put("Version", "v1.0");
        DataSignature sign = new DataSignature(scheme, acct, getClaim());
        byte[] signature = sign.signature();
        SignatureInfo info = new SignatureInfo("", "",publicKeyId, signature);
        claim.put("Signature", info.getJson());

    }

    public String getClaim() {
        Map tmp = new HashMap<String, Object>();
        for (Map.Entry<String, Object> e : claim.entrySet()) {
            tmp.put(e.getKey(), e.getValue());
        }
        return JSONObject.toJSONString(tmp);
    }
}

class SignatureInfo {

    private String Format = "pgp";
    private String Algorithm = "ECDSAwithSHA256";
    private byte[] Value;
    private String PublicKeyId;

    public SignatureInfo(String format, String alg ,String publicKeyId,byte[] val) {
        Value = val;
        PublicKeyId = publicKeyId;
    }

    public Object getJson() {
        Map<String, Object> signature = new HashMap<String, Object>();
        signature.put("Format", Format);
        signature.put("Algorithm", Algorithm);
        signature.put("Value", Value);
        signature.put("PublicKeyId", PublicKeyId);
        return signature;
    }
}

class MetaData {

    private String CreateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());//"2017-08-25T10:03:04Z";
    private Map<String, Object> meta = new HashMap();

    public MetaData(Map map) {
        if (map != null) {
            meta = map;
        }
    }

    public Object getJson() {
        meta.put("CreateTime", CreateTime);
        Map tmp = new HashMap<String, Object>();
        for (Map.Entry<String, Object> e : meta.entrySet()) {
            tmp.put(e.getKey(), e.getValue());
        }
        return tmp;
    }
}