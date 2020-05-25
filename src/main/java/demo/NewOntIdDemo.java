package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
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
            String txhash = ontSdk.nativevm().ontId().sendRegister(identity.ontid, acc, payer, 20000, 0);
            txhash = ontSdk.nativevm().ontId().sendRegister(identity2.ontid, acc2, payer, 20000, 0);
            txhash = ontSdk.nativevm().ontId().sendRegister(identity3.ontid, acc3, payer, 20000, 0);
            Thread.sleep(6000);
        }
        if (false) {
            System.out.println("ontid:" + identity.ontid);
            String res = ontSdk.nativevm().ontId().sendVerifySignature(identity.ontid, 1, acc);
            System.out.println(res);

            System.out.println(Helper.toHexString(acc.serializePrivateKey()));
            System.out.println("doc:" + ontSdk.nativevm().ontId().sendGetDocument(identity.ontid));
            return;
        }
        if (false) {
            byte[][] contexts = new byte[][]{new byte[]{1, 2}};
            String txhash = ontSdk.nativevm().ontId().sendAddContext(identity.ontid, contexts, 1, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddContext", txhash);
            System.out.println("context: " + ontSdk.nativevm().ontId().sendGetDocument(identity.ontid));
            txhash = ontSdk.nativevm().ontId().sendRemoveContext(identity.ontid, contexts, 1, acc, payer, 200000, 0);
            showEvent(ontSdk, "sendRemoveContext", txhash);
            System.out.println("context: " + ontSdk.nativevm().ontId().sendGetDocument(identity.ontid));
            return;
        }
        if (false) {
            System.out.println("before sendRevokeId doc: " + ontSdk.nativevm().ontId().sendGetDocument(identity.ontid));
            String txhash = ontSdk.nativevm().ontId().sendRevokeId(identity.ontid, 1, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendRevokeId", txhash);
            System.out.println("after sendRevokeId doc: " + ontSdk.nativevm().ontId().sendGetDocument(identity.ontid));
            return;
        }
        if (false) {
            System.out.println("before sendAddKeyByIndex, pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid));
            String txhash = ontSdk.nativevm().ontId().sendAddKeyByIndex(identity.ontid, acc, acc2.serializePublicKey(), 1, identity2.ontid, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddKeyByIndex", txhash);
            System.out.println("after sendAddKeyByIndex, pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid));

            ontSdk.nativevm().ontId().sendRemoveKeyByIndex(identity.ontid, acc, acc2.serializePublicKey(), 1, payer, 2000000, 0);
            showEvent(ontSdk, "sendRemoveKeyByIndex", txhash);
            System.out.println("after sendRemoveKeyByIndex, pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid));
            return;
        }
        if (false) {
            Identity identityTemp = ontSdk.getWalletMgr().createIdentity(pwd);
            System.out.println("identity.ontid:" + Helper.toHexString(identity.ontid.getBytes()));


            System.out.println(identityTemp.ontid);
            System.out.println(Helper.toHexString(acc.serializePrivateKey()));

            System.out.println("before add pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identityTemp.ontid));
            System.out.println("controller: " + ontSdk.nativevm().ontId().sendGetController(identityTemp.ontid));
            return;
        }

        if (false) {
            Identity identityTemp = ontSdk.getWalletMgr().createIdentity(pwd);
            System.out.println("single controller: " + ontSdk.nativevm().ontId().sendGetController(identityTemp.ontid));

            Attribute[] attributes = new Attribute[]{
                    new Attribute("key".getBytes(), "valueType".getBytes(), "value".getBytes()),
            };
            System.out.println("pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identityTemp.ontid));
            System.out.println("after sendRevokeIdBySingleController single controller: " + ontSdk.nativevm().ontId().sendGetController(identityTemp.ontid));
            return;
        }

        if (false) {
            Identity identityTemp = ontSdk.getWalletMgr().createIdentity(pwd);
            Group group = new Group(new Object[]{identity.ontid.getBytes()}, 1);
            Signer signer = new Signer(identity.ontid.getBytes(), 1);
            Signer[] signers = new Signer[]{signer};

            String txhash = ontSdk.nativevm().ontId().sendRegisterIdWithController(identityTemp.ontid, new Account[]{acc}, group, signers, payer, 200000, 0);
            showEvent(ontSdk, "sendRegisterIdWithController", txhash);
            System.out.println("multi controller:" + ontSdk.nativevm().ontId().sendGetController(identityTemp.ontid));

            txhash = ontSdk.nativevm().ontId().sendAddNewAuthKeyByController(identityTemp.ontid, acc3.serializePublicKey(), identity3.ontid, signers, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddNewAuthKeyByController", txhash);

            txhash = ontSdk.nativevm().ontId().sendSetAuthKeyByController(identityTemp.ontid, 1, signers, acc, payer, 200000, 0);
            showEvent(ontSdk, "sendSetAuthKeyByController", txhash);

            txhash = ontSdk.nativevm().ontId().sendRemoveAuthKeyByController(identityTemp.ontid, 1, signers, acc, payer, 20000000, 0);
            showEvent(ontSdk, "sendRemoveAuthKeyByController", txhash);

            String tmp = ontSdk.nativevm().ontId().sendVerifyController(identityTemp.ontid, signers, new Account[]{acc});
            System.out.println("tmp:" + tmp);

            txhash = ontSdk.nativevm().ontId().sendAddKeyByController(identityTemp.ontid, acc2.serializePublicKey(), signers, identity.ontid, new Account[]{acc}, payer, 200000, 0);
            showEvent(ontSdk, "sendAddKeyByController", txhash);
            System.out.println("after addKey pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identityTemp.ontid));

            txhash = ontSdk.nativevm().ontId().sendRemoveKeyByController(identityTemp.ontid, 1, signers, new Account[]{acc}, payer, 200000, 0);
            showEvent(ontSdk, "sendRemoveKeyByController", txhash);
            System.out.println("after removeKey pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identityTemp.ontid));

            Attribute[] attributes = new Attribute[]{
                    new Attribute("key".getBytes(), "valueType".getBytes(), "value".getBytes()),
            };
            txhash = ontSdk.nativevm().ontId().sendAddAttributesByController(identityTemp.ontid, attributes, signers, new Account[]{acc}, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddAttributesBySingleController", txhash);

            System.out.println("after add attributes: " + ontSdk.nativevm().ontId().sendGetAttributes(identityTemp.ontid));

            txhash = ontSdk.nativevm().ontId().sendRemoveAttributesByController(identityTemp.ontid, "key".getBytes(), signers, new Account[]{acc}, payer, 2000000, 0);
            showEvent(ontSdk, "sendRemoveAttributesByController", txhash);
            System.out.println("after remove attributes: " + ontSdk.nativevm().ontId().sendGetAttributes(identityTemp.ontid));

            txhash = ontSdk.nativevm().ontId().sendRevokeIdByController(identityTemp.ontid, signers, new Account[]{acc}, payer, 200000, 0);
            showEvent(ontSdk, "sendRevokeIdByController", txhash);
            System.out.println("pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identityTemp.ontid));
            return;
        }

        if (false) {
            byte[] serviceId = "serviceId".getBytes();
            byte[] type = "type".getBytes();
            String txhash = ontSdk.nativevm().ontId().sendAddService(identity.ontid, serviceId, type, "serviceEndpoint".getBytes(), 1, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddService", txhash);

            System.out.println("serviceId: " + ontSdk.nativevm().ontId().sendGetService(identity.ontid, serviceId));

            txhash = ontSdk.nativevm().ontId().sendUpdateService(identity.ontid, serviceId, type, "serviceEndpoint".getBytes(), 1, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendUpdateService", txhash);

            System.out.println("serviceId: " + ontSdk.nativevm().ontId().sendGetService(identity.ontid, serviceId));

            txhash = ontSdk.nativevm().ontId().sendRemoveService(identity.ontid, serviceId, 1, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendUpdateService", txhash);

            System.out.println("serviceId: " + ontSdk.nativevm().ontId().sendGetService(identity.ontid, serviceId));
            return;
        }

        if (false) {
            System.out.println("before add attributes: " + ontSdk.nativevm().ontId().sendGetAttributes(identity.ontid));
            Attribute[] attributes = new Attribute[]{
                    new Attribute("key".getBytes(), "valueType".getBytes(), "value".getBytes()),
            };
            String txhash = ontSdk.nativevm().ontId().sendAddAttributes(identity.ontid, attributes, acc, payer, 20000, 0);
            showEvent(ontSdk, "sendAddAttributes", txhash);
            System.out.println("after add attributes: " + ontSdk.nativevm().ontId().sendGetAttributes(identity.ontid));

            txhash = ontSdk.nativevm().ontId().sendRemoveAttribute(identity.ontid, "key".getBytes(), acc, payer, 20000000, 0);
            showEvent(ontSdk, "sendRemoveAttribute", txhash);
            System.out.println("after remove attributes: " + ontSdk.nativevm().ontId().sendGetAttributes(identity.ontid));

            txhash = ontSdk.nativevm().ontId().sendAddAttributesByIndex(identity2.ontid, attributes, 1, acc2, payer, 200000, 0);
            showEvent(ontSdk, "sendAddAttributesByIndex", txhash);
            System.out.println("after sendAddAttributesByIndex attributes: " + ontSdk.nativevm().ontId().sendGetAttributes(identity.ontid));

            txhash = ontSdk.nativevm().ontId().sendRemoveAttributeByIndex(identity2.ontid, "key".getBytes(), 1, acc2, payer, 2000000, 0);
            showEvent(ontSdk, "sendRemoveAttributeByIndex", txhash);
            System.out.println("after sendRemoveAttributeByIndex attributes: " + ontSdk.nativevm().ontId().sendGetAttributes(identity.ontid));
            return;
        }

        if (false) {
            System.out.println("before sendAddPubKey, pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid));
            String txhash = ontSdk.nativevm().ontId().sendAddPubKey(identity.ontid, acc, acc2.serializePublicKey(), identity2.ontid, payer, 20000, 0);
            showEvent(ontSdk, "sendAddPubKey", txhash);
            System.out.println("after sendAddPubKey, pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid));

            txhash = ontSdk.nativevm().ontId().sendRemovePubKey(identity.ontid, acc2.serializePublicKey(), acc2, payer, 20000000, 0);
            showEvent(ontSdk, "sendRemovePubKey", txhash);
            System.out.println("after sendRemovePubKey, pubkeys: " + ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid));
            return;
        }
        if (false) {
            String txhash = ontSdk.nativevm().ontId().sendAddNewAuthKey(identity.ontid, acc2.serializePublicKey(), identity2.ontid, 1, acc, payer, 20000, 0);
            showEvent(ontSdk, "sendAddAuthKey", txhash);
            showDoc(ontSdk, identity.ontid);

            txhash = ontSdk.nativevm().ontId().sendRemoveAuthKey(identity.ontid, 2, 1, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendRemoveAuthKey", txhash);
            showDoc(ontSdk, identity.ontid);
            return;
        }
        //recovery test
        if (true) {
            System.out.println("********recovery test**************");
            Group group = new Group(new Object[]{identity2.ontid.getBytes()}, 1);
            String txhash = ontSdk.nativevm().ontId().sendSetRecovery(identity.ontid, group, 1, acc, payer, 20000, 0);
            showEvent(ontSdk, "sendSetRecovery", txhash);

            Signer signer = new Signer(identity2.ontid.getBytes(), 1);
            Signer[] signers = new Signer[]{signer};

            System.out.println("ontid:" + identity.ontid);
            System.out.println("pubkey:" + Helper.toHexString(acc3.serializePublicKey()));
            System.out.println("controller:" + Helper.toHexString(identity3.ontid.getBytes()));
            txhash = ontSdk.nativevm().ontId().sendAddNewAuthKeyByRecovery(identity.ontid, acc3.serializePublicKey(), identity3.ontid, signers, acc2, payer, 2000000, 0);
            showEvent(ontSdk, "sendAddNewAuthKeyByRecovery", txhash);
            System.out.println("after add authkey: " + ontSdk.nativevm().ontId().sendGetDocument(identity.ontid));

            txhash = ontSdk.nativevm().ontId().sendRemoveAuthKeyByRecovery(identity.ontid, 2, signers, acc2, payer, 20000000, 0);
            showEvent(ontSdk, "sendRemoveAuthKeyByRecovery", txhash);
            System.out.println("after remove authkey: " + ontSdk.nativevm().ontId().sendGetDocument(identity.ontid));


            txhash = ontSdk.nativevm().ontId().sendSetAuthKeyByRecovery(identity.ontid, 1,signers,acc2,payer,20000000,0);
            showEvent(ontSdk, "sendSetAuthKeyByRecovery", txhash);
            System.out.println("after set authkey: " + ontSdk.nativevm().ontId().sendGetDocument(identity.ontid));

            txhash = ontSdk.nativevm().ontId().sendAddKeyByRecovery(identity.ontid, acc2.serializePublicKey(), signers, identity2.ontid, new Account[]{acc2}, payer, 20000, 0);
            showEvent(ontSdk, "sendAddKeyByRecovery", txhash);

            txhash = ontSdk.nativevm().ontId().sendRemoveKeyByRecovery(identity.ontid, 2, signers, new Account[]{acc2}, payer, 20000, 0);
            showEvent(ontSdk, "sendRemoveKeyByRecovery", txhash);

            Group newGroup = new Group(new Object[]{identity3.ontid.getBytes()}, 1);
            txhash = ontSdk.nativevm().ontId().sendUpdateRecovery(identity.ontid, newGroup, signers, new Account[]{acc2}, payer, 200000000, 0);
            showEvent(ontSdk, "sendUpdateRecovery", txhash);

            System.out.println("pubkey:" + Helper.toHexString(acc.serializePublicKey()));
            txhash = ontSdk.nativevm().ontId().sendRemoveRecovery(identity.ontid, 1, acc, payer, 2000000, 0);
            showEvent(ontSdk, "sendRemoveRecovery", txhash);
            return;
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
