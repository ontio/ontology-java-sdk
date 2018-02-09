package ontology.account;

import ontology.common.Fixed8;
import ontology.common.UInt160;
import ontology.common.UInt256;
import ontology.core.TransactionInput;
import ontology.common.Common;

/**
 * Account's change, used to pay for the transaction
 * 
 * @author 12146
 *
 */
public class Coin {
    public TransactionInput input;
    public UInt256 assetId;
    public Fixed8 value;
    public UInt160 scriptHash;
    public String stateStr;

    //[NonSerialized]
    private String _address = null;
    public String address() {
        if (_address == null) {
            _address = Common.toAddress(scriptHash);
        }
        return _address;
    }

    //[NonSerialized]
    private CoinState state;
    public CoinState getState() {
        return state;
    }
    
    public TransactionInput key() {
        return input;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (!(obj instanceof Coin)) {
        	return false;
        }
        return input.equals(((Coin) obj).input);
    }

    @Override
    public int hashCode() {
        return input.hashCode();
    }
}
