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
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Contract;
import com.github.ontio.core.asset.State;
import com.github.ontio.core.asset.Transfers;
import com.github.ontio.core.payload.InvokeCode;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.math.BigInteger;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @date 2018/3/30
 */
public class MakeTxWithoutWalletDemo {
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
    public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";
    public static String privatekey6 = "6c2c7eade4c5cb7c9d4d6d85bfda3da62aa358dd5b55de408d6a6947c18b9279";
    public static String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";
    public static String privatekey8 = "87a209d232d6b4f3edfcf5c34434aa56871c2cb204c263f6b891b95bc5837cac";
    public static String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";
    public static String ontContractAddr = "ff00000000000000000000000000000000000001";

    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();

            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            com.github.ontio.account.Account acct0 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0), ontSdk.signatureScheme);
            System.out.println(Helper.toHexString(acct0.serializePublicKey()));

            com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.signatureScheme);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), ontSdk.signatureScheme);
            com.github.ontio.account.Account acct3 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey3), ontSdk.signatureScheme);
            com.github.ontio.account.Account acct4 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey4), ontSdk.signatureScheme);
            com.github.ontio.account.Account acct5 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey5), ontSdk.signatureScheme);

            byte[] pubkey1 = Helper.hexToBytes("120203a4e50edc1e59979442b83f327030a56bffd08c2de3e0a404cefb4ed2cc04ca3e");
            byte[] pubkey2 = Helper.hexToBytes("12020225c98cc5f82506fb9d01bad15a7be3da29c97a279bb6b55da1a3177483ab149b");
            com.github.ontio.account.Account acct11 = new com.github.ontio.account.Account(false, pubkey1);
            com.github.ontio.account.Account acct12 = new com.github.ontio.account.Account(false, pubkey2);

            if (false) {
                //transer
                Address sender = acct0.getAddressU160();
                Address recvAddr = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());
//                Address recvAddr = Address.decodeBase58("TA5SgQXTeKWyN4GNfWGoXqioEQ4eCDFMqE");
                System.out.println("sender:" + sender.toBase58());
                System.out.println("recvAddr:" + recvAddr.toBase58());
                long amount = 1000;

                Transaction tx = ontSdk.nativevm().ont().makeTransfer(sender.toBase58(),recvAddr.toBase58(), amount,sender.toBase58(),30000,0);

                System.out.println(tx.json());
                ontSdk.signTx(tx, new com.github.ontio.account.Account[][]{{acct0}});
                ontSdk.addMultiSign(tx,2,new com.github.ontio.account.Account[]{acct0,acct1});

                System.out.println(tx.hash().toHexString());
                ontSdk.getConnect().sendRawTransaction(tx.toHexString());

            }

            if (false) {
                //sender address From MultiPubKeys
                Address multiAddr = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());
                System.out.println("sender:" + multiAddr);
                Address recvAddr = acct5.getAddressU160();
                System.out.println("recvAddr:" + recvAddr.toBase58());
                int amount = 8;

                Transaction tx = ontSdk.nativevm().ont().makeTransfer(multiAddr.toBase58(),recvAddr.toBase58(), amount,multiAddr.toBase58(),30000,0);
                System.out.println(tx.json());
                //ontSdk.signTx(tx, new com.github.ontio.account.Account[][]{{acct1, acct2}});
                ontSdk.addMultiSign(tx,2,new com.github.ontio.account.Account[]{acct1, acct2});

                System.out.println(tx.hash().toHexString());
                ontSdk.getConnect().sendRawTransaction(tx.toHexString());

            }

            if (true) {
                //2 sender transfer to 1 reveiver
                Address sender1 = acct0.getAddressU160();
                Address sender2 = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());
                Address recvAddr = acct4.getAddressU160();
                System.out.println("sender1:" + sender1.toBase58());
                System.out.println("sender2:" + sender2.toBase58());
                System.out.println("recvAddr:" + recvAddr.toBase58());

                int amount = 1;
                int amount2 = 2;
                State state1 = new State(sender1,recvAddr,amount);
                State state2 = new State(sender2,recvAddr,amount2);
                Transaction tx = ontSdk.nativevm().ont().makeTransfer(new State[]{state1,state2},sender1.toBase58(),30000,0);
                System.out.println(tx.json());
                ontSdk.signTx(tx, new com.github.ontio.account.Account[][]{{acct0}});
                ontSdk.addMultiSign(tx,2,new com.github.ontio.account.Account[]{acct1, acct2});

                System.out.println(tx.hash().toHexString());
                ontSdk.getConnect().sendRawTransaction(tx.toHexString());

            }
            if(false){
                //receiver tx from other, and  add sign
                Address sender = acct0.getAddressU160();
                Address recvAddr = Address.decodeBase58("TA5SgQXTeKWyN4GNfWGoXqioEQ4eCDFMqE");
                System.out.println("sender:" + sender.toBase58());
                System.out.println("recvAddr:" + recvAddr.toBase58());
                long amount = 1000;

                Transaction tx = ontSdk.nativevm().ont().makeTransfer(sender.toBase58(),recvAddr.toBase58(), amount,sender.toBase58(),30000,0);

                //serialize tx data
                String txHex = tx.toHexString();

                //deserialize
                InvokeCode txRx = (InvokeCode)Transaction.deserializeFrom(Helper.hexToBytes(txHex));
                System.out.println(Transfers.deserializeFrom(Contract.deserializeFrom(txRx.code).args).json());
                ontSdk.addSign(txRx,acct0);
                ontSdk.addMultiSign(txRx,2,new com.github.ontio.account.Account[]{acct1,acct2});

                //send tx
                //ontSdk.getConnect().sendRawTransaction(tx.toHexString());

            }
            if(false){
                String claimer = acct0.getAddressU160().toBase58();
                Transaction tx = ontSdk.nativevm().ong().makeClaimOng(claimer,claimer,10,claimer,30000,0);
                ontSdk.signTx(tx, new com.github.ontio.account.Account[][]{{acct0}});
                ontSdk.getConnect().sendRawTransaction(tx.toHexString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
//        String ip = "http://polaris1.ont.io";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20386";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        return wm;
    }
}
