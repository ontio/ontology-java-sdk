package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.smartcontract.neovm.oep8.Oep8State;
import com.github.ontio.smartcontract.neovm.oep8.TransferFrom;

import java.util.Base64;

public class Oep8Demo {
    public static void main(String[] args) {
        try {
            OntSdk sdk = getOntSdk();
            String privateKey = Account.getGcmDecodedPrivateKey("8p2q0vLRqyfKmFHhnjUYVWOm12kPm78JWqzkTOi9rrFMBz624KjhHQJpyPmiSSOa","111111","AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe",Base64.getDecoder().decode("KbiCUr53CZUfKG1M3Gojjw=="),16384,SignatureScheme.SHA256WITHECDSA);
//            Account account = sdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p", "xinhao");
            Account account1 = sdk.getWalletMgr().getAccount("ARR5ywDEx3ybXkMGmZPFYu9hiC8J4xvNdc", "xinhao");
            Account account2 = sdk.getWalletMgr().getAccount("AacHGsQVbTtbvSWkqZfvdKePLS6K659dgp", "xinhao");
            Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
            if (false){
                System.out.println(privateKey);
                String txhash = sdk.neovm().oep8().sendInit(account1, account,200000, 500);
                System.out.println(txhash);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                return;
            }
            byte[] tokenId1 = Helper.hexToBytes("01");
            byte[] tokenId2 = Helper.hexToBytes("02");
            byte[] tokenId3 = Helper.hexToBytes("03");
            byte[] tokenId4 = Helper.hexToBytes("04");
            byte[] tokenId5 = Helper.hexToBytes("05");
            byte[] tokenId6 = Helper.hexToBytes("06");
            byte[] tokenId7 = Helper.hexToBytes("07");
            byte[] tokenId8 = Helper.hexToBytes("08");
            if (true){
                String name = sdk.neovm().oep8().queryName(tokenId1);
                System.out.println("name: " + name);
                System.out.println(sdk.neovm().oep8().queryName(tokenId2));
                System.out.println(sdk.neovm().oep8().queryName(tokenId3));
                System.out.println(sdk.neovm().oep8().queryName(tokenId4));
                System.out.println(sdk.neovm().oep8().queryName(tokenId5));
                System.out.println(sdk.neovm().oep8().queryName(tokenId6));
                System.out.println(sdk.neovm().oep8().queryName(tokenId7));
                System.out.println(sdk.neovm().oep8().queryName(tokenId8));
                return;
            }
            if(true){
                System.out.println("totalsupply: " + sdk.neovm().oep8().queryTotalSupply(tokenId1));
                System.out.println("totalsupply: " + sdk.neovm().oep8().queryTotalSupply(tokenId2));
                System.out.println("totalsupply: " + sdk.neovm().oep8().queryTotalSupply(tokenId3));
                System.out.println("totalsupply: " + sdk.neovm().oep8().queryTotalSupply(tokenId4));
                System.out.println("totalsupply: " + sdk.neovm().oep8().queryTotalSupply(tokenId5));
                System.out.println("totalsupply: " + sdk.neovm().oep8().queryTotalSupply(tokenId6));
                System.out.println("totalsupply: " + sdk.neovm().oep8().queryTotalSupply(tokenId7));
                System.out.println("totalsupply: " + sdk.neovm().oep8().queryTotalSupply(tokenId8));
                System.out.println("symbol: " + sdk.neovm().oep8().querySymbol(tokenId1));
                System.out.println("symbol: " + sdk.neovm().oep8().querySymbol(tokenId2));
                System.out.println("symbol: " + sdk.neovm().oep8().querySymbol(tokenId3));
                System.out.println("symbol: " + sdk.neovm().oep8().querySymbol(tokenId4));
                System.out.println("symbol: " + sdk.neovm().oep8().querySymbol(tokenId5));
                System.out.println("symbol: " + sdk.neovm().oep8().querySymbol(tokenId6));
                System.out.println("symbol: " + sdk.neovm().oep8().querySymbol(tokenId7));
                System.out.println("symbol: " + sdk.neovm().oep8().querySymbol(tokenId8));
                return;
            }
            if(false){
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId1));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId2));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId3));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId4));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId5));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId6));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId7));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId8));

                return;
            }
            if(false){
                System.out.println(sdk.neovm().oep8().balancesOf(account1.getAddressU160().toBase58()));
                System.out.println(sdk.neovm().oep8().totalBalanceOf(account1.getAddressU160().toBase58()));
                return;
            }
            if(false){
                System.out.println(sdk.neovm().oep8().queryTotalSupply(tokenId1));
                return;
            }
            if(false){
                System.out.println(sdk.neovm().oep8().queryTotalSupply(tokenId1));
                String txhash = sdk.neovm().oep8().mint(account1,tokenId1,1, account, 20000, 500);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                System.out.println(sdk.neovm().oep8().queryTotalSupply(tokenId1));
                return;
            }
            if(false){
                String txhash = sdk.neovm().oep8().sendCompound(account1,2, account, 71442, 500);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                System.out.println(sdk.neovm().oep8().queryTotalSupply(tokenId1));
                System.out.println(sdk.neovm().oep8().queryTotalSupply(tokenId8));
                return;
            }
            if(false){
                System.out.println(sdk.neovm().oep8().balancesOf(account.getAddressU160().toBase58()));
                System.out.println(sdk.neovm().oep8().totalBalanceOf(account.getAddressU160().toBase58()));
                return;
            }
            if (false){
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId1));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account2.getAddressU160().toBase58(), tokenId1));
                String txhash = sdk.neovm().oep8().sendTransfer(account1,account2.getAddressU160().toBase58(),tokenId1,10,account,20000,500);
                System.out.println("txhash: " + txhash);
                Thread.sleep(6000);
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId1));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account2.getAddressU160().toBase58(), tokenId1));
                return;
            }

            if (false){
                System.out.println("res: " + sdk.neovm().oep8().queryAllowance(account1.getAddressU160().toBase58(),account2.getAddressU160().toBase58(), tokenId1));
                return;
            }
            if (false){
                String res = sdk.neovm().oep8().sendApprove(account1,account2.getAddressU160().toBase58(), tokenId1, 10000, account, 20000, 500);
                System.out.println("res: " + res);
                Thread.sleep(3000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                return;
            }
            if (false){
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId1));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account2.getAddressU160().toBase58(), tokenId1));
                String res = sdk.neovm().oep8().sendTransferFrom(account2,account1.getAddressU160().toBase58(),account2.getAddressU160().toBase58(), tokenId1, 1000, account, 20000, 500);
                System.out.println("res: " + res);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId1));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account2.getAddressU160().toBase58(), tokenId1));
                return;
            }
            if (false){
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId1));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account2.getAddressU160().toBase58(), tokenId1));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account.getAddressU160().toBase58(), tokenId1));
                Account[] accounts = new Account[]{account1, account2};
                Oep8State state = new Oep8State(account1.getAddressU160().toArray(), account.getAddressU160().toArray(), tokenId1, 1);
                Oep8State state2 = new Oep8State(account2.getAddressU160().toArray(), account.getAddressU160().toArray(), tokenId1, 1);
                String res = sdk.neovm().oep8().sendTransferMulti(accounts,new Oep8State[]{state,state2},  account, 20000, 500);
                System.out.println("res: " + res);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account1.getAddressU160().toBase58(), tokenId1));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account2.getAddressU160().toBase58(), tokenId1));
                System.out.println("balance: " + sdk.neovm().oep8().queryBalanceOf(account.getAddressU160().toBase58(), tokenId1));
                return;
            }
            if (false){
                System.out.println("res: " + sdk.neovm().oep8().queryAllowance(account1.getAddressU160().toBase58(),account.getAddressU160().toBase58(), tokenId1));
                System.out.println("res: " + sdk.neovm().oep8().queryAllowance(account1.getAddressU160().toBase58(),account.getAddressU160().toBase58(), tokenId1));
                Account[] accounts = new Account[]{account1, account2};
                Oep8State state = new Oep8State(account1.getAddressU160().toArray(), account.getAddressU160().toArray(), tokenId1, 1);
                Oep8State state2 = new Oep8State(account2.getAddressU160().toArray(), account.getAddressU160().toArray(), tokenId1, 1);
                String res = sdk.neovm().oep8().sendApproveMulti(accounts,new Oep8State[]{state, state2},  account, 20000, 500);
                System.out.println("res: " + res);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                System.out.println("res: " + sdk.neovm().oep8().queryAllowance(account1.getAddressU160().toBase58(),account.getAddressU160().toBase58(), tokenId1));
                System.out.println("res: " + sdk.neovm().oep8().queryAllowance(account1.getAddressU160().toBase58(),account.getAddressU160().toBase58(), tokenId1));
                return;
            }
            if (true){
                System.out.println("res: " + sdk.neovm().oep8().queryAllowance(account1.getAddressU160().toBase58(),account.getAddressU160().toBase58(), tokenId1));
                System.out.println("res: " + sdk.neovm().oep8().queryAllowance(account1.getAddressU160().toBase58(),account.getAddressU160().toBase58(), tokenId1));
                Account[] accounts = new Account[]{account};
                TransferFrom state = new TransferFrom(account.getAddressU160().toArray(),account1.getAddressU160().toArray(), account.getAddressU160().toArray(), tokenId1, 1);
                TransferFrom state2 = new TransferFrom(account.getAddressU160().toArray(),account2.getAddressU160().toArray(), account.getAddressU160().toArray(), tokenId1, 1);
                String res = sdk.neovm().oep8().sendTransferFromMulti(accounts,new TransferFrom[]{state, state2}, account, 27740, 500);
                System.out.println("res: " + res);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                System.out.println("res: " + sdk.neovm().oep8().queryAllowance(account1.getAddressU160().toBase58(),account.getAddressU160().toBase58(), tokenId1));
                System.out.println("res: " + sdk.neovm().oep8().queryAllowance(account1.getAddressU160().toBase58(),account.getAddressU160().toBase58(), tokenId1));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://139.219.108.204";
        String ip = "http://127.0.0.1";
        ip = "http://polaris3.ont.io";
//        ip= "http://139.219.138.201";
//        String ip = "http://101.132.193.149";
//        String ip = "http://polaris1.ont.io";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.neovm().oep8().setContractAddress("24643d7411223bcd0dd4d8b358e1e563af311408");
        wm.openWalletFile("oep8.dat");
        return wm;
    }
}
