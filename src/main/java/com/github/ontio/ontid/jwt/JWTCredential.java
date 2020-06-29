package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.github.ontio.account.Account;
import com.github.ontio.ontid.Proof;
import com.github.ontio.ontid.VerifiableCredential;
import com.github.ontio.ontid.VerifiablePresentation;
import com.github.ontio.sdk.exception.SDKException;

import java.util.Base64;

@JSONType(orders = {"header", "payload", "jws"})
public class JWTCredential {
    public String jws;
    public JWTHeader header;
    public JWTPayload payload;

    public JWTCredential() {
    }

    // payload.jti need to be recalculated
    // jti should be uuid, not json-ld hash
    public JWTCredential(JWTHeader header, JWTPayload payload, Account signer) throws Exception {
        this.header = header;
        this.payload = payload;
        byte[] needSignData = this.genNeedSignData();
        byte[] sig = signer.generateSignature(needSignData, signer.getSignatureScheme(), null);
        jws = Base64.getEncoder().encodeToString(sig);
    }

    private JWTCredential(String header, String payload, String jws) {
        this.jws = jws;
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedHeader = decoder.decode(header);
        byte[] decodedPayload = decoder.decode(payload);
        this.header = JSON.parseObject(decodedHeader, JWTHeader.class);
        // should parse iss and credentialSubject by field order
        this.payload = JSON.parseObject(decodedPayload, JWTPayload.class, Feature.OrderedField);
    }

    // the proof signature should be jws
    public JWTCredential(VerifiableCredential credential) throws Exception {
        if (credential.proof == null) {
            throw new SDKException("proof is null");
        }
        if (credential.proof.jws == null || credential.proof.jws.isEmpty()) {
            throw new SDKException("credential has no jws");
        }
        this.header = new JWTHeader(credential);
        this.payload = new JWTPayload(credential);
        this.jws = credential.proof.jws;
    }

    // the proof signature should be jws
    public JWTCredential(VerifiablePresentation presentation, Proof proof) throws Exception {
        if (proof == null) {
            throw new SDKException("proof is null");
        }
        if (proof.jws == null || proof.jws.isEmpty()) {
            throw new SDKException("credential has no jws");
        }
        this.jws = proof.jws;
        this.header = new JWTHeader(proof);
        this.payload = new JWTPayload(presentation, proof);
    }

    public static JWTCredential deserializeToJWTCred(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new SDKException("invalid jwt cred");
        }
        return new JWTCredential(parts[0], parts[1], parts[2]);
    }

    public byte[] genNeedSignData() throws Exception {
        String header = Base64.getEncoder().encodeToString(JSON.toJSONString(this.header).getBytes());
        String payload = Base64.getEncoder().encodeToString(JSON.toJSONString(this.payload).getBytes());
        String needSignData = header + "." + payload;
        System.out.println(needSignData);
        return needSignData.getBytes();
    }

    public Proof parseProof() {
        Proof p = null;
        if (this.payload.vc != null) {
            p = this.payload.vc.proof;
        } else if (this.payload.vp != null) {
            p = this.payload.vp.proof;
        }
        Proof proof = new Proof();
        if (p != null) {
            proof.proofPurpose = p.proofPurpose;
            proof.hex = p.hex;
            proof.created = p.created;
        }
        proof.type = this.header.alg.proofPubKeyType();
        proof.verificationMethod = this.header.kid;
        proof.jws = this.jws;
        proof.domain = this.payload.aud;
        proof.challenge = this.payload.nonce;
        return proof;
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
