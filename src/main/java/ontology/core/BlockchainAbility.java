package ontology.core;

import java.util.EnumSet;

/**
 * 表示特定区块链实现所提供的功能
 */
public enum BlockchainAbility {
    /**
     *  必须实现的虚函数：GetBlockAndHeight, GetBlockHeight, GetNextBlock, GetNextBlockHash, GetSysFeeAmount
     */
    BlockIndexes(0x01),

    /**
     *  必须实现的虚函数：ContainsAsset, GetAssets, GetEnrollments
     */
    TransactionIndexes(0x02),

    /**
     *  必须实现的虚函数：ContainsUnspent, GetUnclaimed, GetUnspent, GetVotes, IsDoubleSpend
     */
    UnspentIndexes(0x04),

    /**
     *  必须实现的虚函数：GetQuantityIssued
     */
    Statistics(0x08);

	public static final EnumSet<BlockchainAbility> None = EnumSet.noneOf(BlockchainAbility.class);
	public static final EnumSet<BlockchainAbility> All = EnumSet.allOf(BlockchainAbility.class);
    private byte value;

    BlockchainAbility(int v) {
        value = (byte)v;
    }

    public byte getByte()  {
        return value;
    }
}
