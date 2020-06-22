package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.Proof;
import com.github.ontio.ontid.VerifiableCredential;
import com.github.ontio.ontid.VerifiablePresentation;

import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: set nonce

@JSONType(orders = {"sub", "jti", "iss", "nbf", "iat", "exp", "aud", "nonce", "vc", "vp"})
public class JWTPayload {
    public String exp; // VerifiableCredential expiration, for example "1541493724"
    public String iss; // VerifiableCredential issuer
    public String nbf; // VerifiableCredential issue date, for example "1541493724", 定义在什么时间之前,该jwt都是不可用的
    public String jti; // VerifiableCredential id

    // VerifiableCredential credential id, if there are more than 1 credentialSubject, cannot parse to JWTPayload
    public String sub;

    public Object aud; // audience, may not present, String or String[]
    public String iat; // created date time, same with nbf, for example "1541493724", may not present, jwt的签发时间
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
            this.exp = String.valueOf(exp.getTime() / 1000);
        }
        this.iss = credential.fetchIssuerOntId();
        Date nbf = formatter.parse(credential.issuanceDate);
        this.nbf = String.valueOf(nbf.getTime() / 1000);
        this.jti = credential.id;
        String credentialSubjectId = credential.findSubjectId();
        if (!"".equals(credentialSubjectId)) {
            this.sub = credentialSubjectId;
        }
        this.iat = this.nbf;
        if (credential.proof != null) {
            this.aud = credential.proof.domain;
        }
        this.vc = new JWTVC(credential);
    }

//    public JWTPayload(VerifiablePresentation presentation, String audience) throws Exception {
//        this(presentation);
//        this.aud = audience;
//    }

    public JWTPayload(VerifiablePresentation presentation) throws Exception {
        this.iss = presentation.fetchHolderOntId();
        this.jti = presentation.id;
        this.sub = presentation.findSubjectId();
        this.vp = new JWTVP(presentation);
    }

    public JWTPayload(VerifiablePresentation presentation, Object audience, String nonce) throws Exception {
        this.aud = audience;
        this.nonce = nonce;
        this.iss = presentation.fetchHolderOntId();
        this.jti = presentation.id;
        this.sub = presentation.findSubjectId();
        this.vp = new JWTVP(presentation);
    }

    public JWTPayload(VerifiablePresentation presentation, Proof proof, String nonce) throws Exception {
        this.aud = proof.domain;
        this.nonce = nonce;
        this.iss = presentation.fetchHolderOntId();
        this.jti = presentation.id;
        this.sub = presentation.findSubjectId();
        this.vp = new JWTVP(presentation, proof);
    }
}
