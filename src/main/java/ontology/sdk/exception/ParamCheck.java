package ontology.sdk.exception;

import java.lang.*;
import java.util.List;

public class ParamCheck {
	
	public static boolean isNotEmptyOrNull(String... objs) {
		if(objs == null) {
			return false;
		}
		for(String ss: objs) {
			if(ss == null || ss.trim().length() == 0) {
				return false;
			}
		}
		return true;
	}
	
	
	public static boolean isValidAddress(String... objs) {
		if(objs == null) {
			return false;
		}
		for(String ss: objs) {
			if(ss == null || ss.length() != 34) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isValidAmount(long amount) {
		if(amount <= 0) {
			return false;
		}
		return true;
	}
	
	public static boolean isValidAssetId(String... objs) {
		if(objs == null) {
			return false;
		}
		for(String ss: objs) {
			if(ss == null || ss.length() != 64) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isValidTxid(String... objs) {
		return isValidAssetId(objs);
	}
	public static void checkStateUpdateParameter(String namespace, String key, String value, String controller) throws SDKException {
		if (!ParamCheck.isNotEmptyOrNull(namespace, key, value, controller)) {
			throw new SDKException(Error.getDescArgError(String.format("%s=%s,%s=%s,%s=%s,%s=%s", "namespace", namespace, "key", key, "value", value, "controller", controller)));
		}
		if (!ParamCheck.isValidAddress(controller)) {
			throw new SDKException(Error.getDescAddrError(String.format("%s=%s,%s=%s", "controller", controller)));
		}
	}
	public static void checkRegisterParameter(String issuer, String name, long amount, String desc, String controller) throws SDKException {
		if (!ParamCheck.isNotEmptyOrNull(issuer, name, controller)) {
			throw new SDKException(Error.getDescArgError(String.format("%s=%s,%s=%s,%s=%s", "issuer", issuer, "name", name, "controller", controller)));
		}
		if (!ParamCheck.isValidAddress(issuer, controller)) {
			throw new SDKException(Error.getDescAddrError(String.format("%s=%s,%s=%s", "issuer", issuer, "controller", controller)));
		}
	}
	public static void checkDestroyParameter(String issuer, String assetId, String txDesc) throws SDKException {
		if (!ParamCheck.isNotEmptyOrNull(issuer, assetId)) {
			throw new SDKException(Error.getDescArgError(String.format("%s=%s,%s=%s", "issuer", issuer, "assetId", assetId)));
		}
		if (!ParamCheck.isValidAddress(issuer)) {
			throw new SDKException(Error.getDescAddrError(String.format("%s=%s", "issuer", issuer)));
		}
	}

	public static void checkIssueAndTransferParameter(String sendAddr, String assetid, long amount, String recvAddr, String desc) throws SDKException {
		if (!ParamCheck.isNotEmptyOrNull(sendAddr, assetid, recvAddr)) {
			throw new SDKException(Error.getDescArgError(String.format("%s=%s,%s=%s,%s=%s", "sendAddr", sendAddr, "assetid", assetid, "recvAddr", recvAddr)));
		}
		if (!ParamCheck.isValidAddress(sendAddr, recvAddr)) {
			throw new SDKException(Error.getDescAddrError(String.format("%s=%s,%s=%s", "sendAddr", sendAddr, "recvAddr", recvAddr)));
		}
		if (!ParamCheck.isValidAmount(amount)) {
			throw new SDKException(Error.getDescAmountError(String.format("%s=%s", "amount", amount)));
		}
		if (!ParamCheck.isValidAssetId(assetid)) {
			throw new SDKException(Error.getDescAssetIdError(String.format("%s=%s", "assetid", assetid)));
		}
	}

}

