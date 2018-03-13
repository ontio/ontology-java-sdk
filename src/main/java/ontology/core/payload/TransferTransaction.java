package ontology.core.payload;

import ontology.core.Transaction;
import ontology.core.TransactionType;

/**
 *  资产转移交易
 *
 */
public class TransferTransaction extends Transaction {
	
	public TransferTransaction() {
		super(TransactionType.TransferTransaction);
	}
}
