package demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.asset.State;
import com.github.ontio.core.transaction.Transaction;

import java.math.BigInteger;
import java.util.*;

class UserAcct{
    String id;
    String address;
    String withdrawAddr;
    byte[] privkey;
    BigInteger ontBalance;
    BigInteger ongBalance;
}

class Balance{
    @JSONField(name="ont")
    String ont;
    @JSONField(name="ong")
    String ong;

    public String getOnt() {
        return ont;
    }

    public void setOnt(String ont) {
        this.ont = ont;
    }

    public String getOng() {
        return ong;
    }

    public void setOng(String ong) {
        this.ong = ong;
    }
}


class States{
    @JSONField(name="States")
    Object[] states;

    @JSONField(name="ContractAddress")
    String contractAddress;

    public Object[] getStates() {
        return states;
    }

    public void setStates(Object[] states) {
        this.states = states;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

}

class Event{
    @JSONField(name="GasConsumed")
    int gasConsumed;


    @JSONField(name="TxHash")
    String txHash;

    @JSONField(name="State")
    int state;

    @JSONField(name="Notify")
    States[] notify;

    public int getGasConsumed() {
        return gasConsumed;
    }

    public void setGasConsumed(int gasConsumed) {
        this.gasConsumed = gasConsumed;
    }


    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public States[] getNotify() {
        return notify;
    }

    public void setNotify(States[] notify) {
        this.notify = notify;
    }
}



public class ExchangeDemo {

    //init account should have some onts
    public static final String INIT_ACCT_ADDR = "Ad4pjz2bqep4RhQrUAzMuZJkBC3qJ1tZuT";
    public static final String INIT_ACCT_SALT = "OkX96EG0OaCNUFD3hdc50Q==";

    public static final String FEE_PROVIDER = "AS3SCXw8GKTEeXpdwVw7EcC4rqSebFYpfb";
    public static final String FEE_PROVIDER_SALT = "KvKkxNOGm4q4bLkD8TS2PA==";

    //for test all account's pwd is the same
    public static final String PWD = "123456";

    //for generate a multi-sig address
    public static final String MUTI_SIG_ACCT_SEED1_ADDR = "AK98G45DhmPXg4TFPG1KjftvkEaHbU8SHM";
    public static final String MUTI_SIG_ACCT_SEED1_SALT = "rD4ewxv4qHH8FbUkUv6ePQ==";

    public static final String MUTI_SIG_ACCT_SEED2_ADDR = "ALerVnMj3eNk9xe8BnQJtoWvwGmY3x4KMi";
    public static final String MUTI_SIG_ACCT_SEED2_SALT = "1K8a7joYQ+iwj3/+wGICrw==";

    public static final String MUTI_SIG_ACCT_SEED3_ADDR = "AKmowTi8NcAMjZrg7ZNtSQUtnEgdaC65wG";
    public static final String MUTI_SIG_ACCT_SEED3_SALT = "b9oBYBIPvZMw66q1ky+JDQ==";

    //withdraw address for test user
    public static final String WITHDRAW_ADDRESS = "AZbcPX7HyJTWjqogZhnr2qDTh6NNksGSE6";


    public static  String ONT_NATIVE_ADDRESS = "";
    public static  String ONG_NATIVE_ADDRESS = "";

