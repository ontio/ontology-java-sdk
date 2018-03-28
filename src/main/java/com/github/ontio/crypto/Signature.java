package com.github.ontio.crypto;

import org.bouncycastle.jcajce.spec.SM2ParameterSpec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class Signature {
    private SignatureScheme scheme;
    private AlgorithmParameterSpec param;
    private byte[] value;

    public Signature(SignatureScheme scheme, AlgorithmParameterSpec param, byte[] signature) {
        this.scheme = scheme;
        this.param = param;
        this.value = signature;
    }

    // parse a serialized bytes to signature structure
    public Signature(byte[] data) throws Exception {
        if (data == null) {
            throw new Exception("null input");
        }

        if (data.length < 2) {
            throw new Exception("invalid signature data length");
        }

        this.scheme = SignatureScheme.values()[data[0]];
        if (scheme == SignatureScheme.SM3WITHSM2) {
            int i = 0;
            while (i < data.length && data[i] != 0){
                i++;
            }
            if (i >= data.length) {
                throw new Exception("invalid signature data: missing the ID parameter for SM3withSM2");
            }
            this.param = new SM2ParameterSpec(Arrays.copyOfRange(data, 1, i));
        }
        this.value = Arrays.copyOfRange(data, 1, data.length);
    }

    // serialize to byte array
    public byte[] toBytes() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            byte[] res = new byte[this.value.length + 1];
            bs.write((byte)scheme.ordinal());
            if (scheme == SignatureScheme.SM3WITHSM2) {
                // adding the ID
                bs.write(((SM2ParameterSpec)param).getID());
                // padding a 0 as the terminator
                bs.write((byte)0);
            }
            bs.write(value);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return bs.toByteArray();
    }

    public SignatureScheme getScheme() { return scheme; }

    public byte[] getValue() {
        return this.value;
    }
}
