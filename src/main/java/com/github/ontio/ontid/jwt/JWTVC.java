package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.CredentialStatus;
import com.github.ontio.ontid.VerifiableCredential;

@JSONType(orders = {"@context", "type", "credentialSubject", "credentialStatus"})
public class JWTVC {
    @JSONField(name = "@context")
    public String[] context;
    public String[] type;
    public Object credentialSubject;
    public CredentialStatus credentialStatus;

    public JWTVC() {
    }

    public JWTVC(VerifiableCredential credential) {
        this.context = credential.context;
        this.type = credential.type;
        this.credentialStatus = credential.credentialStatus;
        String jsonCredentialSubject = JSON.toJSONString(credential.credentialSubject);
        // remove id attribute
        if (jsonCredentialSubject.startsWith("[")) {
            // credential subject is array
            JSONArray credentialSubject = JSON.parseArray(jsonCredentialSubject);
            for (int i = 0; i < credentialSubject.size(); i++) {
                JSONObject object = credentialSubject.getJSONObject(i);
                object.remove("id");
            }
            this.credentialSubject = credentialSubject;
        } else {
            JSONObject credentialSubject = JSON.parseObject(jsonCredentialSubject);
            credentialSubject.remove("id");
            this.credentialSubject = credentialSubject;
        }
    }
}
