package ontology.core;

import ontology.common.Fixed8;
import ontology.common.UInt256;

/**
 *  交易结果，表示交易中资产的变化量
 */
public class TransactionResult {
    /**
     *  资产编号
     */
    public final UInt256 assetId;
    /**
     *  该资产的变化量
     */
    public final Fixed8 amount;
    
    public TransactionResult(UInt256 assetId, Fixed8 amount) {
    	this.assetId = assetId;
    	this.amount = amount;
    }
}
