package com.github.ontio.sdk.exception;

public class Error {
	public static String getCodeMsg(String code, String message) {
		return ExceptionConst.Json_CodeMessage.replace(ExceptionConst.Flag_Code, code).replace(ExceptionConst.Flag_Message, message);
	}
	
	public static String getDescTxidError(String message) {
		return getErrorDesc("Txid Error",ExceptionConst.Code_TxidError, message);
	}
	public static String getDescAmountError(String message) {
		return getErrorDesc("Amount Error",ExceptionConst.Code_AmountError, message);
	}
	public static String getDescAssetIdError(String message) {
		return getErrorDesc("AssetId Error",ExceptionConst.Code_AssetIdError, message);
	}
	public static String getDescAddrError(String message) {
		return getErrorDesc("Address Error",ExceptionConst.Code_AddressError, message);
	}
	public static String getDescArgError(String message) {
		return getErrorDesc("Argument Error",ExceptionConst.Code_ArgumentError, message);
	}
	public static String getDescSigIncomplete(String message) {
		return getErrorDesc("SigIncomplete",ExceptionConst.Code_SigIncompleteError, message);
	}
	
	
	public static String getDescComposeTransferTx(String message) {
		return getErrorDesc("ComposeTrfTransaction",ExceptionConst.Code_ComposeTrfTxError, message);
	}
	public static String getDescComposeIssueTx(String message) {
		return getErrorDesc("ComposeIssTransaction",ExceptionConst.Code_ComposeIssTxError, message);
	}
	public static String getDescDeserializeTx(String message) {
		return getErrorDesc("Deserialize Error",ExceptionConst.Code_DeserializeTxError, message);
	}
	public static String getDescDeserializeBlock(String message) {
		return getErrorDesc("Deserialize Error",ExceptionConst.Code_DeserializeBlockError, message);
	}
	public static String getDescEncrypto(String message) {
		return getErrorDesc("Encrypto Error",ExceptionConst.Code_EncryptoError, message);
	}
	public static String getDescDecrypto(String message) {
		return getErrorDesc("Decrypto Error",ExceptionConst.Code_DecryptoError, message);
	}
	public static String getDescNoBalance(String message) {
		return getErrorDesc("NoBalance",ExceptionConst.Code_NoBalanceError, message);
	}
	public static String getDescDatabaseError(String message) {
		return getErrorDesc("Database Error",ExceptionConst.Code_DatabaseError, message);
	}
	public static String getDescNetworkError(String message) {
		return getErrorDesc("NetWork Error",ExceptionConst.Code_NetWorkError, message);
	}
	public static String getDescParaError(String message) {
		return getErrorDesc("Parm Error",ExceptionConst.Code_ParmError, message);
	}

	public static String getErrorDesc(String errType,long error, String message) {
		return String.format(ExceptionConst.Json_ErrorDesc,errType, error, message);
	}

}

/**

异常代码：
60000 网络错误
60001 数据库操作错误
60002 余额不足
60003 解密错误
60004 加密错误
60005 反序列化Block错误
60006 反序列化Transaction错误
60007 分发组合交易错误
60008 资产注销交易错误

 */
