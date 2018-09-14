package demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class NeoVmDemo3 {


    public static String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String abi ="{\"hash\":\"0x638119357dd210e0d7661f779526690af5e3cf8e\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"TestMap\",\"parameters\":[],\"returntype\":\"Any\"},{\"name\":\"DeserializeMap\",\"parameters\":[{\"name\":\"param\",\"type\":\"Map\"}],\"returntype\":\"Any\"},{\"name\":\"TestStruct\",\"parameters\":[],\"returntype\":\"Any\"},{\"name\":\"DeserializeStruct\",\"parameters\":[{\"name\":\"param\",\"type\":\"Struct\"}],\"returntype\":\"Any\"}],\"events\":[]}";
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            Account acct1 = new Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);
            Account acct2 = new Account(Helper.hexToBytes(privatekey2), ontSdk.defaultSignScheme);
            Account acct = new Account(Helper.hexToBytes(privatekey0), ontSdk.defaultSignScheme);

//            Account account1 = getAccount("YfOr9im4rOciy3cV7JkVo9QCfrRT4IGLa/CZKUJfL29pM6Zi1oVEM67+8MezMIro","1","AXmQDzzvpEtPkNwBEFsREzApTTDZFW6frD","RCIo60eCJAwzkTYmIfp3GA==");
//            Account account2 = getAccount("gpgMejEHzawuXG+ghLkZ8/cQsOJcs4BsFgFjSaqE7SC8zob8hqc6cDNhJI/NBkk+","1","AY5W6p4jHeZG2jjW6nS1p4KDUhcqLkU6jz","tuLGZOimilSnypT91WrenQ==");
            Account account3 = getAccount("guffI05Eafq9F0j3/eQxHWGo1VN/xpeIkXysEPeH51C2YHYCNnCWTWAdqDB7lonl","1","ALZVrZrFqoSvqyi38n7mpPoeDp7DMtZ9b6","oZPg+5YotRWStVsRMYlhfg==");
