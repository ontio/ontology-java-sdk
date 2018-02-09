package ontology.account;

public enum CoinState {
    Unconfirmed(0x00),
    Unspent(0x01),
    Spending(0x02),
    Spent(0x03),
    SpentAndClaimed(0x04)
    
    ;
    
    private byte value;
    
    public int value() {
    	return value;
    }
    CoinState(int value) {
    	this.value = (byte) value;
    }
}