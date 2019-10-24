/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.network.connect;

import java.io.IOException;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.exception.ConnectorException;

public interface IConnector {

	String getUrl();
	Object sendRawTransaction(boolean preExec,String userid,String hexData) throws ConnectorException, IOException;
	Object sendRawTransaction(String hexData) throws ConnectorException, IOException;
	Transaction getRawTransaction(String txhash) throws ConnectorException, IOException;
	Object getRawTransactionJson(String txhash) throws ConnectorException, IOException;
	int getNodeCount() throws ConnectorException, IOException;
	int getBlockHeight() throws ConnectorException, IOException;
	Block getBlock(int height) throws ConnectorException, IOException;
	Block getBlock(String hash) throws ConnectorException, IOException ;
	String getBlockBytes(int height) throws ConnectorException, IOException ;
	String getBlockBytes(String hash) throws ConnectorException, IOException ;
	Object getBlockJson(int height) throws ConnectorException, IOException;
	Object getBlockJson(String hash) throws ConnectorException, IOException;

	Object getBalance(String address) throws ConnectorException, IOException;

	Object getContract(String hash) throws ConnectorException, IOException;
	Object getContractJson(String hash) throws ConnectorException, IOException;
	Object getSmartCodeEvent(int height) throws ConnectorException, IOException;
	Object getSmartCodeEvent(String hash) throws ConnectorException, IOException;
	int getBlockHeightByTxHash(String hash) throws ConnectorException, IOException;

	String getStorage(String codehash,String key) throws ConnectorException, IOException;
	Object getMerkleProof(String hash) throws ConnectorException, IOException;
	String getAllowance(String asset,String from,String to) throws ConnectorException, IOException;
	Object getMemPoolTxCount() throws ConnectorException, IOException;
	Object getMemPoolTxState(String hash) throws ConnectorException, IOException;
	String getVersion() throws ConnectorException, IOException;
	String getGrantOng(String address) throws ConnectorException, IOException;
	int getNetworkId() throws ConnectorException, IOException;
	String getSideChainData(int sideChainID) throws ConnectorException, IOException;
}