    public static void main(String[] args) {
        try{
            //simulate a database using hashmap
            HashMap<String,UserAcct> database = new HashMap<String,UserAcct>();

            OntSdk ontSdk = getOntSdk();
            ONT_NATIVE_ADDRESS = Helper.reverse(ontSdk.nativevm().ont().getContractAddress());
            ONG_NATIVE_ADDRESS = Helper.reverse(ontSdk.nativevm().ong().getContractAddress());

            printlog("++++ starting simulate exchange process ...========");
            printlog("++++ 1. create a random account for user ====");
            String id1 = "id1";
            Account acct1 = new Account(ontSdk.defaultSignScheme);
            String pubkey =  acct1.getAddressU160().toBase58();
            byte[] privkey = acct1.serializePrivateKey();
            printlog("++++ public key is " + acct1.getAddressU160().toBase58());

            UserAcct usr =getNewUserAcct(id1,pubkey,privkey,BigInteger.valueOf(0),BigInteger.valueOf(0));
            usr.withdrawAddr = WITHDRAW_ADDRESS;
            database.put(acct1.getAddressU160().toBase58(),usr);
            //all transfer fee is provide from this account
            Account feeAct = ontSdk.getWalletMgr().getAccount(FEE_PROVIDER,PWD,Base64.getDecoder().decode(FEE_PROVIDER_SALT));

            //create a multi-sig account as a main account
            Account mutiSeedAct1 = ontSdk.getWalletMgr().getAccount(MUTI_SIG_ACCT_SEED1_ADDR,PWD,Base64.getDecoder().decode(MUTI_SIG_ACCT_SEED1_SALT));
            Account mutiSeedAct2 = ontSdk.getWalletMgr().getAccount(MUTI_SIG_ACCT_SEED2_ADDR,PWD,Base64.getDecoder().decode(MUTI_SIG_ACCT_SEED2_SALT));
            Account mutiSeedAct3 = ontSdk.getWalletMgr().getAccount(MUTI_SIG_ACCT_SEED3_ADDR,PWD,Base64.getDecoder().decode(MUTI_SIG_ACCT_SEED3_SALT));

            Address mainAccountAddr = Address.addressFromMultiPubKeys(3,mutiSeedAct1.serializePublicKey(),mutiSeedAct2.serializePublicKey(),mutiSeedAct3.serializePublicKey());
            printlog("++++ Main Account Address is :" + mainAccountAddr.toBase58());


            //monitor the charge and withdraw thread
            Thread t = new Thread(new Runnable() {

                long lastblocknum = 0 ;

                @Override
                public void run() {
                    while(true){
                        try{
                            //get latest blocknum:
                            //TODO fix lost block
                            int height = ontSdk.getConnect().getBlockHeight();
                            if (height > lastblocknum){
                                printlog("====== new block sync :" + height);

                                Object  event = ontSdk.getConnect().getSmartCodeEvent(height);
                                if(event == null){
                                    lastblocknum = height;
                                    Thread.sleep(1000);
                                    continue;
                                }
                                printlog("====== event is " + event.toString());

                                List<Event> events = JSON.parseArray(event.toString(), Event.class);
                                if(events == null){
                                    lastblocknum = height;
                                    Thread.sleep(1000);
                                    continue;
                                }
                                if (events.size()> 0){
                                    for(Event ev:events){
                                        printlog("===== State:" + ev.getState());
                                        printlog("===== TxHash:" + ev.getTxHash());
                                        printlog("===== GasConsumed:" + ev.getGasConsumed());

                                        for(States state:ev.notify){

                                            printlog("===== Notify - ContractAddress:" + state.getContractAddress());
                                            printlog("===== Notify - States[0]:" + state.getStates()[0]);
                                            printlog("===== Notify - States[1]:" + state.getStates()[1]);
                                            printlog("===== Notify - States[2]:" + state.getStates()[2]);
                                            printlog("===== Notify - States[3]:" + state.getStates()[3]);

                                            if (ev.getState() == 1){  //exec succeed
                                                Set<String> keys = database.keySet();
                                                //
                                                if ("transfer".equals(state.getStates()[0]) && keys.contains(state.getStates()[2])){
                                                    BigInteger amount = new BigInteger(state.getStates()[3].toString());
                                                    if (ONT_NATIVE_ADDRESS.equals(state.getContractAddress())){
                                                        printlog("===== charge ONT :"+state.getStates()[2] +" ,amount:"+amount);
                                                        database.get(state.getStates()[2]).ontBalance = amount.add(database.get(state.getStates()[2]).ontBalance);
                                                    }
                                                    if (ONG_NATIVE_ADDRESS.equals(state.getContractAddress())){
                                                        printlog("===== charge ONG :"+state.getStates()[2] +" ,amount:"+amount);
                                                        database.get(state.getStates()[2]).ongBalance = amount.add(database.get(state.getStates()[2]).ongBalance);
                                                    }
                                                }

                                                //withdraw case
                                                if("transfer".equals(state.getStates()[0]) && mainAccountAddr.toBase58().equals(state.getStates()[1])){

                                                    for(UserAcct ua: database.values()){
                                                        if (ua.withdrawAddr.equals((state.getStates()[2]))){
                                                            BigInteger amount = new BigInteger(state.getStates()[3].toString());
                                                            if (ONT_NATIVE_ADDRESS.equals(state.getContractAddress())){
                                                                printlog("===== widtdraw "+ amount +" ont to " + ua.withdrawAddr + " confirmed!");
                                                            }
                                                            if (ONG_NATIVE_ADDRESS.equals(state.getContractAddress())){
                                                                printlog("===== widtdraw "+ amount +" ong to " + ua.withdrawAddr + " confirmed!");
                                                            }

                                                        }
                                                    }

                                                }

                                            }

                                        }

                                    }
                                }

                                lastblocknum = height;


                            }
                            Thread.sleep(1000);

                        }catch(Exception e){
                            printlog("exception 1:"+ e.getMessage());
                        }
                    }
                }
            });

            //monitor the collect
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        while (true){

                            Set<String> keys = database.keySet();

                            List<Account> ontAccts = new ArrayList<Account>() ;
                            List<State> ontStates = new ArrayList<State>();
                            List<Account> ongAccts = new ArrayList<Account>() ;
                            List<State> ongStates = new ArrayList<State>();


                            for(String key:keys){
                                Object balance = ontSdk.getConnect().getBalance(key);
                                printlog("----- balance of " + key + " : " + balance);
                                Balance b = JSON.parseObject(balance.toString(),Balance.class);
                                BigInteger ontbalance = new BigInteger(b.ont);
                                BigInteger ongbalance = new BigInteger(b.ong);

                                if (ontbalance.compareTo(new BigInteger("0")) > 0){
                                    //transfer ont to main wallet
                                    UserAcct ua = database.get(key);
                                    Account acct = new Account(ua.privkey,ontSdk.defaultSignScheme);
                                    ontAccts.add(acct);
                                    State st = new State(Address.addressFromPubKey(acct.serializePublicKey()),mainAccountAddr,ua.ontBalance.longValue());
                                    ontStates.add(st);
                                }

                                if (ongbalance.compareTo(new BigInteger("0")) > 0){
                                    //transfer ong to main wallet
                                    UserAcct ua = database.get(key);
                                    Account acct = new Account(ua.privkey,ontSdk.defaultSignScheme);
                                    ongAccts.add(acct);
                                    State st = new State(Address.addressFromPubKey(acct.serializePublicKey()),mainAccountAddr,ua.ongBalance.longValue());
                                    ongStates.add(st);
                                }
                            }

                            //construct ont transfer tx
                            if (ontStates.size() > 0) {
                                printlog("----- Will collect ont to main wallet");
                                Transaction ontTx = ontSdk.nativevm().ont().makeTransfer(ontStates.toArray(new State[ontStates.size()]), FEE_PROVIDER, 30000, 0);
                                for (Account act : ontAccts) {
                                    ontSdk.addSign(ontTx, act);
                                }
                                //add fee provider account sig
                                ontSdk.addSign(ontTx, feeAct);
                                ontSdk.getConnect().sendRawTransaction(ontTx.toHexString());
                            }

                            //construct ong transfer tx
                            if(ongStates.size() > 0) {
                                printlog("----- Will collect ong to main wallet");
                                Transaction ongTx = ontSdk.nativevm().ong().makeTransfer(ongStates.toArray(new State[ongStates.size()]), FEE_PROVIDER, 30000, 0);
                                for (Account act : ongAccts) {
                                    ontSdk.addSign(ongTx, act);
                                }
                                //add fee provider account sig
                                ontSdk.addSign(ongTx, feeAct);
                                ontSdk.getConnect().sendRawTransaction(ongTx.toHexString());
                            }

                            Thread.sleep(10000);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        printlog("exception 2:"+e.getMessage());
                    }

                }
            });

