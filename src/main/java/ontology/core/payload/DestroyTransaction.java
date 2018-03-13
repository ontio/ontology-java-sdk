package ontology.core.payload;

import ontology.core.Transaction;
import ontology.core.TransactionType;

/**
 * 注销资产
 * 
 * @author 12146
 *
 */
public class DestroyTransaction extends Transaction {

	public DestroyTransaction() {
		super(TransactionType.DestroyTransaction);
	}
}
