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
import com.github.ontio.core.asset.Contract;
import com.github.ontio.core.payload.InvokeCode;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;

import java.util.Base64;


/**
 *
 */
public class OntDemo {
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
    public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";
    public static String privatekey6 = "0bc8c1f75a028672cd42c221bf81709dfc7abbbaf0d87cb6fdeaf9a20492c194";
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            String privatekey0 = "523c5fcf74823831756f0bcb3634234f10b3beb1c05595058534577752ad2d9f";
            com.github.ontio.account.Account payerAcct = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0),ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct0 = payerAcct;
            com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct3 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey3), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct4 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey4), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct5 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey5), ontSdk.defaultSignScheme);
            com.github.ontio.account.Account acct6 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey6), ontSdk.defaultSignScheme);
            System.out.println("acct0:" + acct0.getAddressU160().toBase58());
            System.out.println("acct1:" + acct1.getAddressU160().toBase58());
            System.out.println("acct2:" + acct2.getAddressU160().toBase58());
            System.out.println(acct0.getAddressU160().toBase58());



            if(false){//sendTransferFromMultiSignAddr
                com.github.ontio.account.Account acct00 = new com.github.ontio.account.Account(Helper.hexToBytes("dcb22fdeb1cd57c4ad82c8dc21dd6792d4b1e90b5aa06d6698c03eacddabeb1f"),SignatureScheme.SM3WITHSM2);
                com.github.ontio.account.Account acct11 = new com.github.ontio.account.Account(Helper.hexToBytes("0638dff2f03964883471e1dac3df9e7738f21fd2452aef4846c11a53be6feb0e"),ontSdk.defaultSignScheme);
                com.github.ontio.account.Account acct22 = new com.github.ontio.account.Account(Helper.hexToBytes("46027c9786e24ecc1b4d7b406dfe90ec30b2c2fa6ad2f7963df251200e7f003d"),ontSdk.defaultSignScheme);
                com.github.ontio.account.Account acct33 = new com.github.ontio.account.Account(Helper.hexToBytes("523c5fcf74823831756f0bcb3634234f10b3beb1c05595058534577752ad2d9f"),ontSdk.defaultSignScheme);

//            System.out.println("##"+Helper.toHexString(acct00.serializePublicKey()));
//            System.out.println(Helper.toHexString(acct11.serializePublicKey()));
//            System.out.println(Helper.toHexString(acct22.serializePublicKey()));
//            System.out.println(Helper.toHexString(acct33.serializePublicKey()));

                Address multiAddr = Address.addressFromMultiPubKeys(4,acct00.serializePublicKey(),acct11.serializePublicKey(),acct22.serializePublicKey(),acct33.serializePublicKey());
                System.out.println(multiAddr.toBase58());

                ontSdk.nativevm().ont().sendTransferFromMultiSignAddr(new com.github.ontio.account.Account[]{acct00,acct11,acct22,acct33},4,acct1.getAddressU160().toBase58(),5,acct0,ontSdk.DEFAULT_GAS_LIMIT,0);
                System.exit(0);
            }
            if (false) {
                String hash = ontSdk.nativevm().ont().sendApprove(acct0,acct1.getAddressU160().toBase58(),100,payerAcct,30000,0);
                System.out.println(hash);
//                Thread.sleep(6000);
                System.out.println(ontSdk.nativevm().ont().queryAllowance(acct6.getAddressU160().toBase58(), acct1.getAddressU160().toBase58()));
//                System.out.println("acct0:" + ontSdk.getConnect().getBalance(acct0.getAddressU160().toBase58()));
//                System.out.println("acct1:" + ontSdk.getConnect().getBalance(acct1.getAddressU160().toBase58()));
//                System.out.println("acct2:" + ontSdk.getConnect().getBalance(acct2.getAddressU160().toBase58()));
//                System.out.println(ontSdk.getConnect().getAllowance("ont",acct0.getAddressU160().toBase58(), acct1.getAddressU160().toBase58()));
            }
            if (false) {
                String hash = ontSdk.nativevm().ont().sendTransferFrom(acct1,acct6.getAddressU160().toBase58(),acct1.getAddressU160().toBase58(),100,payerAcct,30000,0);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent(hash));
            }
            if(false){
                System.out.println(ontSdk.nativevm().ont().queryBalanceOf(acct6.getAddressU160().toBase58()));
                //System.out.println(ontSdk.nativevm().ont().queryTotalSupply());
//                System.exit(0);
                String hash = ontSdk.nativevm().ont().sendTransfer(acct0,"AS4VEDn8TvJd3fQuFcHy2NfPqCm5xa7kYk",200,acct0,ontSdk.DEFAULT_GAS_LIMIT,0);
                System.out.println(hash);
                //Thread.sleep(6000);

               // ontSdk.nativevm().ong().claimOng(acct0,acct0.getAddressU160().toBase58(),49520000000000L,acct0,ontSdk.DEFAULT_GAS_LIMIT,0);
            }

            if(false){

                String hash = ontSdk.nativevm().ont().sendTransfer(acct6,acct1.getAddressU160().toBase58(),1000L,payerAcct,30000,0);

//                String hash = ontSdk.nativevm().ont().sendTransfer(acct0, acct1.getAddressU160().toBase58(), 30L, payerAcct, 30000, 0);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent(hash));
            }

            if(true) {
                System.out.println(ontSdk.nativevm().ont().queryName());
                System.out.println(ontSdk.nativevm().ont().querySymbol());
                System.out.println(ontSdk.nativevm().ont().queryDecimals());
                System.out.println(ontSdk.nativevm().ont().queryTotalSupply());
                System.out.println(ontSdk.nativevm().ont().queryBalanceOf(acct0.getAddressU160().toBase58()));
                System.out.println(ontSdk.nativevm().ont().queryAllowance(acct0.getAddressU160().toBase58(),acct1.getAddressU160().toBase58()));

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
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.openWalletFile("OntAssetDemo.json");
        return wm;
    }
}
