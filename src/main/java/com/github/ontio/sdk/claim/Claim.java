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

import com.github.ontio.account.Acct;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.crypto.Digest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zx on 2017/11/27.
 */
public class Claim {
    private String context = "";
    private String id = UUID.randomUUID().toString();
    private Map<String, Object> claim = new HashMap<String, Object>();

    public Claim(String alg, Acct acct, String ctx, Map claimMap, String issuer, String subject, Map metadata) {
        context = ctx;
        claim.put("Context", context);
        if (claimMap != null) {
            claim.put("Content", claimMap);
        }
        claim.put("Metadata", new MetaData(issuer, subject, metadata).getJson());
        id = Helper.toHexString(Digest.sha256(JSON.toJSONString(claim).getBytes()));
        claim.put("Id", id);
        DataSignature sign = new DataSignature(alg, acct, getClaim());
        claim.put("Signature", new Sign("", "", sign.signature()).getJson());

    }

    public String getClaim() {
        return JSONObject.toJSONString(claim);
    }
}

class Sign {

    private String Format = "pgp";
    private String Algorithm = "ECDSAwithSHA256";
    private byte[] Value;

    public Sign(String format, String alg, byte[] val) {
        Value = val;
    }

    public Object getJson() {
        Map<String, Object> signature = new HashMap<String, Object>();
        signature.put("Format", Format);
        signature.put("Algorithm", Algorithm);
        signature.put("Value", Value);
        return signature;
    }
}

class MetaData {

    private String CreateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());//"2017-08-25T10:03:04Z";
    private String Issuer = "";
    private String Subject = "";
    private Map meta = new HashMap();

    public MetaData(String issuer, String subject, Map map) {
        if (map != null) {
            meta = map;
        }
        Issuer = issuer;
        Subject = subject;
    }

    public Object getJson() {
        meta.put("CreateTime", CreateTime);
        meta.put("Issuer", Issuer);
        meta.put("Subject", Subject);
        return meta;
    }
}