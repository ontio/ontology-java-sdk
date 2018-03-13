package ontology.sdk.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ontology.common.*;
import ontology.core.payload.IssueTransaction;
import ontology.core.Transaction;
import ontology.core.TransactionAttribute;
import ontology.core.TransactionInput;
import ontology.core.TransactionOutput;
import ontology.sdk.info.Coin;
import ontology.sdk.exception.Error;
import ontology.sdk.exception.CoinException;
import ontology.sdk.exception.CoinRuntimeException;
import ontology.sdk.info.asset.UTXOsInfo;
import ontology.sdk.info.asset.UTXOInfo;

import static ontology.common.Common.toAddress;

/**
 * 余额管理器
 * 
 * @author 12146
 *
 */
public class CoinMgr {

	public <T extends Transaction> T makeTransaction(ConnectMgr connManager, T tx, UInt160 from) throws CoinException {
		if (tx.outputs == null) {
			throw new IllegalArgumentException("tx.output is null");
		}
		if (tx.attributes == null) {
			tx.attributes = new TransactionAttribute[0];
		}
		try {
			// 待支付的资产
			Map<UInt256, Fixed8> payTotal = Arrays.stream(tx instanceof IssueTransaction ? new TransactionOutput[0]
					: tx.outputs).collect(Collectors.groupingBy(p -> p.assetId)).entrySet().stream().collect(
					Collectors.toMap(
							p -> p.getKey(),
							p -> Fixed8.sum(p.getValue().toArray(new TransactionOutput[0]), o -> o.value)));
			// 可支付的资产
			Map<UInt256, ontology.account.Coin[]> payCoins = payTotal.entrySet().stream().collect(
					Collectors.toMap(
							p -> p.getKey(),
							p -> findUnspentCoins(connManager,p.getKey(), p.getValue(), from)));
			if (payCoins.values().stream().anyMatch(p -> p == null)) {
				throw new CoinRuntimeException(Error.getDescNoBalance("Insufficient balance3"));
			}

			// 找零
			Map<UInt256, Fixed8> input_sum = payCoins.entrySet().stream().collect(
					Collectors.toMap(
							p -> p.getKey(),
							p -> Fixed8.sum(p.getValue(), c -> c.value)));
			UInt160 change_address = from == null ? from : from;
			List<TransactionOutput> outputsNew = new ArrayList<TransactionOutput>(Arrays.asList(tx.outputs));
			for (Entry<UInt256, Fixed8> entry : input_sum.entrySet()) {
				Fixed8 pay = payTotal.get(entry.getKey());
				if (entry.getValue().compareTo(pay) > 0) {
					TransactionOutput output = new TransactionOutput();
					output.assetId = entry.getKey();
					output.value = entry.getValue().subtract(pay);
					output.scriptHash = change_address;
					outputsNew.add(output);
				}
			}
			tx.inputs = payCoins.values().stream().flatMap(p -> Arrays.stream(p)).map(p -> p.input).toArray(TransactionInput[]::new);
			tx.outputs = outputsNew.toArray(new TransactionOutput[outputsNew.size()]);
		} catch (CoinRuntimeException e) {
			throw new CoinException(e.getMessage(), e);
		}
		return tx;
	}
	private ontology.account.Coin[] findUnspentCoins(ConnectMgr connManager, UInt256 assetId, Fixed8 amount, UInt160 from) {
		String scriptHash = toAddress(from);

		// 可用余额
		long canUseCoin = -1;
		try {
			canUseCoin = connManager.getBalance(scriptHash,assetId.toString());
		} catch (Exception e){
			throw new CoinRuntimeException(Error.getDescArgError(String.format("can't find sufficient balance by scriptHash:%s, assetId:%s", scriptHash, assetId)), e);
		}
		long l = amount.toLong();
		if(canUseCoin < l) {
			throw new CoinRuntimeException(Error.getDescNoBalance("Insufficient balance1"+ "  "+l + " "+ canUseCoin+" "+ Helper.toHexString(assetId.toArray())));
		}
		// 可用UTXO
		List<Coin> list = new ArrayList<>();
		List<UTXOInfo> utxos = null;
		try {
			utxos = connManager.getUTXO(scriptHash,assetId.toString());
			for(int i =0;i<utxos.size();i++){
				UTXOInfo utxo = utxos.get(i);
				Coin coin = new Coin();
				coin.index = Integer.parseInt(utxo.Index);
				coin.txid = utxo.Txid;
				coin.value = utxo.Value/100000000;
				coin.scriptHash = scriptHash;
				coin.assetId = assetId.toString();
				coin.state = "Unspent";

				ontology.account.Coin c = new ontology.account.Coin();
				list.add(coin);
			}
		} catch (Exception e){
			throw new CoinRuntimeException(Error.getDescArgError(String.format("can't find sufficient utxo by scriptHash:%s, assetId:%s", scriptHash, assetId)), e);
		}

		//return list.toArray(DNA.Account.Coin[]::new);
		return findUnspentCoins(to(list).stream(), assetId, amount);
	}

