package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.core.payload.InvokeCode;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;

import java.util.*;

//
//
public class MakeTxWithJsonDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            String str = "{\"action\":\"invoke\",\"params\":{\"login\":true,\"url\":\"http://127.0.0.1:80/rawtransaction/txhash\",\"message\":\"will pay 1 ONT in this transaction\",\"invokeConfig\":{\"contractHash\":\"16edbe366d1337eb510c2ff61099424c94aeef02\",\"functions\":[{\"operation\":\"method name\",\"args\":[{\"name\":\"arg0-list\",\"value\":[true,100,\"Long:100000000000\",\"ByteArray:aabb\",\"String:hello\",[true,100],{\"key\":6}]},{\"name\":\"arg1-map\",\"value\":{\"key\":\"String:hello\",\"key1\":\"ByteArray:aabb\",\"key2\":\"Long:100000000000\",\"key3\":true,\"key4\":100,\"key5\":[100],\"key6\":{\"key\":6}}},{\"name\":\"arg2-str\",\"value\":\"String:test\"}]}],\"payer\":\"AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\",\"gasLimit\":20000,\"gasPrice\":500,\"signature\":{\"m\":1,\"signers\":[\"AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\"]}}}}";
            System.out.println(str);
            Transaction tx = ontSdk.makeTransactionByJson(str);
            //System.out.println(tx.json());

            List paramList = new ArrayList<>();
            paramList.add("method name");

            List args2 = new ArrayList();
            Map map = new HashMap<>();
            Map map2 = new HashMap<>();
            map.put("key1",Helper.hexToBytes("aabb"));
            map.put("key2",100000000000L);
            map.put("key3",true);
            map.put("key4",100);
            map.put("key","hello");
            List list0 = new ArrayList();
            list0.add(100);
            map.put("key5",list0);
            map2.put("key",6);
            map.put("key6",map2);
            List list = new ArrayList();
            List list2 = new ArrayList();
            list.add(true);
            list.add(100);
            list.add(100000000000L);
            list.add(Helper.hexToBytes("aabb"));
            list.add("hello");
            list2.add(true);
            list2.add(100);
            list.add(list2);
            list.add(map2);
            args2.add(list);
            args2.add(map);
            args2.add("test");

            paramList.add(args2);
            System.out.println("########make by self##############");
            System.out.println(paramList);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("wallet2.dat");

        return wm;
    }
}