//            Account account4 = getAccount("fAknSuXzMMC0nJ2+YuTpTLs6Hl5Dc0c2zHZBd2Q7vCuv8Wt97uYz1IU0t+AtrWts","1","AMogjmLf2QohTcGST7niV75ekZfj44SKme","0BVIiUf46rb/e5dVZIwfrg==");
//            Account account5 = getAccount("IufXVQfrL3LI7g2Q7dmmsdoF7BdoI/vHIsXAxd4qkqlkGBYj3pcWHoQgdCF+iVOv","1","AZzQTkZvjy7ih9gjvwU8KYiZZyNoy6jE9p","zUtzh0B4UW0wokzL+ILdeg==");
//            Account account6 = getAccount("PYEJ1c79aR7bxdzvBlj3lUMLp0VLKQHwSe+/OS1++1qa++gBMJJmJWJXUP5ZNhUs","1","AKEqQKmxCsjWJz8LPGryXzb6nN5fkK1WDY","uJhjsfcouCGZQUdHO2TZZQ==");
//            Account account7s = getAccount("ZG/SfHRArUkopwhQS1MW+a0fvQvyN1NnwonU0oZH8y1bGqo5T+dQz3rz1qsXqFI2","1","AQNpGWz4oHHFBejtBbakeR43DHfen7cm8L","6qiU9bgK/+1T2V8l14mszg==");

            Account account100 = new Account(Helper.hexToBytes("5864530b5589d9e25f730ff27d6e2cf99b4f70028d8aebb3b4c4050af9c08276"),SignatureScheme.SHA256WITHECDSA);

            String swapAbi = "{\"hash\":\"0x1a5c9719acc4bfc6e9ab0027c757aa0ad71a6f4d\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Swap\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"fromSymbol\",\"type\":\"String\"},{\"name\":\"toSymbol\",\"type\":\"String\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"SetTokenBase\",\"parameters\":[{\"name\":\"symbol\",\"type\":\"String\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"GetTokenBase\",\"parameters\":[{\"name\":\"symbol\",\"type\":\"String\"}],\"returntype\":\"Integer\"},{\"name\":\"SetFeeRate\",\"parameters\":[{\"name\":\"percentage\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"GetFeeRate\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"SetContractHash\",\"parameters\":[{\"name\":\"key\",\"type\":\"String\"},{\"name\":\"hash\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"GetContractHash\",\"parameters\":[{\"name\":\"key\",\"type\":\"String\"}],\"returntype\":\"ByteArray\"}],\"events\":[]}";

            //TODO 1. 部署老庙和有乐合约，
            // TODO 2. 执行合约中的deploy方法，
            // TODO 3.执行group合约中SetContractHash方法设置contracthash，
            // TODO 4. 执行group中的GetContractHash验证第3步是否正确执行，
            // TODO 5.执行group合约中的GroupTransfer方法


            boolean deployContract = false;// 部署合约
            boolean deploy = false;        //执行Token合约中的deploy方法
            boolean balanceOf = true;     //执行合约中的balanceOf方法， 查询账户余额
            boolean SetContractHash = false;//执行group合约中的SetContractHash方法
            boolean GetContractHash = false;//执行group合约中的GetContractHash方法， 验证SetContractHash是否执行成功
            boolean GroupTransfer = true;  //执行group合约中的GroupTransfer方法，
            boolean transferMulti = false;  //测试多转多

            if(false){
                System.out.println(Address.parse("d2c124dd088190f709b684e0bc676d70c41b3776").toBase58());
                System.out.println(Address.parse("a34344eb3f7ea6ee8088ad6d13f5cfacc899dd88").toBase58());
                return;
            }
            //部署老庙，有乐合约，全家合约，group合约等
            if(false){
                String code = "011cc56b6c766b00527ac46c766b51527ac4616c766b00c30453776170876c766b52527ac46c766b52c364a900616c766b51c3c0559c009c6c766b58527ac46c766b58c3640e00006c766b59527ac46208036c766b51c300c36c766b53527ac46c766b51c351c36c766b54527ac46c766b51c352c36c766b55527ac46c766b51c353c36c766b56527ac46c766b51c354c36c766b57527ac46c766b53c36c766b54c36c766b55c36c766b56c36c766b57c3615479517956727551727553795279557275527275659a026c766b59527ac46287026c766b00c30c536574546f6b656e42617365876c766b5a527ac46c766b5ac3645d00616c766b51c3c0529c009c6c766b5d527ac46c766b5dc3640e00006c766b59527ac46240026c766b51c300c36c766b5b527ac46c766b51c351c36c766b5c527ac46c766b5bc36c766b5cc3617c6519066c766b59527ac4620b026c766b00c30c476574546f6b656e42617365876c766b5e527ac46c766b5ec3644900616c766b51c3c0519c009c6c766b60527ac46c766b60c3640e00006c766b59527ac462c4016c766b51c300c36c766b5f527ac46c766b5fc361659a066c766b59527ac462a3016c766b00c30a53657446656552617465876c766b0111527ac46c766b0111c3644d00616c766b51c3c0519c009c6c766b0113527ac46c766b0113c3640e00006c766b59527ac4625a016c766b51c300c36c766b0112527ac46c766b0112c361658b066c766b59527ac46237016c766b00c30a47657446656552617465876c766b0114527ac46c766b0114c364120061616540076c766b59527ac46206016c766b00c30f536574436f6e747261637448617368876c766b0115527ac46c766b0115c3646300616c766b51c3c0529c009c6c766b0118527ac46c766b0118c3640e00006c766b59527ac462b8006c766b51c300c36c766b0116527ac46c766b51c351c36c766b0117527ac46c766b0116c36c766b0117c3617c650a076c766b59527ac4627f006c766b00c30f476574436f6e747261637448617368876c766b0119527ac46c766b0119c3644d00616c766b51c3c0519c009c6c766b011b527ac46c766b011bc3640e00006c766b59527ac46231006c766b51c300c36c766b011a527ac46c766b011ac3616589076c766b59527ac4620e00006c766b59527ac46203006c766b59c3616c75660115c56b6c766b00527ac46c766b51527ac46c766b52527ac46c766b53527ac46c766b54527ac4616c766b54c3009f6c766b5d527ac46c766b5dc3640e00006c766b5e527ac462ac036c766b00c3c00114907c907c9e6311006c766b51c3c001149c009c620400516c766b5f527ac46c766b5fc3640e00006c766b5e527ac46273036c766b52c36165e0066c766b55527ac46c766b53c36165d0066c766b56527ac46c766b55c3c00114907c907c9e6311006c766b56c3c001149c009c620400516c766b60527ac46c766b60c3640e00006c766b5e527ac4621a0361682d53797374656d2e457865637574696f6e456e67696e652e476574457865637574696e67536372697074486173686c766b57527ac452c57600083131313131313131c476516c766b57c3c461681553797374656d2e52756e74696d652e4e6f746966796161681953797374656d2e53746f726167652e476574436f6e746578746c766b58527ac46c766b58c36105626173655f6c766b52c37e617c681253797374656d2e53746f726167652e4765746c766b59527ac46c766b58c36105626173655f6c766b53c37e617c681253797374656d2e53746f726167652e4765746c766b5a527ac452c576000732323232323232c476516c766b59c3c461681553797374656d2e52756e74696d652e4e6f746966796152c57600083333333333333333c476516c766b5ac3c461681553797374656d2e52756e74696d652e4e6f74696679616c766b59c300907c907ca1630f006c766b5ac300a0009c620400516c766b0111527ac46c766b0111c3644c006151c576001e496e76616c696420746f6b656e20636f6e76657274696f6e20626173652ec461681553797374656d2e52756e74696d652e4e6f7469667961006c766b5e527ac46261016c766b58c30766656552617465617c681253797374656d2e53746f726167652e4765746c766b5b527ac452c57600083434343434343434c476516c766b5bc3c461681553797374656d2e52756e74696d652e4e6f74696679616c766b5bc3009f6c766b0112527ac46c766b0112c3643f006151c5760011496e76616c69642066656520726174652ec461681553797374656d2e52756e74696d652e4e6f7469667961006c766b5e527ac462b4006c766b54c36c766b5ac3956c766b59c39601646c766b5bc394950164966c766b5c527ac46c766b00c36c766b51c36c766b55c36c766b54c3615379517955727551727552795279547275527275652f04009c6c766b0113527ac46c766b0113c364050061f06c766b57c36c766b51c36c766b56c36c766b5cc361537951795572755172755279527954727552727565ee03009c6c766b0114527ac46c766b0114c364050061f0516c766b5e527ac46203006c766b5ec3616c756656c56b6c766b00527ac46c766b51527ac4616c766b51c300a16c766b53527ac46c766b53c3640f00006c766b54527ac462b0006114d2c124dd088190f709b684e0bc676d70c41b377661681b53797374656d2e52756e74696d652e436865636b5769746e657373009c6c766b55527ac46c766b55c3640f00006c766b54527ac46260006105626173655f6c766b00c37e6c766b52527ac461681953797374656d2e53746f726167652e476574436f6e746578746c766b52c36c766b51c3615272681253797374656d2e53746f726167652e50757461516c766b54527ac46203006c766b54c3616c756652c56b6c766b00527ac46161681953797374656d2e53746f726167652e476574436f6e746578746105626173655f6c766b00c37e617c681253797374656d2e53746f726167652e4765746c766b51527ac46203006c766b51c3616c756654c56b6c766b00527ac4616c766b00c300907c907c9f6310006c766b00c301649f009c620400516c766b51527ac46c766b51c3640f00006c766b52527ac4629f006114d2c124dd088190f709b684e0bc676d70c41b377661681b53797374656d2e52756e74696d652e436865636b5769746e657373009c6c766b53527ac46c766b53c3640e00006c766b52527ac4624f0061681953797374656d2e53746f726167652e476574436f6e7465787407666565526174656c766b00c3615272681253797374656d2e53746f726167652e50757461516c766b52527ac46203006c766b52c3616c756651c56b6161681953797374656d2e53746f726167652e476574436f6e746578740766656552617465617c681253797374656d2e53746f726167652e4765746c766b00527ac46203006c766b00c3616c756655c56b6c766b00527ac46c766b51527ac4616114d2c124dd088190f709b684e0bc676d70c41b377661681b53797374656d2e52756e74696d652e436865636b5769746e657373009c6c766b52527ac46c766b52c3640e00006c766b53527ac46288006c766b00c30087631a006c766b00c3826411006c766b51c3c001149c009c620400516c766b54527ac46c766b54c3640e00006c766b53527ac4624c0061681953797374656d2e53746f726167652e476574436f6e746578746c766b00c36c766b51c3615272681253797374656d2e53746f726167652e50757461516c766b53527ac46203006c766b53c3616c756652c56b6c766b00527ac46161681953797374656d2e53746f726167652e476574436f6e746578746c766b00c3617c681253797374656d2e53746f726167652e4765746c766b51527ac46203006c766b51c3616c756658c56b6c766b00527ac46c766b51527ac46c766b52527ac46c766b53527ac46153c576006c766b00c3c476516c766b51c3c476526c766b53c3c46c766b54527ac452c57600083131313131313131c476516c766b00c3c461681553797374656d2e52756e74696d652e4e6f746966796152c57600083131313131313131c476516c766b51c3c461681553797374656d2e52756e74696d652e4e6f746966796152c57600083131313131313131c476516c766b53c3c461681553797374656d2e52756e74696d652e4e6f74696679616c766b52c36c766b55527ac46c766b55c3087472616e736665726c766b54c3617c527a670000000000000000000000000000000000000000009c6c766b56527ac46c766b56c3640e00006c766b57527ac4620e00516c766b57527ac46203006c766b57c3616c7566";
                System.out.println("ContractAddress:" + Address.AddressFromVmCode(code).toHexString());
                ontSdk.vm().setCodeAddress(Address.AddressFromVmCode(code).toHexString());
                Account account = new Account(Helper.hexToBytes("957419a5ceaf5bd40e83e0fc59e71b0d7fef68149e3ea99f79149afc441549cd"),SignatureScheme.SHA256WITHECDSA);
                Transaction tx = ontSdk.vm().makeDeployCodeTransaction(code, true, "name",
                        "v1.0", "author", "email", "desp", account.getAddressU160().toBase58(),20600000,0);
                ontSdk.signTx(tx, new Account[][]{{account}});
                String txHex = Helper.toHexString(tx.toArray());
                System.out.println(tx.hash().toString());
                Object result = ontSdk.getConnect().syncSendRawTransaction(txHex);
                System.out.println(result);
                System.exit(0);
            }

            String swapAddress =  "1a5c9719acc4bfc6e9ab0027c757aa0ad71a6f4d";
            String youleAddress = "749a701ae89c0dbdab9b4b660ba84ee478004219";
            String quanjiaAddress  = "f9417534cbd3b09976f75f8597fe7be3fd88456b";
            String swapAddress2 = "016c1e1357727d539df2403f5a23fc2fe43ea579";

            //执行合约中的deploy方法
            if(false){
                Account adminAcct = new Account(Helper.hexToBytes("75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf"),SignatureScheme.SHA256WITHECDSA);
                Account account = new Account(Helper.hexToBytes("f9d2d30ffb22dffdf4f14ad6f1303460efc633ea8a3014f638eaa19c259bada1"),SignatureScheme.SHA256WITHECDSA);
                String abi = "{\"hash\":\"0xddd75e0e5c4aadc79f0ed83cd478920a37e9116f\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"name\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"symbol\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"decimals\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"deploy\",\"parameters\":[],\"returntype\":\"Boolean\"},{\"name\":\"totalSupply\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"balanceOf\",\"parameters\":[{\"name\":\"address\",\"type\":\"ByteArray\"}],\"returntype\":\"Integer\"},{\"name\":\"inflation\",\"parameters\":[{\"name\":\"count\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"recycle\",\"parameters\":[{\"name\":\"count\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"}],\"events\":[]}";
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "deploy";
                AbiFunction func = abiinfo.getFunction(name);
                boolean preExec = false;
                if (preExec){
                    Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(youleAddress),null,null,20000,0,func, preExec);
                    System.out.println(obj);
                }else {
                    Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(quanjiaAddress),adminAcct,adminAcct,20000,0,func, preExec);
                    System.out.println(obj);
                    Thread.sleep(6000);
                    System.out.println(ontSdk.getConnect().getSmartCodeEvent((String) obj));
                }
                return;
            }
            String laomiao = Account.getGcmDecodedPrivateKey("gpgMejEHzawuXG+ghLkZ8/cQsOJcs4BsFgFjSaqE7SC8zob8hqc6cDNhJI/NBkk+","1","AY5W6p4jHeZG2jjW6nS1p4KDUhcqLkU6jz",Base64.getDecoder().decode("tuLGZOimilSnypT91WrenQ=="),16384,SignatureScheme.SHA256WITHECDSA);
            Account laomiaoAcct = new Account(Helper.hexToBytes(laomiao),SignatureScheme.SHA256WITHECDSA);
            String youle = Account.getGcmDecodedPrivateKey("P2CK5DPZa+cfrkq9mK/1YeNU13cfPEm3ByUYrYNwrtqk9lc+UoM91UnOddc1wxWs","xinhao","ASUwFccvYFrrWR6vsZhhNszLFNvCLA5qS6",Base64.getDecoder().decode("tlWLpyfD54jM/27IvPXAWg=="),16384,SignatureScheme.SHA256WITHECDSA);
            Account youleAcct = new Account(Helper.hexToBytes(youle),SignatureScheme.SHA256WITHECDSA);
            //执行合约中的balanceOf方法
            if (false){
                Account account999 = new Account(Helper.hexToBytes("2ffe190d50ebcfbc8a765a4f25422275b900c476ba1fa72ea86fbdcf14900d74"),SignatureScheme.SHA256WITHECDSA);

                String abi = "{\"hash\":\"0xddd75e0e5c4aadc79f0ed83cd478920a37e9116f\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"name\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"symbol\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"decimals\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"deploy\",\"parameters\":[],\"returntype\":\"Boolean\"},{\"name\":\"totalSupply\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"balanceOf\",\"parameters\":[{\"name\":\"address\",\"type\":\"ByteArray\"}],\"returntype\":\"Integer\"},{\"name\":\"inflation\",\"parameters\":[{\"name\":\"count\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"recycle\",\"parameters\":[{\"name\":\"count\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"}],\"events\":[]}";
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "balanceOf";
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue(Address.parse(swapAddress2).toArray());
                boolean preExec = true;
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(quanjiaAddress),null,null,20000,0,func, preExec);
                System.out.println(obj);
                return;
            }
            //
            //GetFeeRate
            if (false){
                Account account999 = new Account(Helper.hexToBytes("2ffe190d50ebcfbc8a765a4f25422275b900c476ba1fa72ea86fbdcf14900d74"),SignatureScheme.SHA256WITHECDSA);

                AbiInfo abiinfo = JSON.parseObject(swapAbi, AbiInfo.class);
                String name = "GetFeeRate";
                AbiFunction func = abiinfo.getFunction(name);
                boolean preExec = true;
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(swapAddress2),null,null,20000,0,func, preExec);
                System.out.println(obj);
                return;
            }
            Account adminAcct = new Account(Helper.hexToBytes("75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf"),SignatureScheme.SHA256WITHECDSA);
            //SetFeeRate
            if (false){
                Account account999 = new Account(Helper.hexToBytes("2ffe190d50ebcfbc8a765a4f25422275b900c476ba1fa72ea86fbdcf14900d74"),SignatureScheme.SHA256WITHECDSA);

                AbiInfo abiinfo = JSON.parseObject(swapAbi, AbiInfo.class);
                String name = "SetFeeRate";
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue(5L);
                boolean preExec = false;
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(swapAddress2),adminAcct,adminAcct,20000,0,func, preExec);
                System.out.println(obj);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent((String)obj));
                return;
            }
            //GetTokenBase
            if (false){
                Account account999 = new Account(Helper.hexToBytes("2ffe190d50ebcfbc8a765a4f25422275b900c476ba1fa72ea86fbdcf14900d74"),SignatureScheme.SHA256WITHECDSA);

                AbiInfo abiinfo = JSON.parseObject(swapAbi, AbiInfo.class);
                String name = "GetTokenBase";
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue("YLT");
                boolean preExec = true;
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(swapAddress2),null,null,20000,0,func, preExec);
                System.out.println(obj);
                return;
            }

            //SetContractHash
            if (false){
                Account account999 = new Account(Helper.hexToBytes("2ffe190d50ebcfbc8a765a4f25422275b900c476ba1fa72ea86fbdcf14900d74"),SignatureScheme.SHA256WITHECDSA);

                AbiInfo abiinfo = JSON.parseObject(swapAbi, AbiInfo.class);
                String name = "SetContractHash";
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue("YLT", Helper.reverse(Address.parse(youleAddress).toArray()));
                boolean preExec = false;
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(swapAddress2),adminAcct,adminAcct,20000,0,func, preExec);
                System.out.println(obj);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent((String)obj));
                return;
            }
            //GetContractHash
            if (false){
                Account account999 = new Account(Helper.hexToBytes("2ffe190d50ebcfbc8a765a4f25422275b900c476ba1fa72ea86fbdcf14900d74"),SignatureScheme.SHA256WITHECDSA);

                AbiInfo abiinfo = JSON.parseObject(swapAbi, AbiInfo.class);
                String name = "GetContractHash";
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue("QJT");
                boolean preExec = true;
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(swapAddress2),null,null,20000,0,func, preExec);
                System.out.println(obj);
                return;
            }
            //SetTokenBase
            if (false){
                Account account999 = new Account(Helper.hexToBytes("2ffe190d50ebcfbc8a765a4f25422275b900c476ba1fa72ea86fbdcf14900d74"),SignatureScheme.SHA256WITHECDSA);

                AbiInfo abiinfo = JSON.parseObject(swapAbi, AbiInfo.class);
                String name = "SetTokenBase";
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue("QJT", 1L);
                boolean preExec = false;
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(swapAddress2),adminAcct,adminAcct,20000,0,func, preExec);
                System.out.println(obj);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent((String)obj));
                return;
            }
            if(false){
                com.github.ontio.sdk.wallet.Account account9999999 = ontSdk.getWalletMgr().importAccount("8p2q0vLRqyfKmFHhnjUYVWOm12kPm78JWqzkTOi9rrFMBz624KjhHQJpyPmiSSOa","111111","AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe",Base64.getDecoder().decode("KbiCUr53CZUfKG1M3Gojjw=="));
                Account account = ontSdk.getWalletMgr().getAccount(account9999999.address, "111111", account9999999.getSalt());
                ontSdk.nativevm().ont().sendTransfer(account,account3.getAddressU160().toBase58(),100000,account,200000,0);
                System.out.println(ontSdk.getConnect().getBalance(account9999999.address));
                System.out.println(Helper.toHexString(account.serializePrivateKey()));
                System.out.println(ontSdk.nativevm().ong().unboundOng(account.getAddressU160().toBase58()));
                ontSdk.nativevm().ong().withdrawOng(account,account.getAddressU160().toBase58(),359943000000L,account,200000,0);
                return;
            }

            //Swap
            if (true){
                com.github.ontio.sdk.wallet.Account account9999999 = ontSdk.getWalletMgr().importAccount("8p2q0vLRqyfKmFHhnjUYVWOm12kPm78JWqzkTOi9rrFMBz624KjhHQJpyPmiSSOa","111111","AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe",Base64.getDecoder().decode("KbiCUr53CZUfKG1M3Gojjw=="));
                Account account = ontSdk.getWalletMgr().getAccount(account9999999.address, "111111", account9999999.getSalt());

                AbiInfo abiinfo = JSON.parseObject(swapAbi, AbiInfo.class);
                String name = "Swap";
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue(adminAcct.getAddressU160().toArray(),Address.decodeBase58("AWf8NiLzXSDf1JB2Ae6YUKSHke4yLHMVCm").toArray(),"YLT","QJT",10L);
                boolean preExec = false;
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(swapAddress2),adminAcct,adminAcct,20474,0,func, preExec);
                System.out.println(obj);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent((String)obj));
                return;
            }
            //youle   749a701ae89c0dbdab9b4b660ba84ee478004219
            //laomiao 2ab4d126665141f5e3273dbe4935e2b4fe207552

            if(true){

                Account account999 = new Account(Helper.hexToBytes("2ffe190d50ebcfbc8a765a4f25422275b900c476ba1fa72ea86fbdcf14900d74"),SignatureScheme.SHA256WITHECDSA);

                Account account = new Account(Helper.hexToBytes("f9d2d30ffb22dffdf4f14ad6f1303460efc633ea8a3014f638eaa19c259bada1"),SignatureScheme.SHA256WITHECDSA);
                String abi = "{\"hash\":\"0xddd75e0e5c4aadc79f0ed83cd478920a37e9116f\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"name\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"symbol\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"decimals\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"deploy\",\"parameters\":[],\"returntype\":\"Boolean\"},{\"name\":\"totalSupply\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"balanceOf\",\"parameters\":[{\"name\":\"address\",\"type\":\"ByteArray\"}],\"returntype\":\"Integer\"},{\"name\":\"inflation\",\"parameters\":[{\"name\":\"count\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"recycle\",\"parameters\":[{\"name\":\"count\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"}],\"events\":[]}";
                abi = "{\"hash\":\"0xac941e00d99a7daa1d72530f01b1a8e5899bfb78\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"name\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"symbol\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"decimals\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"deploy\",\"parameters\":[],\"returntype\":\"Boolean\"},{\"name\":\"totalSupply\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"balanceOf\",\"parameters\":[{\"name\":\"address\",\"type\":\"ByteArray\"}],\"returntype\":\"Integer\"},{\"name\":\"inflation\",\"parameters\":[{\"name\":\"count\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"recycle\",\"parameters\":[{\"name\":\"count\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"transferMulti\",\"parameters\":[{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Boolean\"}],\"events\":[]}";
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "transfer";
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue(youleAcct.getAddressU160().toArray(), Address.parse(Helper.reverse("1d6f7aba5129271efa0f1e38323d7fa0b3cd11f4")).toArray(),1000L);
                boolean preExec = false;
                Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("df57f2c82646b96ce2ae68dba27460958b46abf7"),youleAcct,adminAcct,20000,500,func, preExec);
                System.out.println(obj);
                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent((String) obj));
                return;
            }
            //youle   749a701ae89c0dbdab9b4b660ba84ee478004219
            //laomiao 2ab4d126665141f5e3273dbe4935e2b4fe207552
            //执行group合约中的SetContractHash方法
            if(false){
                com.github.ontio.sdk.wallet.Account adminAcc = ontSdk.getWalletMgr().importAccount("bBkseVtWdArWNc2OfVnNONt+FV+6RRgvle2ffxYNrOU3bzGzq3dG07PXkzlunfi2","xinhao","AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p",Base64.getDecoder().decode("1NF1DK9zgDyB2cAcSRfB6w=="));
                //有乐管理员Account
                Account youleadminAcct = ontSdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p","xinhao");
                com.github.ontio.sdk.wallet.Account quanjiaadminAcc = ontSdk.getWalletMgr().importAccount("zJ2+ScaXH04G7zHh9QatqJTx4aZS1ehJ4TQlPAJ6n4qZZfzV6iWfC0novEzNwzil","xinhao","ANTPeXCffDZCaCXxY9u2UdssB2EYpP4BMh",Base64.getDecoder().decode("nAb1JNde0mC2PDYs9knxcQ=="));
                //全家管理员Account
                Account quanjiaadminAcct = ontSdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p","xinhao");
                abi = "{\"hash\":\"0x7d59e893207e729410553d055cc976c262cc9e1b\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"YL\",\"parameters\":[{\"name\":\"method\",\"type\":\"String\"},{\"name\":\"parameter\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"GroupTransfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"param\",\"type\":\"Array\"}],\"returntype\":\"Boolean\"},{\"name\":\"SetContractHash\",\"parameters\":[{\"name\":\"key\",\"type\":\"String\"},{\"name\":\"hash\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"GetContractHash\",\"parameters\":[{\"name\":\"key\",\"type\":\"String\"}],\"returntype\":\"ByteArray\"}],\"events\":[]}";
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "deploy";
                name = "SetContractHash";//对应的preExec 是false
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue("laomiao",Helper.reverse(Address.parse("2ab4d126665141f5e3273dbe4935e2b4fe207552").toArray()));

                boolean preExec = false;
                if (preExec){
                    Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("fb2ce5bd31bfb50662d95aaef1badcb8f8b71ce1"),null,null,20000,0,func, preExec);
                    System.out.println(obj);
                }else {
                    Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("44f1f4ee6940b4f162d857411842f2d533892084"),quanjiaadminAcct,quanjiaadminAcct,20000,0,func, preExec);

                }
                return;
            }
            //youle   749a701ae89c0dbdab9b4b660ba84ee478004219
            //laomiao 2ab4d126665141f5e3273dbe4935e2b4fe207552
            //执行group合约中的GetContractHash方法
            if(false){
                com.github.ontio.sdk.wallet.Account adminAcc = ontSdk.getWalletMgr().importAccount("bBkseVtWdArWNc2OfVnNONt+FV+6RRgvle2ffxYNrOU3bzGzq3dG07PXkzlunfi2","xinhao","AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p",Base64.getDecoder().decode("1NF1DK9zgDyB2cAcSRfB6w=="));
                //有乐管理员Account
                Account youleadminAcct = ontSdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p","xinhao");
                com.github.ontio.sdk.wallet.Account quanjiaadminAcc = ontSdk.getWalletMgr().importAccount("zJ2+ScaXH04G7zHh9QatqJTx4aZS1ehJ4TQlPAJ6n4qZZfzV6iWfC0novEzNwzil","xinhao","ANTPeXCffDZCaCXxY9u2UdssB2EYpP4BMh",Base64.getDecoder().decode("nAb1JNde0mC2PDYs9knxcQ=="));
                //全家管理员Account
                Account quanjiaadminAcct = ontSdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p","xinhao");
                abi = "{\"hash\":\"0x7d59e893207e729410553d055cc976c262cc9e1b\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"YL\",\"parameters\":[{\"name\":\"method\",\"type\":\"String\"},{\"name\":\"parameter\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"GroupTransfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"param\",\"type\":\"Array\"}],\"returntype\":\"Boolean\"},{\"name\":\"SetContractHash\",\"parameters\":[{\"name\":\"key\",\"type\":\"String\"},{\"name\":\"hash\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"GetContractHash\",\"parameters\":[{\"name\":\"key\",\"type\":\"String\"}],\"returntype\":\"ByteArray\"}],\"events\":[]}";
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "GetContractHash"; //对应的preExec 是true
                AbiFunction func = abiinfo.getFunction(name);
                func.setParamsValue("laomiao"); //根据key查询合约hash

                boolean preExec = true;
                if (preExec){
                    Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("d755c02caf5741b1c3352a06a41271716a1fca78"),null,null,20000,0,func, preExec);
                    System.out.println(obj);
                }else {
                    Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse("fb2ce5bd31bfb50662d95aaef1badcb8f8b71ce1"),quanjiaadminAcct,quanjiaadminAcct,20000,0,func, preExec);

                }
                return;
            }
            //youle   749a701ae89c0dbdab9b4b660ba84ee478004219
            //laomiao 2ab4d126665141f5e3273dbe4935e2b4fe207552
            //如果是GroupTransfer   就用下面的方法构造参数，，执行该方法之前 需要先部署好有乐合约和老庙合约
            //groupTransfer  单独测试
            if(false){
                Account account999 = new Account(Helper.hexToBytes("75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf"),SignatureScheme.SHA256WITHECDSA);
                com.github.ontio.sdk.wallet.Account adminAcc = ontSdk.getWalletMgr().importAccount("bBkseVtWdArWNc2OfVnNONt+FV+6RRgvle2ffxYNrOU3bzGzq3dG07PXkzlunfi2","xinhao","AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p",Base64.getDecoder().decode("1NF1DK9zgDyB2cAcSRfB6w=="));
                Account youleadminAcct = ontSdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p","xinhao");
                com.github.ontio.sdk.wallet.Account quanjiaadminAcc = ontSdk.getWalletMgr().importAccount("zJ2+ScaXH04G7zHh9QatqJTx4aZS1ehJ4TQlPAJ6n4qZZfzV6iWfC0novEzNwzil","xinhao","ANTPeXCffDZCaCXxY9u2UdssB2EYpP4BMh",Base64.getDecoder().decode("nAb1JNde0mC2PDYs9knxcQ=="));
                Account quanjiaadminAcct = ontSdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p","xinhao");
                abi = "{\"hash\":\"0x7d59e893207e729410553d055cc976c262cc9e1b\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"YL\",\"parameters\":[{\"name\":\"method\",\"type\":\"String\"},{\"name\":\"parameter\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"GroupTransfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"param\",\"type\":\"Array\"}],\"returntype\":\"Boolean\"},{\"name\":\"SetContractHash\",\"parameters\":[{\"name\":\"key\",\"type\":\"String\"},{\"name\":\"hash\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"GetContractHash\",\"parameters\":[{\"name\":\"key\",\"type\":\"String\"}],\"returntype\":\"ByteArray\"}],\"events\":[]}";
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);

                String functionName = "GroupTransfer";
                AbiFunction func = abiinfo.getFunction(functionName);

                //构造参数
                List list = new ArrayList();
                List list2 = new ArrayList();
                list2.add("youle");
                list2.add(1);
                list.add(list2);
                List list3 = new ArrayList();
                list3.add("laomiao");
                list3.add(1);
                list.add(list3);

                func.setParamsValue(account999.getAddressU160().toArray(),Address.decodeBase58("AacHGsQVbTtbvSWkqZfvdKePLS6K659dgp").toArray(),list);

                ontSdk.neovm().sendTransaction(Helper.reverse("44f1f4ee6940b4f162d857411842f2d533892084"),acct,acct,20000,500,func,false);
