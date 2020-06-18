package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.VerifiablePresentation;

@JSONType(orders = {"@context", "type", "verifiableCredential"})
public class JWTVP {
    @JSONField(name = "@context")
    public String[] context;
    public String[] type;
    public String[] verifiableCredential; // base64url encoded JWTVC as string

    public JWTVP() {
    }

    public JWTVP(String[] verifiableCredential, String[] context, String[] type) {
        this.context = context;
        this.type = type;
        this.verifiableCredential = verifiableCredential;
    }

    public JWTVP(VerifiablePresentation presentation) throws Exception {
        this.context = presentation.context;
        this.type = presentation.type;
        String[] verifiableCredential = new String[presentation.verifiableCredential.length];
        for (int i = 0; i < presentation.verifiableCredential.length; i++) {
            JWTClaim jwtClaim = new JWTClaim(presentation.verifiableCredential[i]);
            verifiableCredential[i] = jwtClaim.toString();
        }
        this.verifiableCredential = verifiableCredential;
    }
}
