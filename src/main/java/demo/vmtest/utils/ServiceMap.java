package demo.vmtest.utils;

import demo.vmtest.vm.ExecutionEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @date 2018/6/30
 */
public class ServiceMap {
    static Map<String, Service> map = new HashMap<>();
    static final String ATTRIBUTE_GETUSAGE_NAME = "Ontology.Attribute.GetUsage";
    static final String ATTRIBUTE_GETDATA_NAME = "Ontology.Attribute.GetData";

    static final String BLOCK_GETTRANSACTIONCOUNT_NAME = "System.Block.GetTransactionCount";
    static final String BLOCK_GETTRANSACTIONS_NAME = "System.Block.GetTransactions";
    static final String BLOCK_GETTRANSACTION_NAME = "System.Block.GetTransaction";
    static final String BLOCKCHAIN_GETHEIGHT_NAME = "System.Blockchain.GetHeight";
    static final String BLOCKCHAIN_GETHEADER_NAME = "System.Blockchain.GetHeader";
    static final String BLOCKCHAIN_GETBLOCK_NAME = "System.Blockchain.GetBlock";
    static final String BLOCKCHAIN_GETTRANSACTION_NAME = "System.Blockchain.GetTransaction";
    static final String BLOCKCHAIN_GETCONTRACT_NAME = "System.Blockchain.GetContract";
    static final String BLOCKCHAIN_GETTRANSACTIONHEIGHT_NAME = "System.Blockchain.GetTransactionHeight";

    static final String HEADER_GETINDEX_NAME = "System.Header.GetIndex";
    static final String HEADER_GETHASH_NAME = "System.Header.GetHash";
    static final String HEADER_GETVERSION_NAME = "Ontology.Header.GetVersion";
    static final String HEADER_GETPREVHASH_NAME = "System.Header.GetPrevHash";
    static final String HEADER_GETTIMESTAMP_NAME = "System.Header.GetTimestamp";
    static final String HEADER_GETCONSENSUSDATA_NAME = "Ontology.Header.GetConsensusData";
    static final String HEADER_GETNEXTCONSENSUS_NAME = "Ontology.Header.GetNextConsensus";
    static final String HEADER_GETMERKLEROOT_NAME = "Ontology.Header.GetMerkleRoot";

    static final String TRANSACTION_GETHASH_NAME = "System.Transaction.GetHash";
    static final String TRANSACTION_GETTYPE_NAME = "Ontology.Transaction.GetType";
    static final String TRANSACTION_GETATTRIBUTES_NAME = "Ontology.Transaction.GetAttributes";

    static final String CONTRACT_CREATE_NAME = "Ontology.Contract.Create";
    static final String CONTRACT_MIGRATE_NAME = "Ontology.Contract.Migrate";
    static final String CONTRACT_GETSTORAGECONTEXT_NAME = "System.Contract.GetStorageContext";
    static final String CONTRACT_DESTROY_NAME = "System.Contract.Destroy";
    static final String CONTRACT_GETSCRIPT_NAME = "Ontology.Contract.GetScript";

    static final String STORAGE_GET_NAME = "System.Storage.Get";
    static final String STORAGE_PUT_NAME = "System.Storage.Put";
    static final String STORAGE_DELETE_NAME = "System.Storage.Delete";
    static final String STORAGE_GETCONTEXT_NAME = "System.Storage.GetContext";
    static final String STORAGE_GETREADONLYCONTEXT_NAME = "System.Storage.GetReadOnlyContext";

    static final String STORAGECONTEXT_ASREADONLY_NAME = "System.StorageContext.AsReadOnly";

    static final String RUNTIME_GETTIME_NAME = "System.Runtime.GetTime";
    static final String RUNTIME_CHECKWITNESS_NAME = "System.Runtime.CheckWitness";
    static final String RUNTIME_NOTIFY_NAME = "System.Runtime.Notify";
    static final String RUNTIME_LOG_NAME = "System.Runtime.Log";
    static final String RUNTIME_GETTRIGGER_NAME = "System.Runtime.GetTrigger";
    static final String RUNTIME_SERIALIZE_NAME = "System.Runtime.Serialize";
    static final String RUNTIME_DESERIALIZE_NAME = "System.Runtime.Deserialize";

    static final String NATIVE_INVOKE_NAME = "Ontology.Native.Invoke";

    static final String GETSCRIPTCONTAINER_NAME = "System.ExecutionEngine.GetScriptContainer";
    static final String GETEXECUTINGSCRIPTHASH_NAME = "System.ExecutionEngine.GetExecutingScriptHash";
    static final String GETCALLINGSCRIPTHASH_NAME = "System.ExecutionEngine.GetCallingScriptHash";
    static final String GETENTRYSCRIPTHASH_NAME = "System.ExecutionEngine.GetEntryScriptHash";

    static final String APPCALL_NAME = "APPCALL";
    static final String TAILCALL_NAME = "TAILCALL";
    static final String SHA1_NAME = "SHA1";
    static final String SHA256_NAME = "SHA256";
    static final String HASH160_NAME = "HASH160";
    static final String HASH256_NAME = "HASH256";
    static final String UINT_DEPLOY_CODE_LEN_NAME = "Deploy.Code.Gas";
    static final String UINT_INVOKE_CODE_LEN_NAME = "Invoke.Code.Gas";

    public static Service getService(String key) {
        if (map.size() == 0) {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map.get(key);
    }

    public static void init() throws Exception {
        map.put(STORAGE_PUT_NAME, new Service(Service.class.getMethod("storagePut", Config.class, ExecutionEngine.class), null));
        map.put(STORAGE_GETCONTEXT_NAME, new Service(Service.class.getMethod("storageGetContext", Config.class, ExecutionEngine.class), null));
        map.put(STORAGE_GET_NAME, new Service(Service.class.getMethod("storageGet", Config.class, ExecutionEngine.class), null));
        map.put(RUNTIME_LOG_NAME, new Service(Service.class.getMethod("runtimeLog", Config.class, ExecutionEngine.class), null));
        map.put(RUNTIME_LOG_NAME, new Service(Service.class.getMethod("runtimeLog", Config.class, ExecutionEngine.class), null));
        map.put(RUNTIME_NOTIFY_NAME, new Service(Service.class.getMethod("runtimeNotify", Config.class, ExecutionEngine.class), null));
        map.put(RUNTIME_CHECKWITNESS_NAME, new Service(Service.class.getMethod("runtimeCheckWitness", Config.class, ExecutionEngine.class), null));
        map.put(RUNTIME_DESERIALIZE_NAME, new Service(Service.class.getMethod("runtimeDeserialize", Config.class, ExecutionEngine.class), null));
        map.put(RUNTIME_SERIALIZE_NAME, new Service(Service.class.getMethod("runtimeSerialize", Config.class, ExecutionEngine.class), null));

    }

}
