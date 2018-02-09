package ontology.core;

/**
 *  资产转移交易
 *
 */
public class TransferTransaction extends Transaction {
	
	public TransferTransaction() {
		super(TransactionType.TransferTransaction);
	}
}
