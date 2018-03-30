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

package com.github.ontio.network.rest;

/**
 *
 */
public class UrlConsts {

	public static String Url_send_transaction = "/api/v1/transaction";
	public static String Url_get_transaction = "/api/v1/transaction/";
	public static String Url_get_generate_block_time = "/api/v1/node/generateblocktime";
	public static String Url_get_node_count = "/api/v1/node/connectioncount";
	public static String Url_get_block_height = "/api/v1/block/height";
	public static String Url_get_block_by_height = "/api/v1/block/details/height/";
	public static String Url_get_block_by_hash = "/api/v1/block/details/hash/";
	public static String Url_get_account_balance = "/api/v1/balance/";
	public static String Url_get_contract_state = "/api/v1/contract/";
	public static String Url_get_smartcodeevent_txs_by_height = "/api/v1/smartcode/event/transactions/";
	public static String Url_get_smartcodeevent_by_txhash = "/api/v1/smartcode/event/txhash/";
	public static String Url_get_block_height_by_txhash = "/api/v1/block/height/txhash/";
	public static String Url_get_storage = "/api/v1/storage/";
}
