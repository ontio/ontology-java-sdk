package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.Proof;
import com.github.ontio.ontid.VerifiableCredential;
import com.github.ontio.ontid.VerifiablePresentation;

import java.text.SimpleDateFormat;
import java.util.Date;

@JSONType(orders = {"iss", "sub", "aud", "exp", "nbf", "iat", "jti", "nonce", "vc", "vp"})
public class JWTPayload {
    public long exp; // VerifiableCredential expiration, for example 1541493724
    public String iss; // VerifiableCredential issuer
    public long nbf; // VerifiableCredential issue date, for example 1541493724, 定义在什么时间之前,该jwt都是不可用的
    public String jti; // VerifiableCredential id

    // VerifiableCredential credential id, if there are more than 1 credentialSubject, cannot parse to JWTPayload
    public String sub;

    public Object aud; // audience, may not present, String or String[]
    public long iat; // created date time, same with nbf, for example 1541493724, may not present, jwt的签发时间
    public String nonce; // in case of replay attack, generated form proof, may not present
    public JWTVC vc;
    public JWTVP vp;

    public JWTPayload() {
    }

//    public JWTPayload(VerifiableCredential credential, String audience) throws Exception {
//        this(credential);
//        this.aud = audience;
//    }

    public JWTPayload(VerifiableCredential credential) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (credential.expirationDate != null && !credential.expirationDate.isEmpty()) {
            Date exp = formatter.parse(credential.expirationDate);
            this.exp = exp.getTime() / 1000;
        }
        this.iss = credential.fetchIssuerOntId();
        if (credential.issuanceDate != null && !credential.issuanceDate.isEmpty()) {
            Date nbf = formatter.parse(credential.issuanceDate);
            this.nbf = nbf.getTime() / 1000;
            this.iat = this.nbf;
        }
        this.jti = credential.id;
        String credentialSubjectId = credential.findSubjectId();
        if (!"".equals(credentialSubjectId)) {
            this.sub = credentialSubjectId;
        }
        if (credential.proof != null) {
            this.aud = credential.proof.domain;
            this.nonce = credential.proof.challenge;
        }
        this.vc = new JWTVC(credential);
    }

//    public JWTPayload(VerifiablePresentation presentation, String audience) throws Exception {
//        this(presentation);
//        this.aud = audience;
//    }

    public JWTPayload(VerifiablePresentation presentation, Proof proof) throws Exception {
        if (proof != null) {
            this.aud = proof.domain;
            this.nonce = proof.challenge;
        }
        this.iss = presentation.fetchHolderOntId();
        this.jti = presentation.id;
        this.vp = new JWTVP(presentation, proof);
    }
}
