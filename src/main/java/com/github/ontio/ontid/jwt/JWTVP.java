package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.Proof;
import com.github.ontio.ontid.VerifiablePresentation;
import com.github.ontio.sdk.exception.SDKException;

@JSONType(orders = {"@context", "type", "challenge", "verifiableCredential", "holder", "proof"})
public class JWTVP {
    @JSONField(name = "@context")
    public String[] context;
    public String[] type;
    public String[] verifiableCredential; // base64url encoded JWTVC as string
    public Object holder;
    public Proof proof;

    public JWTVP() {
    }

    public JWTVP(VerifiablePresentation presentation, Proof proof) throws Exception {
        if (presentation.holder.getClass().isPrimitive() || presentation.holder.getClass().isArray() ||
                presentation.holder instanceof JSONArray) {
            throw new SDKException("illegal presentation holder");
        }
        if (!(presentation.holder instanceof String)) {
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(presentation.holder);
            jsonObject.remove("id");
            if (jsonObject.size() > 0) {
                this.holder = jsonObject;
            }
        }
        this.context = presentation.context;
        this.type = presentation.type;
        String[] verifiableCredential = new String[presentation.verifiableCredential.length];
        for (int i = 0; i < presentation.verifiableCredential.length; i++) {
            JWTClaim jwtClaim = new JWTClaim(presentation.verifiableCredential[i]);
            verifiableCredential[i] = jwtClaim.toString();
        }
        this.verifiableCredential = verifiableCredential;
        this.proof = proof.genNeedSignProof();
        this.proof.hex = proof.hex;
        this.proof.verificationMethod = null;
        this.proof.type = null;
    }
}
