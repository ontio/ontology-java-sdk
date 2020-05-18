package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.ontid.Group;
import com.github.ontio.core.ontid.Signer;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Identity;

import java.io.IOException;

public class NewOntIdDemo {

    public static void main(String[] args) throws Exception {
        OntSdk ontSdk = getOntSdk();

        String pwd = "111111";

        Account acc = new Account(SignatureScheme.SHA256WITHECDSA);
        Account acc2 = new Account(SignatureScheme.SHA256WITHECDSA);
        Account acc3 = new Account(SignatureScheme.SHA256WITHECDSA);
        Account acc4 = new Account(SignatureScheme.SHA256WITHECDSA);
        Account payer = new Account(SignatureScheme.SHA256WITHECDSA);

        Identity identity = ontSdk.getWalletMgr().createIdentity(pwd);
        Identity identity2 = ontSdk.getWalletMgr().createIdentity(pwd);
        Identity identity3 = ontSdk.getWalletMgr().createIdentity(pwd);
        if (true) {
            String txhash = ontSdk.nativevm().ontId().sendRegister(identity.ontid, "", acc, payer, 20000, 0);
            txhash = ontSdk.nativevm().ontId().sendRegister(identity2.ontid, "", acc2, payer, 20000, 0);
            txhash = ontSdk.nativevm().ontId().sendRegister(identity3.ontid, "", acc3, payer, 20000, 0);
            Thread.sleep(6000);
        }
        if (false) {
            Identity identityTemp = ontSdk.getWalletMgr().createIdentity(pwd);
            String txhash = ontSdk.nativevm().ontId().sendRegisterIdWithSingleController(identityTemp.ontid, identity.ontid, 1, acc, payer, 200000, 0);
            showEvent(ontSdk, "sendRegisterIdWithSingleController", txhash);

            System.out.println("single controller: " + ontSdk.nativevm().ontId().sendGetController(identityTemp.ontid));

            Attribute[] attributes = new Attribute[]{
                    new Attribute("key".getBytes(), "valueType".getBytes(), "value".getBytes()),
            };

            txhash = ontSdk.nativevm().ontId().sendAddAttributesBySingleController(identityTemp.ontid, attributes, 1, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddAttributesBySingleController", txhash);

            System.out.println("single attributes: " + ontSdk.nativevm().ontId().sendGetAttributes(identityTemp.ontid));
            return;
        }

        if (false) {
            Identity identityTemp = ontSdk.getWalletMgr().createIdentity(pwd);
            Group group = new Group(new Object[]{identity.ontid.getBytes()}, 1);
            Signer signer = new Signer(identity.ontid.getBytes(), 1);
            Signer[] signers = new Signer[]{signer};

            String txhash = ontSdk.nativevm().ontId().sendRegisterIdWithMultiController(identityTemp.ontid, new Account[]{acc}, group, signers, payer, 200000, 0);
            showEvent(ontSdk, "sendRegisterIdWithMultiController", txhash);

            System.out.println("multi controller:" + ontSdk.nativevm().ontId().sendGetController(identityTemp.ontid));

            Attribute[] attributes = new Attribute[]{
                    new Attribute("key".getBytes(), "valueType".getBytes(), "value".getBytes()),
            };
            txhash = ontSdk.nativevm().ontId().sendAddAttributesByMultiController(identityTemp.ontid, attributes, signers, new Account[]{acc}, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddAttributesBySingleController", txhash);

            System.out.println("single attributes: " + ontSdk.nativevm().ontId().sendGetAttributes(identityTemp.ontid));
            return;
        }
        if (true) {
            byte[] serviceId = "serviceId".getBytes();
            String txhash = ontSdk.nativevm().ontId().sendAddService(identity.ontid,serviceId,"type".getBytes(),"serviceEndpoint".getBytes(),1,acc,payer,2000000,0);
            showEvent(ontSdk, "sendAddService", txhash);

            System.out.println("serviceId: " + ontSdk.nativevm().ontId().sendGetService(identity.ontid,serviceId));;
            return;
        }
        if (false) {
            Attribute[] attributes = new Attribute[]{
                    new Attribute("key".getBytes(), "valueType".getBytes(), "value".getBytes()),
            };
            String txhash = ontSdk.nativevm().ontId().sendAddAttributes(identity.ontid, attributes, acc, payer, 20000, 0);
            showEvent(ontSdk, "sendAddAttributes", txhash);


            txhash = ontSdk.nativevm().ontId().sendAddAttributesBySingleController(identity2.ontid, attributes, 1, acc2, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddAttributesBySingleController", txhash);

            Group group = new Group(new Object[]{identity.ontid.getBytes()}, 1);
            Signer signer = new Signer(identity.ontid.getBytes(), 1);
            Signer[] signers = new Signer[]{signer};
            txhash = ontSdk.nativevm().ontId().sendAddAttributesByMultiController(identity2.ontid, attributes, signers, new Account[]{acc}, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddAttributesBySingleController", txhash);
            return;
        }
        if (false) {
            System.out.println("before sendAddPubKey, pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid));
            String txhash = ontSdk.nativevm().ontId().sendAddPubKey(identity.ontid, acc, acc2.serializePublicKey(), identity2.ontid, "", payer, 20000, 0);
            showEvent(ontSdk, "sendAddPubKey", txhash);
            System.out.println("after sendAddPubKey, pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid));

            txhash = ontSdk.nativevm().ontId().sendRemovePubKey(identity.ontid, acc2.serializePublicKey(), acc2, payer, 20000000, 0);
            showEvent(ontSdk, "sendRemovePubKey", txhash);
            System.out.println("after sendRemovePubKey, pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid));


            txhash = ontSdk.nativevm().ontId().sendAddKeyBySingleController(identity.ontid, acc2.serializePublicKey(), 1, "", "", null, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddKeyBySingleController", txhash);
            return;
        }
        if (false) {
            String txhash = ontSdk.nativevm().ontId().sendAddNewAuthKey(identity.ontid, acc2.serializePublicKey(), "", 1, null, acc, payer, 20000, 0);
            showEvent(ontSdk, "sendAddAuthKey", txhash);
            showDoc(ontSdk, identity.ontid);

            txhash = ontSdk.nativevm().ontId().sendRemoveAuthKey(identity.ontid, 2, 1, null, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendRemoveAuthKey", txhash);
            showDoc(ontSdk, identity.ontid);
            return;
        }
        //recovery test
        if (false) {
            Group group = new Group(new Object[]{identity2.ontid.getBytes()}, 1);
            String txhash = ontSdk.nativevm().ontId().sendSetRecovery(identity.ontid, group, 1, new byte[]{}, acc, acc, 20000, 0);
            showEvent(ontSdk, "sendSetRecovery", txhash);

            Signer signer = new Signer(identity2.ontid.getBytes(), 1);
            Signer[] signers = new Signer[]{signer};
            txhash = ontSdk.nativevm().ontId().sendAddKeyByRecovery(identity.ontid, acc2.serializePublicKey(), signers, null, new Account[]{acc}, acc, 20000, 0);
            showEvent(ontSdk, "sendAddKeyByRecovery", txhash);

            txhash = ontSdk.nativevm().ontId().sendRemoveKeyByRecovery(identity.ontid, 1, signers, null, new Account[]{acc}, acc, 20000, 0);
            showEvent(ontSdk, "sendRemoveKeyByRecovery", txhash);

            Group newGroup = new Group(new Object[]{identity3.ontid.getBytes()}, 1);
            txhash = ontSdk.nativevm().ontId().sendUpdateRecovery(identity.ontid, newGroup, signers, null, new Account[]{acc}, acc, 200000000, 0);
            showEvent(ontSdk, "sendUpdateRecovery", txhash);
        }

        System.out.println("identity.ontid:" + identity.ontid);
        String doc = ontSdk.nativevm().ontId().sendGetDocument(identity.ontid);
//        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        System.out.println(doc);
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

    public static void showDoc(OntSdk ontSdk, String ontid) throws Exception {
        String doc = ontSdk.nativevm().ontId().sendGetDocument(ontid);
        System.out.println("doc: " + doc);
        System.out.println("");
    }

    public static void showEvent(OntSdk ontSdk, String methodName, String txHash) throws InterruptedException, SDKException, ConnectorException, IOException {
        System.out.println("methodName: " + methodName + " txHash: " + txHash);
        Thread.sleep(6000);
        System.out.println("event: " + ontSdk.getRestful().getSmartCodeEvent(txHash));
        System.out.println("");
    }
}