//
//                Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse("44f1f4ee6940b4f162d857411842f2d533892084"),null,params,account3.getAddressU160().toBase58(),20000,0);
//                ontSdk.signTx(tx,new Account[][]{{account999}});
//                ontSdk.addSign(tx,account3);
//                System.out.println(tx.hash().toHexString());
//                ontSdk.getConnect().sendRawTransaction(tx);
//                Thread.sleep(6000);
//
//                System.out.println(ontSdk.getConnect().getSmartCodeEvent(tx.hash().toHexString()));

            }
            //多转多的测试
            if(transferMulti){
                Account adminAcct2 = new Account(Helper.hexToBytes("5f2fe68215476abb9852cfa7da31ef00aa1468782d5ca809da5c4e1390b8ee45"),SignatureScheme.SHA256WITHECDSA);
                Account account = new Account(Helper.hexToBytes("f00dd7f5356e8aee93a049bdccc44ce91169e07ea3bec9f4e0142e456fd39bae"),SignatureScheme.SHA256WITHECDSA);
                //多转多  特殊处理   不用function
                String name = "transferMulti";
                List list = new ArrayList();
                List list2 = new ArrayList();
                list2.add(account.getAddressU160().toArray());
                list2.add(acct1.getAddressU160().toArray());
                list2.add(1);
                list.add(list2);
                List list3 = new ArrayList();
                list3.add(adminAcct.getAddressU160().toArray());
                list3.add(acct2.getAddressU160().toArray());
                list3.add(1);
                list.add(list3);

                //多转多的特殊处理，
                List listF = new ArrayList<Object>();
                listF.add(name.getBytes());
                listF.add(list);
                byte[] params = BuildParams.createCodeParamsScript(listF);
                Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse("ad0bd8c715fcec8d7b3bed0fd1c88fcc8ef43927"),null,params,account.getAddressU160().toBase58(),20000,0);
                ontSdk.signTx(tx,new Account[][]{{account}});
                ontSdk.addSign(tx,adminAcct);
                ontSdk.getConnect().sendRawTransaction(tx);

                Thread.sleep(6000);
                System.out.println(ontSdk.getConnect().getSmartCodeEvent(tx.hash().toHexString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Account getAccount(String enpri,String password,String address,String salt) throws Exception {
        String privateKey = Account.getGcmDecodedPrivateKey(enpri,password,address,Base64.getDecoder().decode(salt),16384,SignatureScheme.SHA256WITHECDSA);
        Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
//        System.out.println(Helper.toHexString(account.serializePublicKey()));
        return account;
    }

    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://polaris1.ont.io";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.openWalletFile("nep5.json");


        return wm;
    }
}
