package demo.neo;


import com.alibaba.fastjson.JSON;
import com.github.neo.core.*;
import com.github.neo.core.transaction.TransactionNeo;
import com.github.neo.core.transaction.TransferTransaction;
import com.github.ontio.common.*;
import com.github.ontio.crypto.ECC;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.network.exception.RpcException;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bouncycastle.math.ec.ECPoint;
import java.util.*;
import java.util.stream.Collectors;

/**
 * neo sdk Demo
 * 
 *
 * 
 */
public class NeoTransferDemo {
	public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
	public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
	public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
	public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
	public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";
	public static String privatekey6 = "6c2c7eade4c5cb7c9d4d6d85bfda3da62aa358dd5b55de408d6a6947c18b9279";
	public static String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";
	public static String privatekey8 = "87a209d232d6b4f3edfcf5c34434aa56871c2cb204c263f6b891b95bc5837cac";
	public static String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";
	public static String privatekey10 = "e23237f33bf18ebed6fc648e765b56ed8ee9017fff1528f178fb9157284c153c";
	public static String contractAddr = "5bb169f915c916a5e30a3c13a5e0cd228ea26826";
	public static String nodeUrl = "http://seed2.neo.org:20332";
	public static String assetid = "c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b";
	public static void main(String[] args) throws Exception {
        System.out.println("Hi NEO, Nep-5 smartcontract invoke test!");
		com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), SignatureScheme.SHA256WITHECDSA);
		com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), SignatureScheme.SHA256WITHECDSA);
		com.github.ontio.account.Account acct3 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey3), SignatureScheme.SHA256WITHECDSA);
		com.github.ontio.account.Account acct4 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey4), SignatureScheme.SHA256WITHECDSA);
		com.github.ontio.account.Account acct5 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey5), SignatureScheme.SHA256WITHECDSA);
		com.github.ontio.account.Account acct6 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey6), SignatureScheme.SHA256WITHECDSA);
		com.github.ontio.account.Account acct7 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey7), SignatureScheme.SHA256WITHECDSA);
		Address multiSignAddr = Address.addressFromMultiPubKeys(2,acct1.serializePublicKey(),acct2.serializePublicKey());
		System.out.println("acct1:"+acct1.getAddressU160().toBase58());
		System.out.println("acct2:"+acct2.getAddressU160().toBase58());
		System.out.println("acct3:"+acct3.getAddressU160().toBase58());
		System.out.println("acct4:"+acct4.getAddressU160().toBase58());
		System.out.println("acct5:"+acct5.getAddressU160().toBase58());
		System.out.println("acct6:"+acct6.getAddressU160().toBase58());
		System.out.println("acct7:"+acct7.getAddressU160().toBase58());
		System.out.println("multiSignAddr:"+multiSignAddr.toBase58());
		if(false) {
			String input1 = "8af8bb6cb2540ebf019e7f96e467c675f50245306cd24e325872ae756fa73840";
			int prevIndex = 1;
			int allInputAmount = 979;
			int trfAmount = 1;
			TransactionInput txinput = new TransactionInput(UInt256.parse(input1),prevIndex);
			com.github.ontio.account.Account senderAcct = acct3;
			Address senderAddr = senderAcct.getAddressU160();
			Address recvAddr = acct7.getAddressU160();
			TransferTransaction tx = makeTransferTx(assetid, new TransactionInput[]{txinput}, allInputAmount,trfAmount,senderAddr, recvAddr);
			tx.scripts = new Program[1];
			tx.scripts[0] = new Program();
			tx.scripts[0].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(senderAcct, SignatureScheme.SHA256WITHECDSA)});
			tx.scripts[0].code =  Program.ProgramFromPubKey(senderAcct.serializePublicKey());
			System.out.println(tx.hash().toString());

			//send tx to neo node
			Object obj = sendRawTransaction(nodeUrl,tx.toHexString());
			System.out.println(obj);
		}
		if(false) { //multiSignAddr
			String input1 = "bdb4ee3a3ff7295fe91a2e445f14d172b6c9a06bfbde9b6d3774ed556c8fdf74";
			int prevIndex = 1;
			int allInputAmount = 14;
			int trfAmount = 1;
			TransactionInput txinput = new TransactionInput(UInt256.parse((input1)),prevIndex);
			Address senderAddr = multiSignAddr;
			Address recvAddr = acct1.getAddressU160();
			TransferTransaction tx = makeTransferTx(assetid,new TransactionInput[]{txinput}, allInputAmount,trfAmount,senderAddr, recvAddr);
			tx.scripts = new Program[1];
			tx.scripts[0] = new Program();
			tx.scripts[0].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(acct1, SignatureScheme.SHA256WITHECDSA),tx.sign(acct2, SignatureScheme.SHA256WITHECDSA)});
			tx.scripts[0].code =  Program.ProgramFromMultiPubKey(2,acct1.serializePublicKey(),acct2.serializePublicKey());
			System.out.println(tx.toHexString());

			//send tx to neo node
			Object obj = sendRawTransaction(nodeUrl,tx.toHexString());
			System.out.println(obj);
		}

		if(true) { //multi input
			String input0 = "c5952100d11b94ba0848db6d34b0c23e14e868dcf83d0570d85d23460a9d0ef9";
			String input1 = "fb8d0dc04545e44335b5e7e83ab6c61dfab629dd0871d1a3a656cdf13b76a5d0";
			String input2 = "0535316e3fecc4e849252545eded3db5d94481f33edcf6eb6912916d16e4ffb9";
			String input3 = "8af8bb6cb2540ebf019e7f96e467c675f50245306cd24e325872ae756fa73840";
			String input4 = "c3ed859686e554577998fcfc0028d66282e0625da8ff51849a612ddc4d87305e";
			int prevIndex = 0;
			int allInputAmount = 5;
			int trfAmount = 5;
			TransactionInput txinput2 = new TransactionInput(UInt256.parse((input0)),prevIndex);
			TransactionInput txinput4 = new TransactionInput(UInt256.parse((input1)),prevIndex);
			TransactionInput txinput5 = new TransactionInput(UInt256.parse((input2)),prevIndex);
			TransactionInput txinput6 = new TransactionInput(UInt256.parse((input3)),prevIndex);
			TransactionInput txinput7 = new TransactionInput(UInt256.parse((input4)),prevIndex);
			Address restAddr = acct4.getAddressU160();
			Address recvAddr = acct1.getAddressU160();
			TransferTransaction tx = makeTransferTx(assetid,new TransactionInput[]{txinput4,txinput2,txinput5,txinput6,txinput7}, allInputAmount,trfAmount,restAddr, recvAddr);
			tx.scripts = new Program[5];
			tx.scripts[3] = new Program();
			tx.scripts[3].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(acct4, SignatureScheme.SHA256WITHECDSA)});
			tx.scripts[3].code =  Program.ProgramFromPubKey(acct4.serializePublicKey());

			tx.scripts[1] = new Program();
			tx.scripts[1].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(acct5, SignatureScheme.SHA256WITHECDSA)});// 5 6 4 7
			tx.scripts[1].code =  Program.ProgramFromPubKey(acct5.serializePublicKey());

			tx.scripts[2] = new Program();
			tx.scripts[2].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(acct6, SignatureScheme.SHA256WITHECDSA)});
			tx.scripts[2].code =  Program.ProgramFromPubKey(acct6.serializePublicKey());

			tx.scripts[0] = new Program();
			tx.scripts[0].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(acct7, SignatureScheme.SHA256WITHECDSA)});
			tx.scripts[0].code =  Program.ProgramFromPubKey(acct7.serializePublicKey());

			tx.scripts[4] = new Program();
			tx.scripts[4].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(acct2, SignatureScheme.SHA256WITHECDSA)});
			tx.scripts[4].code =  Program.ProgramFromPubKey(acct2.serializePublicKey());

			for(int i=0;i<tx.scripts.length;i++){
			//	System.out.println(i+" "+Helper.toHexString(tx.scripts[i].code)+ " "+((Address.toScriptHash(tx.scripts[i].code).toBase58())));
			}
			tx.scripts = Arrays.stream(tx.scripts).sorted((o1, o2) -> {
				byte[] bs1 = new byte[o1.code.length-2];
				System.arraycopy(o1.code, 1, bs1, 0, bs1.length);
				byte[] bs2 = new byte[o2.code.length-2];
				System.arraycopy(o2.code, 1, bs2, 0, bs2.length);

				ECPoint pk1 = ECC.secp256r1.getCurve().decodePoint(bs1);
				ECPoint pk2 = ECC.secp256r1.getCurve().decodePoint(bs2);
//				return ECC.compare(pk1, pk2);
//				if(pk1.getYCoord().toString().compareTo(pk2.getYCoord().toString()) < 0) {
//					return pk1.getXCoord().toString().compareTo(pk2.getXCoord().toString());
//				}
//				return pk1.getYCoord().toString().compareTo(pk2.getYCoord().toString());
				return Helper.reverse(Address.toScriptHash(o1.code).toHexString()).compareTo( Helper.reverse(Address.toScriptHash(o2.code).toHexString()));
			}).toArray(Program[]::new);
			System.out.println();
			for(int i=0;i<tx.scripts.length;i++){
				System.out.println(i+" "+Helper.toHexString(tx.scripts[i].code)+ " "+((Address.toScriptHash(tx.scripts[i].code).toBase58())));
			}

			System.out.println(tx.toHexString());

			//send tx to neo node
			Object obj = sendRawTransaction(nodeUrl,tx.toHexString());
			System.out.println(obj);
		}

	}
	private static TransferTransaction makeTransferTx(String assetId, TransactionInput[] inputs, int allInputAmount,int amount,Address senderAddr,  Address recvAddr) throws Exception {
		TransferTransaction tx = new TransferTransaction();
		tx.attributes = new TransactionAttribute[1];
		tx.attributes[0] = new TransactionAttribute();
		tx.attributes[0].usage = TransactionAttributeUsage.DescriptionUrl;
		tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
		tx.inputs = inputs;
		for(int i=0;i<tx.inputs.length;i++){
			//System.out.println(i+" "+tx.inputs[i].prevHash.toHexString());
		}
//		tx.inputs = Arrays.stream(tx.inputs).sorted((o1, o2) -> {
//			return Helper.reverse(o1.prevHash.toHexString()).compareTo(Helper.reverse(o2.prevHash.toHexString()));
//		}).toArray(TransactionInput[]::new);

		//System.out.println();
		for(int i=0;i<tx.inputs.length;i++){
			//System.out.println(i+" "+tx.inputs[i].prevHash.toHexString());
		}
		if(allInputAmount > amount){
			tx.outputs = new TransactionOutput[2];
			tx.outputs[0] = new TransactionOutput();
			tx.outputs[0].assetId = UInt256.parse(assetId);
			tx.outputs[0].value = Fixed8.parse(String.valueOf(amount));
			tx.outputs[0].scriptHash = recvAddr;
			tx.outputs[1] = new TransactionOutput();
			tx.outputs[1].assetId = UInt256.parse(assetId);
			tx.outputs[1].value = Fixed8.parse(String.valueOf(allInputAmount-amount));
			tx.outputs[1].scriptHash = senderAddr;
		}else {
			tx.outputs = new TransactionOutput[1];
			tx.outputs[0] = new TransactionOutput();
			tx.outputs[0].assetId = UInt256.parse(assetId);
			tx.outputs[0].value = Fixed8.parse(String.valueOf(amount));
			tx.outputs[0].scriptHash = recvAddr;
		}
		return tx;
	}
	public static Object sendRawTransaction(String url,String sData) throws Exception {
		Object result = call(url,"sendrawtransaction", new Object[]{sData});
		return result;
	}
	public static Object getBalance(String url,String contractAddr,String addr) throws Exception {
		Object result = call(url,"getstorage", new Object[]{contractAddr,addr});
		return result;
	}
	public static Object call(String url,String method, Object... params) throws RpcException, IOException {
		Map req = makeRequest(method, params);
		Map response = (Map) send(url,req);
		System.out.println(JSON.toJSONString(response));
		if (response == null) {
			throw new RpcException(0, ErrorCode.ConnectUrlErr(  url + "response is null. maybe is connect error"));
		}
		else if (response.get("result")  != null) {
			return response.get("result");
		}
		else if (response.get("Result")  != null) {
			return response.get("Result");
		}
		else if (response.get("error") != null) {
			throw new RpcException(0,JSON.toJSONString(response));
		}
		else {
			throw new IOException();
		}
	}

	private static Map makeRequest(String method, Object[] params) {
		Map request = new HashMap();
		request.put("jsonrpc", "2.0");
		request.put("method", method);
		request.put("params", params);
		request.put("id", 1);
		System.out.println(String.format("POST %s,%s", nodeUrl,JSON.toJSONString(request)));
		return request;
	}


	public static Object send(String url,Object request) throws IOException {
		try {
			HttpURLConnection connection = (HttpURLConnection)  new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			try (OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream())) {
				w.write(JSON.toJSONString(request));
			}
			try (InputStreamReader r = new InputStreamReader(connection.getInputStream())) {
				StringBuffer temp = new StringBuffer();
				int c = 0;
				while ((c = r.read()) != -1) {
					temp.append((char) c);
				}
				//System.out.println("result:"+temp.toString());
				return JSON.parseObject(temp.toString(), Map.class);
			}
		} catch (IOException e) {
		}
		return null;
	}
}
