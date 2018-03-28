package com.github.ontio.crypto;

import com.github.ontio.common.Helper;
import org.bouncycastle.asn1.*;

import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class SignatureHandler {
    private KeyType type;
    private SignatureScheme scheme;
    private java.security.Signature ctx;

    public SignatureHandler(KeyType type, SignatureScheme scheme) throws Exception {
        this.type = type;
        this.scheme = scheme;

        switch (this.type) {
            case ECDSA:
                switch (scheme) {
                    case SHA224WITHECDSA:
                    case SHA256WITHECDSA:
                    case SHA384WITHECDSA:
                    case SHA512WITHECDSA:
                        ctx = java.security.Signature.getInstance(scheme.toString(), "BC");
                        break;
                    default:
                        throw new Exception("unsupported signature scheme: " + scheme.toString());
                }
                break;
            case SM2:
                if (scheme.compareTo(SignatureScheme.SM3WITHSM2) != 0) {
                    throw new Exception("unsupported signature scheme: " + scheme.toString());
                }
                ctx = java.security.Signature.getInstance(scheme.toString(), "BC");
                break;
            default:
                throw new Exception("unknown key type");
        }

    }

    public byte[] generateSignature(PrivateKey priKey, byte[] msg, AlgorithmParameterSpec param) throws Exception {
        if (param != null) {
            ctx.setParameter(param);
        }
        ctx.initSign(priKey);
        ctx.update(msg);
        byte[] sig = ctx.sign();
        switch (type) {
            case ECDSA:
            case SM2:
                sig = DSADERtoPlain(sig);
                break;
        }

        return sig;
    }

    public boolean verifySignature(PublicKey pubKey, byte[] msg, byte[] sig) throws Exception {
        ctx.initVerify(pubKey);
        ctx.update(msg);
        byte[] v;
        switch (type) {
            case ECDSA:
            case SM2:
                v = DSAPlaintoDER(sig);
                break;
            default:
                v = sig;
                break;
        }
        return ctx.verify(v);
    }

    private byte[] DSADERtoPlain(byte[] sig) throws IOException {
        ASN1Sequence seq = (ASN1Sequence) ASN1Primitive.fromByteArray(sig);
        if (seq.size() != 2) {
            throw new IOException("malformed signature");
        } else if (!Arrays.equals(sig, seq.getEncoded("DER"))) {
            throw new IOException("malformed signature");
        }

        byte[] r = ASN1Integer.getInstance(seq.getObjectAt(0)).getValue().toByteArray();
        byte[] s = ASN1Integer.getInstance(seq.getObjectAt(1)).getValue().toByteArray();
        int ri = (r[0] == 0) ? 1 : 0;
        int rl = r.length - ri;
        int si = (s[0] == 0) ? 1 : 0;
        int sl = s.length - si;
        byte[] res;
        if (rl > sl) {
            res = new byte[rl * 2];
        } else {
            res = new byte[sl * 2];
        }
        System.arraycopy(r, ri, res, res.length/2 - rl, rl);
        System.arraycopy(s, si, res, res.length-sl, sl);
        return res;
    }

    private byte[] DSAPlaintoDER(byte[] sig) throws IOException {
        BigInteger r = new BigInteger(1, Arrays.copyOfRange(sig, 0, sig.length/2));
        BigInteger s = new BigInteger(1, Arrays.copyOfRange(sig, sig.length/2, sig.length));

        ASN1EncodableVector var3 = new ASN1EncodableVector();
        var3.add(new ASN1Integer(r));
        var3.add(new ASN1Integer(s));
        return (new DERSequence(var3)).getEncoded("DER");
    }
}