            t.start();
            t2.start();

            Thread.sleep(2000);
            printlog("++++ 2. charge some ont to acct1 from init account");
            Account initAccount = ontSdk.getWalletMgr().getAccount(INIT_ACCT_ADDR,PWD,Base64.getDecoder().decode(INIT_ACCT_SALT));
            State st = new State(initAccount.getAddressU160(),acct1.getAddressU160(),1000L);
            Transaction tx = ontSdk.nativevm().ont().makeTransfer(new State[]{st}, FEE_PROVIDER, 30000, 0);
            ontSdk.addSign(tx,initAccount);
            ontSdk.addSign(tx, feeAct);

            ontSdk.getConnect().sendRawTransaction(tx.toHexString());
            // test is the tx in txpool
            String txhash = tx.hash().toHexString();
            printlog("++++ txhash :"+txhash);
            Object event = ontSdk.getConnect().getMemPoolTxState(txhash);
            printlog(event.toString());


            printlog("++++ 3. charge some ong to acct1 from init account");
            st = new State(initAccount.getAddressU160(),acct1.getAddressU160(),1200L);
            tx = ontSdk.nativevm().ong().makeTransfer(new State[]{st}, FEE_PROVIDER, 30000, 0);
            ontSdk.addSign(tx,initAccount);
            ontSdk.addSign(tx, feeAct);
            ontSdk.getConnect().sendRawTransaction(tx.toHexString());

