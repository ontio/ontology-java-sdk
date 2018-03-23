package com.github.ontio.sdk.exception;

/**
 * Created by zx on 2018/2/1.
 */
class ExceptionConst {
	public static final String Flag_Code = "CCC";
	public static final String Flag_Message = "MMM";
	public static final String Json_CodeMessage = "{\"Code\":\"CCC\",\"Message\":\"MMM\"}";
	public static final String Json_ErrorDesc = "{\"Type\":\"%s\",\"Error\":%s,\"Desc\":\"%s\"}";
	public static final long Code_NetWorkError = 60000;
	public static final long Code_DatabaseError = 60001;
	public static final long Code_NoBalanceError = 60002;
	public static final long Code_DecryptoError = 60003;
	public static final long Code_EncryptoError = 60004;
	public static final long Code_DeserializeBlockError = 60005;
	public static final long Code_DeserializeTxError = 60006;
	public static final long Code_ComposeIssTxError = 60007;
	public static final long Code_ComposeTrfTxError = 60008;

	public static final long Code_SigIncompleteError = 60009;
	public static final long Code_ArgumentError = 60011;
	public static final long Code_AddressError = 60012;
	public static final long Code_AssetIdError = 60013;
	public static final long Code_AmountError = 60014;
	public static final long Code_TxidError = 60015;
	public static final long Code_ParmError = 60016;
}
