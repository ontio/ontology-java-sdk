package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.ontid.Group;
import com.github.ontio.core.ontid.Signer;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Identity;

public class NewOntIdDemo {

    public static void main(String[] args) throws Exception {
        OntSdk ontSdk = getOntSdk();

        String pwd = "111111";

        Account acc = new Account(SignatureScheme.SHA256WITHECDSA);
        Account acc2 = new Account(SignatureScheme.SHA256WITHECDSA);

        Identity identity = ontSdk.getWalletMgr().createIdentity(pwd);
        Identity identity2 = ontSdk.getWalletMgr().createIdentity(pwd);
        String txhash = "";
        if (false) {
            txhash = ontSdk.nativevm().ontId().sendRegister(identity.ontid,"",null, acc, acc, 20000, 0);
        }
        txhash = ontSdk.nativevm().ontId().sendRegister(identity.ontid,"access",new byte[]{1,2}, acc, acc, 20000, 0);
        txhash = ontSdk.nativevm().ontId().sendRegister(identity2.ontid,"access",new byte[]{1,2}, acc, acc, 20000, 0);
        Thread.sleep(6000);
        if (true) {
            txhash = ontSdk.nativevm().ontId().sendAddPubKey(identity.ontid,acc,acc2.serializePublicKey(),"controller","access",new byte[]{1,2}, acc, 20000, 0);
            Attribute[] attributes = new Attribute[]{
                    new Attribute("key".getBytes(), "valueType".getBytes(),"value".getBytes()),
            };
            txhash = ontSdk.nativevm().ontId().sendAddAttributes(identity.ontid,attributes,null,acc,acc,20000,0);
            txhash = ontSdk.nativevm().ontId().sendAddAuthKey(identity.ontid,false,1,acc2.serializePublicKey(),"",1,null,acc,acc,20000,0);
        }
        if (false) {
            Group group = new Group(new Object[]{identity2.ontid.getBytes()}, 1);
            txhash = ontSdk.nativevm().ontId().sendSetRecovery(identity.ontid,group,1,new byte[]{},acc,acc,20000,0);
            Thread.sleep(6000);
            Signer signer = new Signer(identity2.ontid.getBytes(), 1);
            Signer[] signers = new Signer[]{signer};
            txhash = ontSdk.nativevm().ontId().sendAddKeyByRecovery(identity.ontid,acc2.serializePublicKey(),signers,null,new Account[]{acc},acc,20000,0);
        }
        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        System.out.println(ddo);
        Object obj = ontSdk.getRestful().getSmartCodeEvent(txhash);
        System.out.println(obj);
    }

    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://139.219.108.204";
        String ip = "http://127.0.0.1";
//        ip = "http://polaris3.ont.io";
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
        wm.openWalletFile("wallet.json");
        return wm;
    }
}
