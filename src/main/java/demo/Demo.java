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
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.util.List;
import java.util.Map;


/**
 *
 */
public class Demo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
//            System.out.println(ontSdk.getConnectMgr().getBalance("TA5NzM9iE3VT9X8SGk5h3dii6GPFQh2vme"));
//            System.out.println(Helper.toHexString(ontSdk.getConnectMgr().getBlock(1).transactions[0].sigs[0].sigData[0]));
//            System.out.println(ontSdk.getConnectMgr().getBlock(1584).json());
            System.out.println(ontSdk.getConnectMgr().getGenerateBlockTime());
//            System.out.println(ontSdk.getConnectMgr().getBlockHeight());
//            System.out.println(ontSdk.getConnectMgr().getBlockJson(1));
//            System.out.println(ontSdk.getConnectMgr().getBlockJson("ee2d842fe7cdf48bc39b34d616a9e8f7f046970ed0a988dde3fe05c9126cce74"));
//            System.out.println(ontSdk.getConnectMgr().getGenerateBlockTime());
//            System.out.println(ontSdk.getConnectMgr().getNodeCount());
//            System.out.println(((InvokeCodeTransaction)ontSdk.getConnectMgr().getRawTransaction("c2592940837c2347f6a7b391d4940abb7171dd5dd156b7c031d20a5940142b5a")).fee[0].payer.toBase58());
//            System.out.println((ontSdk.getConnectMgr().getTransaction("d441a967315989116bf0afad498e4016f542c1e7f8605da943f07633996c24cc")));
//            System.out.println(ontSdk.getConnectMgr().getSmartCodeEvent(59));
//            System.out.println(ontSdk.getConnectMgr().getContractJson("80e7d2fc22c24c466f44c7688569cc6e6d6c6f92"));
            System.exit(0);
            List list = (List) ontSdk.getConnectMgr().getSmartCodeEvent("a12117c319aa6906efd8869ba65c221f4e2ee44a8a2766fd326c8d7125beffbf");

            List states = (List) ((Map) (list.get(0))).get("States");
            List state1 = (List) states.get(0);

            byte[] bys = new byte[state1.toArray().length];
            for (int i = 0; i < bys.length; i++) {
                bys[i] = (byte) ((int) state1.get(i) & 0xff);
            }
            System.out.println(Address.parse(Helper.toHexString(bys)).toBase58());
            System.exit(0);


            ontSdk.getWalletMgr().createAccount("123456");
            System.out.println(ontSdk.getWalletMgr().getWallet());
            System.out.println(ontSdk.getWalletMgr().openWallet());
            System.exit(0);
            System.out.println(ontSdk.getWalletMgr().getWallet().getAccounts().get(0));
            ontSdk.getWalletMgr().getWallet().removeAccount(ontSdk.getWalletMgr().getWallet().getAccounts().get(0).address);
            ontSdk.getWalletMgr().writeWallet();
            System.out.println(ontSdk.getWalletMgr().getWallet());
            ontSdk.getWalletMgr().getWallet().setName("name");

            System.exit(0);
            Account acct = ontSdk.getWalletMgr().createAccount("password");
            Identity identity = ontSdk.getWalletMgr().createIdentity("password");
            //Block block = ontSdk.getConnectManager().getBlock(757);
            System.out.println(ontSdk.getConnectMgr().getNodeCount());
            //  System.out.println(ontSdk.getConnectManager().getGenerateBlockTime());
            // ontSdk.getOepMgr().getAccount(ontSdk.getOepMgr().getAccounts().get(0).address,"1234567");

            Account info = ontSdk.getWalletMgr().createAccount("123456");
            ontSdk.getWalletMgr().writeWallet();
            //   ontSdk.getOepMgr().createOntId("123456");
            //  AccountInfo info2 = ontSdk.getWalletMgr().getAccountInfo(info.address,"123456");
            //  System.out.println(info2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
//        String url = "http://54.222.182.88:20336";
//        String url = "http://127.0.0.1:20386";
        String url = "http://127.0.0.1:20384";
        OntSdk wm = OntSdk.getInstance();
        wm.setRestfulConnection(url);
        wm.openWalletFile("Demo3.json");

        return wm;
    }
}
