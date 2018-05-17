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

            ontSdk.neovm().ontId().setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");

            List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();
            if (dids.size() < 2) {
                ontSdk.neovm().ontId().sendRegister("passwordtest","payer",0);
                ontSdk.neovm().ontId().sendRegister("passwordtest","payer",0);
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
