package com.github.ontio.network.rest;

import java.util.stream.Stream;

import com.github.ontio.common.Fixed8;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.Block;
import com.github.ontio.core.Blockchain;
import com.github.ontio.core.Transaction;
import com.github.ontio.core.payload.Vote;

public class RestBlockchain extends Blockchain {
	private RestClient rest;
	public RestBlockchain(RestClient rest) {
		this.rest = rest;
	}

	@Override
	public int height() throws Exception {
		return rest.getBlockHeight();
	}
	
	@Override
	public Block getBlock(int height) throws Exception {
		return rest.getBlock(height);
	}
	
	@Override
    public Block getBlock(UInt256 hash) throws Exception {
		return rest.getBlock(hash.toString());
    }
    
    @Override
	public Transaction getTransaction(UInt256 hash) throws Exception {
		return rest.getRawTransaction(hash.toString());
    }

    @Override
	public UInt256 currentBlockHash() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
    
	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean addBlock(Block block) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void addHeaders(Iterable<Block> headers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsUnspent(UInt256 hash, int index) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public UInt256[] getLeafHeaderHashes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getNextBlock(UInt256 hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UInt256 getNextBlockHash(UInt256 hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fixed8 getQuantityIssued(UInt256 asset_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSysFeeAmount(UInt256 hash) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Stream<Vote> getVotes(Stream<Transaction> others) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDoubleSpend(Transaction tx) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public UInt256 getBlockHash(int height) {
		// TODO Auto-generated method stub
		return null;
	}

}
