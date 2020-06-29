package com.github.ontio.crypto;

import com.github.ontio.common.ErrorCode;
import com.github.ontio.sdk.exception.SDKException;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
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
            throw new SDKException(ErrorCode.ParamError);
        }

        if (data.length == 65) {
            SignatureScheme[] schemes = SignatureScheme.values();
            if (data[0] > schemes.length) {
                throw new SDKException(ErrorCode.UnsupportedSignatureScheme);
            }
            this.scheme = schemes[data[0]];
        } else if (data.length == 64) { // use default scheme
            byte[] temp = new byte[65];
            temp[0] = 1;
            System.arraycopy(data, 0, temp, 1, 64);
            data = temp;
            this.scheme = SignatureScheme.SHA256WITHECDSA;
        } else {
            throw new Exception(ErrorCode.InvalidSignatureDataLen);
        }
        if (scheme == SignatureScheme.SM3WITHSM2) {
            int i = 0;
            while (i < data.length && data[i] != 0) {
                i++;
            }
            if (i >= data.length) {
                throw new Exception(ErrorCode.InvalidSignatureData);
            }
            this.param = new SM2ParameterSpec(Arrays.copyOfRange(data, 1, i));
            this.value = Arrays.copyOfRange(data, i + 1, data.length);
        } else {
            this.value = Arrays.copyOfRange(data, 1, data.length);
        }
    }

    // serialize to byte array
    public byte[] toBytes() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            bs.write((byte) scheme.ordinal());
            if (scheme == SignatureScheme.SM3WITHSM2) {
                // adding the ID
                bs.write(((SM2ParameterSpec) param).getID());
                // padding a 0 as the terminator
                bs.write((byte) 0);
            }
            bs.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bs.toByteArray();
    }

    public SignatureScheme getScheme() {
        return scheme;
    }

    public byte[] getValue() {
        return this.value;
    }
}
