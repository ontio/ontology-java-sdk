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

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.merkle.MerkleVerifier;
import com.github.ontio.network.rpc.*;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class ClaimDemo {

    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            com.github.ontio.account.Account acct0 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0), ontSdk.defaultSignScheme);
            List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();
            if (dids.size() < 2) {
                Identity identity = ontSdk.getWalletMgr().createIdentity("passwordtest");
                ontSdk.nativevm().ontId().sendRegister(identity,"passwordtest",acct0,0,0);
                identity = ontSdk.getWalletMgr().createIdentity("passwordtest");
                ontSdk.nativevm().ontId().sendRegister(identity,"passwordtest",acct0,0,0);
                dids = ontSdk.getWalletMgr().getIdentitys();
                Thread.sleep(6000);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", dids.get(0).ontid);
            map.put("Subject", dids.get(1).ontid);


            String claim = ontSdk.nativevm().ontId().createOntIdClaim(dids.get(0).ontid,"passwordtest", "claim:context", map, map,map,0);
            System.out.println(claim);
            boolean b = ontSdk.nativevm().ontId().verifyOntIdClaim(claim);
            System.out.println(b);

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

        wm.openWalletFile("ClaimDemo.json");

        return wm;
    }
}
