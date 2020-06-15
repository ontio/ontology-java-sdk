package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;

public class VerifiablePresentation {
    public String[] context;
    public String id;
    public String[] type;
    public VerifiableCredential[] verifiableCredential;
    public Proof[] proof;

    public String genNeedSignData() {
        String id = this.id;
        Proof[] proofs = this.proof;
        this.id = "";
        this.proof = null;
        String jsonStr = JSON.toJSONString(this);
        this.id = id;
        this.proof = proofs;
        return jsonStr;
    }
}
