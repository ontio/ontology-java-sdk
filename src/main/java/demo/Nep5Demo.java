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
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class Nep5Demo {
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
    public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.keyType, ontSdk.curveParaSpec);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), ontSdk.keyType, ontSdk.curveParaSpec);
            com.github.ontio.account.Account acct3 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey3), ontSdk.keyType, ontSdk.curveParaSpec);
            com.github.ontio.account.Account acct4 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey4), ontSdk.keyType, ontSdk.curveParaSpec);
            com.github.ontio.account.Account acct5 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey5), ontSdk.keyType, ontSdk.curveParaSpec);

            Account acct = ontSdk.getWalletMgr().createAccountFromPriKey("passwordtest","c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07");
            ontSdk.getWalletMgr().writeWallet();
            System.out.println("recv:"+acct.address);
            String txhash = ontSdk.getNep5Tx().sendTransfer(acct.address,"passwordtest",acct1.getAddressU160().toBase58(),10);
//            String txhash = ontSdk.getNep5Tx().sendInit(acct.address,"passwordtest",false);
//            System.out.println(txhash);
//            System.out.println(acct1.getAddressU160().toHexString());
            //  System.out.println(info2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://139.219.108.204";
        String ip = "http://127.0.0.1";
//        String ip = "http://101.132.193.149";
//        String ip = "http://polaris1.ont.io";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.setCodeAddress("80f022bc6febeaeb0045f25a510a3a100572d3c2");
        wm.openWalletFile("nep5.json");


        return wm;
    }
}
