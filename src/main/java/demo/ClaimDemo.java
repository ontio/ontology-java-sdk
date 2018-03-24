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
import com.github.ontio.sdk.wallet.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.ontio.common.Common.print;

/**
 * Created by zx on 2018/1/25.
 */
public class ClaimDemo {

    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();

            ontSdk.setCodeHash("89ff0f39193ddaeeeab9de4873b549f71bbe809c");

            List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();
            if(dids.size() < 2){
                ontSdk.getOntIdTx().register("passwordtest");
                ontSdk.getOntIdTx().register("passwordtest");
                dids = ontSdk.getWalletMgr().getIdentitys();
                Thread.sleep(6000);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", dids.get(0).ontid);
            map.put("Subject", dids.get(1).ontid);

            //密码是签发人的秘密，钱包文件ontid中必须要有该签发人。
            String claim = ontSdk.getOntIdTx().createOntIdClaim("passwordtest","claim:context",map,map);
            System.out.println(claim);
            boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(dids.get(0).ontid,"passwordtest",claim);
            System.out.println(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static OntSdk getOntSdk() throws Exception {

        String url = "http://127.0.0.1:20334";
//        String url = "http://101.132.193.149:20334";
        OntSdk wm = OntSdk.getInstance();
        wm.setRestfulConnection(url);
        wm.openWalletFile("ClaimDemo.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        return wm;
    }
}
