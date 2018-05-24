package com.github.ontio.smartcontract.nativevm;

import com.github.ontio.OntSdk;

/**
 * @Description:
 * @date 2018/5/24
 */
public class Auth {
    private OntSdk sdk;
    private final String contractAddress = "ff00000000000000000000000000000000000006";
    public Auth(OntSdk sdk) {
        this.sdk = sdk;
    }
}
