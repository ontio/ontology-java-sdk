package com.github.ontio.smartcontract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.sdk.exception.SDKException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;

public class EvmTransaction {
    public static final String ASSET_ONG_CODEHASH = "0200000000000000000000000000000000000000";
    OntSdk ontSdk;

    @Before
    public void setUp() throws SDKException {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("https://POLARIS3.ONT.IO:10334");
        ontSdk.setDefaultConnect(ontSdk.getRestful());
    }

    @Test
    public void parseTransaction() throws Exception {
        String txHash = "0xab0f0b72bde0112db1cffc661542bf7f7c3b593809ada9ccccde51d2abdfb85e";
        String convertHex = Helper.toHexString(Helper.reverse(Helper.hexToBytes(txHash.substring(2))));
        JSONObject eventObj = getEventObjByTxnHash(convertHex);

        JSONArray notifyList = eventObj.getJSONArray("Notify");
        if (notifyList.size() > 0) {

            for (int i = 0, len = notifyList.size(); i < len; i++) {

                JSONObject notifyObj = (JSONObject) notifyList.get(i);
                String contractAddress = notifyObj.getString("ContractAddress");
                if (ASSET_ONG_CODEHASH.equals(contractAddress)) {
                    String evmStates = notifyObj.getString("States");
                    if (evmStates.startsWith("0x")) {
                        evmStates = evmStates.substring(2);
                    }
                    ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(evmStates));
                    BinaryReader reader = new BinaryReader(bais);
                    byte[] addressBytes = reader.readBytes(20);
                    // contract address ong 0000000000000000000000000000000000000002 ont 0000000000000000000000000000000000000001
                    String contract = Helper.toHexString(addressBytes);
                    System.out.println(contract);
                    int length = reader.readInt();
                    for (int j = 0; j < length; j++) {
                        // 		transferSig := "Transfer(address,address,uint256)"
                        //		// this should always be 0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef
                        //		topic[0] = crypto.Keccak256Hash([]byte(transferSig))
                        //		topic[1] = common.BytesToHash(from[:])
                        //		topic[2] = common.BytesToHash(to[:])
                        //		val := common.BytesToHash(value.Bytes())
                        //		sl := &types.StorageLog{
                        //			Address: common.BytesToAddress(utils.OngContractAddress[:]),
                        //			Topics:  topic,
                        //			Data:    val[:],
                        //		}
                        //		stateDB.AddLog(sl)
                        byte[] TopicBytes = reader.readBytes(32);
                        String topic = Helper.toHexString(TopicBytes);
                        if (j == 0) {
                            System.out.println("transferSig: " + topic);
                        } else if (j == 1) {
                            System.out.println("from evm addr: 0x" + topic.substring(24));
                        } else {
                            System.out.println("to evm addr: 0x" + topic.substring(24));
                            Address addr = Address.parse(topic.substring(24));
                            System.out.println("to ontology: " + addr.toBase58());
                        }
                    }
                    byte[] dataBytes = reader.readVarBytes();
                    String data = Helper.toHexString(dataBytes);
                    System.out.println("hex value: " +  data);
                    BigInteger value = new BigInteger(dataBytes);
                    System.out.println("value: " + value);

                }
            }
        }
    }


    private JSONObject getEventObjByTxnHash(String txnHash) throws Exception {
        JSONObject eventObj = new JSONObject();
        while (true) {
            try {
                eventObj = (JSONObject) ontSdk.getConnect().getSmartCodeEvent(txnHash);
                break;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return eventObj;
    }


}
