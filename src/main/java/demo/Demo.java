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

//00000000faa4e381315e62c404cf5d1f0a3faf8b68d694a69daa8b5d56123a2e33d70c78b4c5389e3b2373e528654d984dc54942ad7bc3ec922fe782d2b77fe9ed3114d8c5234e828d794a6764efb770a3e4dda792da132636563338982daa6768c50cf46c7cbb5a0f0000009b6193044674538402b516cd1c357114686d697660079e1114451748012312020272c2826f07f5e4f310e2f708689548f0d6d0e007603bdc7e6a2512c673db54df014101ba5e98cef3c4e3f90b479aad001f2039be35e26948c39fccd129622b7679ae176bcc7a985f57449ccbcdc053b76c8f25929a49082fa52f5809805e57617f0b1e01000000000000000000cc8322c00d1220150000000000000000000001012312020272c2826f07f5e4f310e2f708689548f0d6d0e007603bdc7e6a2512c673db54df0101410133d0d8e531062dbb3dd1fabda127baefca0137ed46c23de180106d4f06b3bbde07c82c180c2ed1b66c7b50edeea38ae775964632a12cfebe81f28313bfbe9cdf
//00000000faa4e381315e62c404cf5d1f0a3faf8b68d694a69daa8b5d56123a2e33d70c78b4c5389e3b2373e528654d984dc54942ad7bc3ec922fe782d2b77fe9ed3114d8c5234e828d794a6764efb770a3e4dda792da132636563338982daa6768c50cf46c7cbb5a0f0000009b6193044674538402b516cd1c357114686d697660079e1114451748012312020272c2826f07f5e4f310e2f708689548f0d6d0e007603bdc7e6a2512c673db54df014101ba5e98cef3c4e3f90b479aad001f2039be35e26948c39fccd129622b7679ae176bcc7a985f57449ccbcdc053b76c8f25929a49082fa52f5809805e57617f0b1e01000000000000000000cc8322c00d1220150000000000000000000001012312020272c2826f07f5e4f310e2f708689548f0d6d0e007603bdc7e6a2512c673db54df0101410133d0d8e531062dbb3dd1fabda127baefca0137ed46c23de180106d4f06b3bbde07c82c180c2ed1b66c7b50edeea38ae775964632a12cfebe81f28313bfbe9cdf
/**
 *
 */
public class Demo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
//            System.out.println(ontSdk.getConnectMgr().getBalance("TA5NzM9iE3VT9X8SGk5h3dii6GPFQh2vme"));
//            System.out.println(Helper.toHexString(ontSdk.getConnectMgr().getBlock(1).transactions[0].sigs[0].sigData[0]));
            System.out.println(ontSdk.getConnectMgr().getBlock(15).json());
//            System.out.println(ontSdk.getConnectMgr().getGenerateBlockTime());
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
