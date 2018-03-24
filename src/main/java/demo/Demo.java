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
import com.github.ontio.common.Helper;
import com.github.ontio.core.payload.InvokeCodeTransaction;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import static com.github.ontio.common.Common.print;

/**
 *
 */
public class Demo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
//            System.out.println(ontSdk.getConnectMgr().getBalance("TA5CF29d8T68nALGeQy7BnT37wgjMJNSLA"));
//            System.out.println(Helper.toHexString(ontSdk.getConnectMgr().getBlock(2).transactions[0].sigs[0].sigData[0]));
//            System.out.println(ontSdk.getConnectMgr().getBlock(2).json());
            System.out.println(ontSdk.getConnectMgr().getBlockHeight());
//            System.out.println(ontSdk.getConnectMgr().getBlockJson(1));
//            System.out.println(ontSdk.getConnectMgr().getBlockJson("9ef2746806ed7345ddaad664faf98d8d3fc1cabd21d1acdac57fdc79952e2b94"));
//            System.out.println(ontSdk.getConnectMgr().getGenerateBlockTime());
//            System.out.println(ontSdk.getConnectMgr().getNodeCount());
//            System.out.println(((InvokeCodeTransaction)ontSdk.getConnectMgr().getRawTransaction("c2592940837c2347f6a7b391d4940abb7171dd5dd156b7c031d20a5940142b5a")).fee[0].payer.toBase58());
//            System.out.println(((InvokeCodeTransaction)ontSdk.getConnectMgr().getRawTransaction("c2592940837c2347f6a7b391d4940abb7171dd5dd156b7c031d20a5940142b52")).json());
//            System.out.println(ontSdk.getConnectMgr().getSmartCodeEvent(2));
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
            //System.out.println(block.transactions[0].type);
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
//        String url = "http://54.222.182.88:20334";
//        String url = "http://52.80.167.8:20336";
        String url = "http://127.0.0.1:20384";
        OntSdk wm = OntSdk.getInstance();
        wm.setRestfulConnection(url);
        wm.openWalletFile("Demo3.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        return wm;
    }
}
