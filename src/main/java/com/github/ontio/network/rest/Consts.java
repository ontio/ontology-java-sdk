package com.github.ontio.network.rest;

/**
 * Created by zx on 2018/2/1.
 */
public class Consts {

	public static String Url_send_transaction = "/api/v1/transaction";
	public static String Url_get_transaction = "/api/v1/transaction/";
	public static String Url_get_asset = "/api/v1/asset/";
	public static String Url_get_GenerateBlockTime = "/api/v1/node/generateblocktime";
	public static String Url_get_node_count = "/api/v1/node/connectioncount";
	public static String Url_get_block_height = "/api/v1/block/height";
	public static String Url_get_block_By_Height = "/api/v1/block/details/height/";
	public static String Url_get_block_By_Hash = "/api/v1/block/details/hash/";
	public static String Url_get_account_balance = "/api/v1/balance/";
	public static String Url_get_account_balances = "/api/v1/asset/balances/";
	public static String Url_get_UTXO_By_address_assetid = "/api/v1/asset/utxo/";
	public static String Url_get_UTXO_By_address = "/api/v1/asset/utxos/";
	public static String Url_get_block_height_db = "/blocks/service/blockHeight";
	public static String Url_get_block = "/blocks/service/oneBlockInfo/";
	public static String Url_get_StateUpdate = "/api/v1/stateupdate/";
	public static String Url_get_IdentityUpdate = "/api/v1/identity/";
	public static String Url_get_DDO = "/api/v1/ontid/ddo/";
	public static String Url_send_to_issService = "/api/transaction/assetIssue";
	public static String Url_send_to_trfService = "/api/transaction/assetTrans";
}
