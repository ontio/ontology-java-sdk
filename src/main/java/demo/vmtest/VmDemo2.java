package demo.vmtest;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import demo.vmtest.types.BoolItem;
import demo.vmtest.types.ByteArrayItem;
import demo.vmtest.types.IntegerItem;
import demo.vmtest.types.StackItems;
import demo.vmtest.utils.Service;
import demo.vmtest.utils.ServiceMap;
import demo.vmtest.utils.Config;
import demo.vmtest.vm.ExecutionContext;
import demo.vmtest.vm.ExecutionEngine;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class VmDemo2 {
    public static String privatekey1 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey2 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String nep5abi = "{\"hash\":\"0x5bb169f915c916a5e30a3c13a5e0cd228ea26826\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Name\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"Symbol\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"Decimals\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Init\",\"parameters\":[],\"returntype\":\"Boolean\"},{\"name\":\"TotalSupply\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"BalanceOf\",\"parameters\":[{\"name\":\"address\",\"type\":\"ByteArray\"}],\"returntype\":\"Integer\"}],\"events\":[{\"name\":\"transfer\",\"parameters\":[{\"name\":\"arg1\",\"type\":\"ByteArray\"},{\"name\":\"arg2\",\"type\":\"ByteArray\"},{\"name\":\"arg3\",\"type\":\"Integer\"}],\"returntype\":\"Void\"}]}";

    public static void main(String[] args) {
        try {
//            System.out.println(Helper.toHexString("System.Runtime.CheckWitness".getBytes()));
//            System.exit(0);
            String codeStr = "53c56b6c766b00527ac46c766b51527ac4616c766b51c300c36c766b51c351c3617c65180075596c766b52527ac46203006c766b52c3616c756655c56b6c766b00527ac46c766b51527ac4616c766b00c361681a53797374656d2e52756e74696d652e446573657269616c697a656c766b52527ac46c766b52c3036b6579c36c766b53527ac46c766b53c36c766b54527ac46203006c766b54c3616c7566";
codeStr="57c56b6c766b00527ac46c766b51527ac4616c766b51c300c36c766b52527ac46c766b51c351c36c766b53527ac46c766b51c352c36c766b54527ac46c766b52c361681b53797374656d2e52756e74696d652e436865636b5769746e657373009c6c766b55527ac46c766b55c3643400610c3d3d3d6661696c3d3d3d3d3d61681253797374656d2e52756e74696d652e4c6f6761026e6f6c766b56527ac46222006c766b52c36c766b53c36c766b54c36152726516006c766b56527ac46203006c766b56c3616c756657c56b6c766b00527ac46c766b51527ac46c766b52527ac461536c766b53527ac461681953797374656d2e53746f726167652e476574436f6e746578746c766b54527ac46c766b54c306726573756c74062d7474657374615272681253797374656d2e53746f726167652e507574616c766b54c306726573756c74617c681253797374656d2e53746f726167652e4765746c766b55527ac40e3d3d737563636573733d3d3d3d3d61681253797374656d2e52756e74696d652e4c6f6761616c766b00c36c766b51c36c766b52c3615272087472616e7366657254c1681553797374656d2e52756e74696d652e4e6f74696679616c766b55c36c766b56527ac46203006c766b56c3616c7566";
            System.out.println(Helper.toHexString("DeserializeMap".getBytes()));
            //System.exit(0);
            com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), SignatureScheme.SHA256WITHECDSA);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), SignatureScheme.SHA256WITHECDSA);
            System.out.println(acct1.getAddressU160().toBase58());
            System.out.println(acct2.getAddressU160().toBase58());
            Address recv = acct2.getAddressU160();
            AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
            System.out.println("Entrypoint:" + abiinfo.getEntrypoint());
            System.out.println("contractAddress:"+abiinfo.getHash());
            System.out.println("Functions:" + abiinfo.getFunctions());
            AbiFunction func = abiinfo.getFunction("Transfer");
            func.name = func.name.toLowerCase();
            Map map = new HashMap<>();
            map.put("key","world");
         //   func.setParamsValue(BuildParams.getMapBytes(map), recv.toArray(), Long.valueOf(19*10000000));//
            func.setParamsValue(acct1.getAddressU160().toArray(), recv.toArray(), Long.valueOf(19*10000000));
            byte[] params = BuildParams.serializeAbiFunction(func);
            params = Helper.addBytes(params, new byte[]{0x67});
           // params = Helper.addBytes(params, Helper.hexToBytes(str));
            System.out.println("params:"+Helper.toHexString(params));

