/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.manager.ECIES;

/**
 *
 */
public class ECIESDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();

            com.github.ontio.account.Account account = new com.github.ontio.account.Account(Helper.hexToBytes("9a31d585431ce0aa0aab1f0a432142e98a92afccb7bcbcaff53f758df82acdb3"), ontSdk.defaultSignScheme);
            System.out.println("PrivateKey:"+Helper.toHexString(account.serializePrivateKey()));
            System.out.println("PublicKey:"+Helper.toHexString(account.serializePublicKey()));
//            System.out.println(Helper.toHexString(account.serializePrivateKey()));

            byte[] msg = new String("1234567890").getBytes();
            String[] ret = ECIES.Encrypt(Helper.toHexString(account.serializePublicKey()),msg);
            byte[] msg2 = ECIES.Decrypt(Helper.toHexString(account.serializePrivateKey()),ret);
//            byte[] msg3 = ECIES.Decrypt(account,ret);
            System.out.println("Msg:"+Helper.toHexString(msg));
            System.out.println("Encrypted:"+JSON.toJSONString(ret));
            System.out.println("Decrypt:"+Helper.toHexString(msg2));
//            System.out.println(Helper.toHexString(msg3));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("Demo3.json");

        return wm;
    }
}
