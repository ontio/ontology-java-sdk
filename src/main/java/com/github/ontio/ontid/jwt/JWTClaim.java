package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.github.ontio.account.Account;
import com.github.ontio.ontid.VerifiableCredential;
import com.github.ontio.ontid.VerifiablePresentation;
import com.github.ontio.sdk.exception.SDKException;

import java.util.Base64;

@JSONType(orders = {"header", "payload", "jws"})
public class JWTClaim {
    public String jws;
    public JWTHeader header;
    public JWTPayload payload;

    public JWTClaim() {
    }

    // payload.jti need to be recalculated
    // jti should be uuid, not json-ld hash
    public JWTClaim(JWTHeader header, JWTPayload payload, Account signer) throws Exception {
        this.header = header;
        this.payload = payload;
        byte[] needSignData = this.genNeedSignData();
        byte[] sig = signer.generateSignature(needSignData, signer.getSignatureScheme(), null);
        jws = Base64.getEncoder().encodeToString(sig);
    }

    public JWTClaim(String header, String payload, String jws) {
        this.jws = jws;
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedHeader = decoder.decode(header);
        byte[] decodedPayload = decoder.decode(payload);
        this.header = JSON.parseObject(decodedHeader, JWTHeader.class);
        // should parse iss and credentialSubject by field order
        this.payload = JSON.parseObject(decodedPayload, JWTPayload.class, Feature.OrderedField);
    }

    // the proof signature should be jws
    public JWTClaim(VerifiableCredential credential) throws Exception {
        if (credential.proof.jws == null || credential.proof.jws.isEmpty()) {
            throw new SDKException("credential has no jws");
        }
        this.header = new JWTHeader(credential);
        this.payload = new JWTPayload(credential);
        this.jws = credential.proof.jws;
    }

    // the proof signature should be jws
    public JWTClaim(VerifiablePresentation presentation) throws Exception {
        this.jws = presentation.findJWS();
        this.header = new JWTHeader(presentation);
        this.payload = new JWTPayload(presentation);
    }

    public Object parseToJSONLDObject() throws Exception {
        if (this.payload.vc != null) {
            return VerifiableCredential.deserializeFromJWT(this, null);
        }
        if (this.payload.vp != null) {
            return VerifiablePresentation.deserializeFromJWT(this, null);
        }
        throw new SDKException("cannot find vp or vc attribute in payload");
    }

    public static JWTClaim deserializeToJWTClaim(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new SDKException("invalid jwt claim");
        }
        return new JWTClaim(parts[0], parts[1], parts[2]);
    }

    public byte[] genNeedSignData() throws Exception {
        String header = Base64.getEncoder().encodeToString(JSON.toJSONString(this.header).getBytes());
        String payload = Base64.getEncoder().encodeToString(JSON.toJSONString(this.payload).getBytes());
        String needSignData = header + "." + payload;
        return this.header.alg.hash(needSignData.getBytes());
    }

    public byte[] parseSignature() {
        return Base64.getDecoder().decode(this.jws);
    }

    @Override
    public String toString() {
        String header = Base64.getEncoder().encodeToString(JSON.toJSONString(this.header).getBytes());
        String payload = Base64.getEncoder().encodeToString(JSON.toJSONString(this.payload).getBytes());
        return header + "." + payload + "." + jws;
    }
}
