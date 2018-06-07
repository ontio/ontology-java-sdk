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
import com.github.ontio.common.Address;
import com.github.ontio.core.VmType;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;


/**
 *
 */
public class OntAssetDemo {
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
    public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();


            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            com.github.ontio.account.Account payerAcct = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0),ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct0 = payerAcct;
            com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct3 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey3), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct4 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey4), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct5 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey5), ontSdk.defaultSignScheme);
            System.out.println("acct0:" + acct0.getAddressU160().toBase58());
            System.out.println("acct1:" + acct1.getAddressU160().toBase58());
            System.out.println("acct2:" + acct2.getAddressU160().toBase58());



            if (false) {
                ontSdk.nativevm().ong().sendApprove(acct0,acct1.getAddressU160().toBase58(),100,payerAcct,30000,0);
                ontSdk.nativevm().ont().sendTransferFrom(acct0,acct0.getAddressU160().toBase58(),acct1.getAddressU160().toBase58(),10,payerAcct,30000,0);

                System.out.println(ontSdk.nativevm().ong().queryAllowance(acct0.getAddressU160().toBase58(), acct1.getAddressU160().toBase58()));
                System.out.println("acct0:" + ontSdk.getConnect().getBalance(acct0.getAddressU160().toBase58()));
                System.out.println("acct1:" + ontSdk.getConnect().getBalance(acct1.getAddressU160().toBase58()));
                System.out.println("acct2:" + ontSdk.getConnect().getBalance(acct2.getAddressU160().toBase58()));
            }
            if(true){
                String encriptPrivate = "ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=";
                com.github.ontio.account.Account account1 = new com.github.ontio.account.Account(Helper.hexToBytes(com.github.ontio.account.Account.getCtrDecodedPrivateKey(encriptPrivate,"111111","TA4nUbnjX5UGVxkumhfndc7wyemrxdMtn8",16384,SignatureScheme.SHA256WITHECDSA)),SignatureScheme.SHA256WITHECDSA);
                ontSdk.nativevm().ont().sendTransfer(account1,acct0.getAddressU160().toBase58(),10,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                System.out.println(ontSdk.nativevm().ong().unclaimOng(account1.getAddressU160().toBase58()));
                ontSdk.nativevm().ong().claimOng(account1,account1.getAddressU160().toBase58(),49520000000000L,account1,ontSdk.DEFAULT_GAS_LIMIT,0);

            }
            if(false){

                ontSdk.nativevm().ong().sendTransfer(acct0,acct1.getAddressU160().toBase58(),1000L,payerAcct,30000,0);

                String hash0 = ontSdk.nativevm().ont().sendTransfer(acct0, acct1.getAddressU160().toBase58(), 30L, payerAcct, 30000, 0);
            }
            if(false){
                System.out.println(ontSdk.nativevm().ong().unclaimOng(acct0.getAddressU160().toBase58()));
                String hash = ontSdk.nativevm().ong().claimOng(acct0, acct1.getAddressU160().toBase58(), 8960000000000L, payerAcct, 30000, 0);
            }
            if(false) {
                System.out.println(ontSdk.nativevm().ont().queryName());
                System.out.println(ontSdk.nativevm().ont().querySymbol());
                System.out.println(ontSdk.nativevm().ont().queryDecimals());
                System.out.println(ontSdk.nativevm().ont().queryTotalSupply());

                System.out.println(ontSdk.nativevm().ong().queryName());
                System.out.println(ontSdk.nativevm().ong().querySymbol());
                System.out.println(ontSdk.nativevm().ong().queryDecimals());
                System.out.println(ontSdk.nativevm().ong().queryTotalSupply());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRpc());
        wm.openWalletFile("OntAssetDemo.json");
        return wm;
    }
}
