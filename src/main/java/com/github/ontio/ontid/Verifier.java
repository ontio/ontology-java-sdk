package com.github.ontio.ontid;

// TODO: verify new specification
public class Verifier {
    public boolean verifyOntIdCredible(String[] CredibleOntIds, VerifiableCredential claim) {
        return true;
    }

    public boolean verifyExpiration(VerifiableCredential claim) {
        return true;
    }

    public boolean verifySignature(VerifiableCredential claim) {
        return true;
    }

    public boolean verifyClaimRevoked(VerifiableCredential claim) {
        return true;
    }
}
