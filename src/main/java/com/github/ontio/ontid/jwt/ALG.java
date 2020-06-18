package com.github.ontio.ontid.jwt;

import com.github.ontio.ontid.Proof;
import com.github.ontio.sdk.exception.SDKException;

public class ALG {

    public static final String ALG_ES256 = "ES256";

    public static Proof.ProofType getProofTypeFromAlg(String alg) throws Exception {
        switch (alg) {
            case ALG_ES256:
                return Proof.ProofType.EcdsaSecp256r1Signature2019;
            default:
                throw new SDKException(String.format("illegal alg %s", alg));
        }
    }
}
