package com.github.ontio.crypto;

import com.github.ontio.common.ErrorCode;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.math.ec.ECCurve;

public enum Curve {
    P224(1, "P-224"),
    P256(2, "P-256"),
    P384(3, "P-384"),
    P512(4, "P-521"),
    SM2P256V1(20, "sm2p256v1");

    private int label;
    private String name;

    private Curve(int v0, String v1) {
        label = v0;
        name = v1;
    }

    public int getLabel() {
        return label;
    }
    @Override
    public String toString() {
        return name;
    }

    public static Curve valueOf(ECCurve v) throws Exception {
        for (Curve c : Curve.values()) {
            if (ECNamedCurveTable.getParameterSpec(c.toString()).getCurve().equals(v)) {
                return c;
            }
        }

        throw new Exception(ErrorCode.UnknownCurve);
    }

    public static Curve fromLabel(int v) throws Exception {
        for (Curve c : Curve.values()) {
            if (c.label == v) {
                return c;
            }
        }

        throw new Exception(ErrorCode.UnknownCurveLabel);
    }
}