    private List<ontology.account.Coin> to(List<Coin> list) {
		return list.stream().map(p -> {
    		ontology.account.Coin coin = new ontology.account.Coin();
    		coin.input = new TransactionInput();
			coin.input.prevHash = UInt256.parse(p.txid);
    		coin.input.prevIndex = (short) p.index;
    		coin.assetId = UInt256.parse(p.assetId);
    		coin.value = Fixed8.parse(String.valueOf(p.value));
    		coin.scriptHash = Common.toScriptHash(p.scriptHash);
    		coin.stateStr = p.state;
    		return coin;
    	}).collect(Collectors.toList());
    }
    
    

    protected static ontology.account.Coin[] findUnspentCoins(Stream<ontology.account.Coin> unspents, UInt256 assetId, Fixed8 amount) {
    	ontology.account.Coin[] unspentsAsset = unspents.filter(p -> p.assetId.equals(assetId)).toArray(ontology.account.Coin[]::new);
        Fixed8 sum = Fixed8.sum(unspentsAsset, p -> p.value);
        
        // 余额不足
        if (sum.compareTo(amount) < 0) {
        	throw new CoinRuntimeException(Error.getDescNoBalance("Insufficient balance2"));
        }
        // 余额刚好
        if (sum.equals(amount)) {
        	return unspentsAsset;
        }
        // 余额足够: 1+2+3 -> 6 | 1+2+4 -> 6
        Arrays.sort(unspentsAsset, (a, b) -> -a.value.compareTo(b.value));
        int i = 0;
        while (unspentsAsset[i].value.compareTo(amount) <= 0) {
            amount = amount.subtract(unspentsAsset[i++].value);
        }
        if (amount.equals(Fixed8.ZERO)) {	 // 零钱 == 支付
            return Arrays.stream(unspentsAsset).limit(i).toArray(ontology.account.Coin[]::new);
        } else {	// 零钱 > 支付
        	ontology.account.Coin[] result = new ontology.account.Coin[i + 1];
        	System.arraycopy(unspentsAsset, 0, result, 0, i);
        	for (int j = unspentsAsset.length - 1; j >= 0; j--) {
        		if (unspentsAsset[j].value.compareTo(amount) >= 0) {
        			result[i] = unspentsAsset[j];
        			break;
        		}
        	}
        	return result;
        }
    }

    public List<ontology.account.Coin> queryAccountAsset(ConnectMgr connManager, String address, String assetId) {
    	try {
			// 可用UTXO
			List<Coin> list = new ArrayList<>();
			List<UTXOInfo> utxos = null;
			try {
				utxos = connManager.getUTXO(address,assetId.toString());
				for(int i =0;i<utxos.size();i++){
					UTXOInfo utxo = utxos.get(i);
					Coin coin = new Coin();
					coin.index = Integer.parseInt(utxo.Index);
					coin.txid = utxo.Txid;
					coin.value = utxo.Value/100000000;
					coin.scriptHash = address;
					coin.assetId = assetId.toString();
					coin.state = "Unspent";

					ontology.account.Coin c = new ontology.account.Coin();
					list.add(coin);
				}
			} catch (Exception e){
				throw new CoinRuntimeException(Error.getDescArgError(String.format("can't find sufficient utxo by scriptHash:%s, assetId:%s", address, assetId)), e);
			}
			return to(list);
		} catch (Exception e) {
			throw new CoinRuntimeException(Error.getDescDatabaseError(e.getMessage()), e);
		}
    }

	public List<ontology.account.Coin> queryAccountAsset(ConnectMgr connManager, String address) {
		try {
			// 可用UTXO
			List<Coin> list = new ArrayList<>();
			List<UTXOInfo> utxos = null;
			List<UTXOsInfo> utxo2s = null;
			try {
				utxo2s = connManager.getUTXOs(address);

				for(int t =0;t<utxo2s.size();t++) {
					UTXOsInfo info = utxo2s.get(t);
					utxos = info.Utxo;
					String assetId = info.AssetId;
					for (int i = 0; i < utxos.size(); i++) {
						UTXOInfo utxo = utxos.get(i);
						Coin coin = new Coin();
						coin.index = Integer.parseInt(utxo.Index);
						coin.txid = utxo.Txid;
						coin.value = utxo.Value / 100000000;
						coin.scriptHash = address;
						coin.assetId = assetId;
						coin.state = "Unspent";

						ontology.account.Coin c = new ontology.account.Coin();
						list.add(coin);
					}
				}
			} catch (Exception e){
				throw new CoinRuntimeException(Error.getDescArgError(String.format("can't find sufficient utxo by scriptHash:%s", address)), e);
			}
			return to(list);
		} catch (Exception e) {
			throw new CoinRuntimeException(Error.getDescDatabaseError(e.getMessage()), e);
		}
	}
    
}