//            System.exit(0);


            byte[] code = Helper.hexToBytes(codeStr);
            ExecutionEngine engine = new ExecutionEngine();
            engine.PushContext(new ExecutionContext(engine,params));
            Config config = new Config();
            String contractAddress = Helper.toHexString(Address.AddressFromVmCode(codeStr).toArray());
            config.tx = OntSdk.getInstance().vm().makeInvokeCodeTransaction(contractAddress, null, params, null,0, 0);
            config.tx.sigs = new Sig[1];
            config.tx.sigs[0] = new Sig();
            config.tx.sigs[0].M = 1;
            config.tx.sigs[0].pubKeys = new byte[1][];
            config.tx.sigs[0].pubKeys[0] = acct1.serializePublicKey();
            config.tx.sigs[0].sigData = new byte[1][];
            config.tx.sigs[0].sigData[0] = config.tx.sign(acct1, acct1.getSignatureScheme());
            int num = 0;
            while (true) {
                if(engine.Contexts.size() == 0){
                    break;
                }
                engine.ExecuteCode();
                if(engine.OpCode == ScriptOp.OP_RET){
                    break;
                }
                if (engine.OpCode == null) {
                    //System.out.println("##OpCode null##"+engine.OpCodeValue);
                } else if (engine.OpCode.getByte() >= ScriptOp.OP_PUSHBYTES1.getByte() && engine.OpCode.getByte() <= ScriptOp.OP_PUSHBYTES75.getByte()) {
                } else if (!engine.ValidateOp()) {
                    break;
                }
                num++;
                if(engine.OpCode != null) {
                    System.out.println(num+">  "+engine.EvaluationStack.Count() + "  " + Helper.toHexString(new byte[]{engine.OpCode.getByte()}) + " " + engine.OpExec.Name + "     " + engine.EvaluationStack.info());
                }
                if(ScriptOp.OP_APPCALL == engine.OpCode){
                    ExecutionEngine engine2 = new ExecutionEngine();
                    engine2.PushContext(new ExecutionContext(engine2,code));
                    engine.EvaluationStack.CopyTo(engine2.EvaluationStack);
                    engine = engine2;
                }else if(ScriptOp.OP_SYSCALL == engine.OpCode){
                    byte[] bys = engine.Context.OpReader.readVarBytes();
                    System.out.println("####SYSCALL#####"+new String(bys));
                    Service service = ServiceMap.getService(new String(bys));
                    if(service == null){
                        System.out.println(new String(bys));
                        System.exit(0);
                    }
                    service.Exec(config,engine);

                }else {
                    engine.StepInto();
                }
            }
            System.out.println("##########end############");
            System.out.println("Stack Count:"+engine.EvaluationStack.Count());
            StackItems items = engine.EvaluationStack.Peek(0);
            if(items instanceof ByteArrayItem) {
                System.out.println("Result ByteArrayItem:" +Helper.toHexString(engine.EvaluationStack.Peek(0).GetByteArray())+"  "+ new String(engine.EvaluationStack.Peek(0).GetByteArray()));
            } else if(items instanceof IntegerItem) {
                System.out.println("Result GetBigInteger:" + engine.EvaluationStack.Peek(0).GetBigInteger().longValue());
            } else if(items instanceof BoolItem) {
                System.out.println("Result BoolItem:" + engine.EvaluationStack.Peek(0).GetBoolean());
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/*

using Neo.SmartContract.Framework.Services.Neo;
using Neo.SmartContract.Framework;
using System;
using System.ComponentModel;
using System.Numerics;
namespace Neo.SmartContract
{
    public class HelloWorld : Framework.SmartContract
    {
        [DisplayName("transfer")]
        public static event Action<byte[], byte[], BigInteger> Transferred;

        public static object Main(string operation, params object[] args)
        {
            byte[] from = (byte[])args[0];
            byte[] to = (byte[])args[1];
			BigInteger value = (BigInteger)args[2];
            if (!Runtime.CheckWitness(from )) {
                Runtime.Log("===fail=====");
               return "no";
            }
            return transfer(from,to,value);

        }
        public static object transfer(byte[] from,byte[] to,BigInteger value){
            int i = 1 +2;


            StorageContext context = Storage.CurrentContext;
            Storage.Put(context, "result", "-ttest");
            byte[] v = Storage.Get(context, "result");
            Runtime.Log("==success=====");


            Transferred(from, to, value);
            return v.AsString();
        }

    }
}




using Neo.SmartContract.Framework;
using Neo.SmartContract.Framework.Services.Neo;
using Neo.SmartContract.Framework.Services.System;
using System;
using System.ComponentModel;
using System.Numerics;
using System.Text;
using Helper = Neo.SmartContract.Framework.Helper;

namespace DID
{
    public class DID : SmartContract
    {
        public static Object Main(string operation, params object[] args)
        {
           // Runtime.Log("start Main");
           // if (operation == "DeserializeMap") {
                DeserializeMap((byte[])args[0], (byte[])args[1]);
           // }

            return 9;
        }
         public static object SerializeMap(byte[] param, byte[] param1)
         {
            Map<string, string> m = new Map<string, string>();
            m["key"] = "world";

            byte[] b = Helper.Serialize(m);
            return b;
         }

        public static object DeserializeMap(byte[] param, byte[] param1)
        {
           // Runtime.Log("start DeserializeMap");
            Map<string, string> b1 = (Map<string, string>)Helper.Deserialize(param);
            string key = b1["key"];
            return key;
        }


    }
}

 */