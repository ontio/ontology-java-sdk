package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.crypto.Digest;
import com.github.ontio.ontid.jwt.JWTClaim;

import java.util.UUID;

@JSONType(orders = {"@context", "id", "type", "verifiableCredential", "holder", "proof"})
public class VerifiablePresentation {
    @JSONField(name = "@context")
    public String[] context;
    public String id;
    public String[] type;
    public VerifiableCredential[] verifiableCredential;
    public Object holder; // holder may not use
    public Proof[] proof;

    public VerifiablePresentation() {
        this.id = "urn:uuid:" + UUID.randomUUID().toString();
    }

    public byte[] genNeedSignData(Proof needSignProof) throws Exception {
        Proof[] proofs = this.proof;
        this.proof = new Proof[]{needSignProof.genNeedSignProof()};
        String jsonStr = JSON.toJSONString(this);
        System.out.println(jsonStr);
        this.proof = proofs;
        return needSignProof.type.getAlg().hash(jsonStr.getBytes());
    }

    public String fetchHolderOntId() {
        return Util.fetchId(holder);
    }

    public static VerifiablePresentation deserializeFromJWT(JWTClaim claim)
            throws Exception {
        VerifiablePresentation presentation = new VerifiablePresentation();
        presentation.context = claim.payload.vp.context;
        presentation.id = claim.payload.jti;
        presentation.type = claim.payload.vp.type;
        // assign issuer
        if (claim.payload.vp.holder == null) {
            presentation.holder = claim.payload.iss;
        } else {
            JSONObject jsonHolder = (JSONObject) JSONObject.toJSON(claim.payload.vp.holder);
            jsonHolder.put("id", claim.payload.iss);
            presentation.holder = jsonHolder;
        }
        presentation.proof = new Proof[]{claim.parseProof()};
        int vcLength = claim.payload.vp.verifiableCredential.length;
        VerifiableCredential[] credentials = new VerifiableCredential[vcLength];
        for (int i = 0; i < vcLength; i++) {
            String vc = claim.payload.vp.verifiableCredential[i];
            JWTClaim vcJWT = JWTClaim.deserializeToJWTClaim(vc);
            VerifiableCredential credential = VerifiableCredential.deserializeFromJWT(vcJWT);
            credentials[i] = credential;
        }
        presentation.verifiableCredential = credentials;
        return presentation;
    }
}