            Thread.sleep(15000);

            //simulate a withdraw
            //todo must add check the user balance of database
            printlog("++++ withdraw 500 onts to " + usr.withdrawAddr );
            //reduce the withdraw amount first
            BigInteger wdAmount = new BigInteger("500");
            if(usr.ontBalance.compareTo(wdAmount) > 0) {
                database.get(usr.address).ontBalance = database.get(usr.address).ontBalance.subtract(wdAmount);
                printlog("++++  " + usr.address + " ont balance : " + database.get(usr.address).ontBalance);
                State wdSt = new State(mainAccountAddr, Address.decodeBase58(usr.withdrawAddr), 500);
                Transaction wdTx = ontSdk.nativevm().ont().makeTransfer(new State[]{wdSt}, FEE_PROVIDER, 30000, 0);
                ontSdk.addMultiSign(wdTx, 3, new Account[]{mutiSeedAct1, mutiSeedAct2, mutiSeedAct3});
                ontSdk.addSign(wdTx, feeAct);
                ontSdk.getConnect().sendRawTransaction(wdTx.toHexString());

            }


            //simulate a withdraw
            printlog("++++ withdraw 500 ongs to " + usr.withdrawAddr );
            wdAmount = new BigInteger("500");
            //reduce the withdraw amount first
            if(usr.ongBalance.compareTo(wdAmount) > 0) {
                database.get(usr.address).ongBalance = database.get(usr.address).ongBalance.subtract(wdAmount);
                printlog("++++  " + usr.address + " ong balance : " + database.get(usr.address).ongBalance);

                State wdSt = new State(mainAccountAddr, Address.decodeBase58(usr.withdrawAddr), 500);
                Transaction wdTx = ontSdk.nativevm().ong().makeTransfer(new State[]{wdSt}, FEE_PROVIDER, 30000, 0);
                ontSdk.addMultiSign(wdTx, 3, new Account[]{mutiSeedAct1, mutiSeedAct2, mutiSeedAct3});
                ontSdk.addSign(wdTx, feeAct);
                ontSdk.getConnect().sendRawTransaction(wdTx.toHexString());
            }



            //claim ong
            Object balance = ontSdk.getConnect().getBalance(mainAccountAddr.toBase58());
            printlog("++++ before claime ong ,balance of "+ mainAccountAddr.toBase58() +" is " + balance);
            String uOngAmt = ontSdk.nativevm().ong().unclaimOng(mainAccountAddr.toBase58());
            printlog("++++ unclaimed ong is " + uOngAmt);
            if(new BigInteger(uOngAmt).compareTo(new BigInteger("0")) > 0) {
                tx = ontSdk.nativevm().ong().makeClaimOng(mainAccountAddr.toBase58(), mainAccountAddr.toBase58(), new BigInteger(uOngAmt).longValue(), FEE_PROVIDER, 30000, 0);
                ontSdk.addMultiSign(tx, 3, new Account[]{mutiSeedAct1, mutiSeedAct2, mutiSeedAct3});
                ontSdk.addSign(tx, feeAct);
                ontSdk.getConnect().sendRawTransaction(tx.toHexString());
                balance = ontSdk.getConnect().getBalance(mainAccountAddr.toBase58());

                Thread.sleep(10000);
                printlog("++++ after claime ong ,balance of " + mainAccountAddr.toBase58() + " is " + balance);
                //distribute ong to users in database
            }

            t.join();
            t2.join();
        }catch (Exception e){
            e.printStackTrace();
            printlog("exception 3:" + e.getMessage());
        }


    }




    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        String walletfile = "wallet.dat";
        wm.openWalletFile(walletfile);

        return wm;
    }

    public static void printlog(String msg){
        System.out.println(msg);
    }

    public  static UserAcct getNewUserAcct(String id ,String pubkey,byte[] privkey,BigInteger ont,BigInteger ong){
        UserAcct acct = new UserAcct();
        acct.id = id;
        acct.privkey = privkey;
        acct.address = pubkey;
        acct.ontBalance = ont;
        acct.ongBalance = ong;

        return acct;
    }
}
