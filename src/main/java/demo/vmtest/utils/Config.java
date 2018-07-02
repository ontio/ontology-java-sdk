package demo.vmtest.utils;

import com.github.ontio.common.Address;
import com.github.ontio.core.payload.InvokeCode;
import com.github.ontio.core.transaction.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @date 2018/6/30
 */
public class Config {
    public String ContractAddress = "ContractAddress";
    public Map<String, byte[]> storageMap = new HashMap<>();
    public Transaction tx = new InvokeCode();

    public Map<String, byte[]> getStorageMap() {
        return storageMap;
    }

    public List<Address> GetSignatureAddresses() {
        if (tx.sigs == null) {
            return null;
        }
        List<Address> list = new ArrayList();
        for (int i = 0; i < tx.sigs.length; i++) {
            for (int j = 0; j < tx.sigs[i].pubKeys.length; j++) {
                if (tx.sigs[i].M == 1) {
                    Address address = Address.addressFromPubKey(tx.sigs[i].pubKeys[0]);
                    list.add(address);
                }
            }
        }
        return list;
    }
}
