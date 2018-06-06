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

package com.github.ontio.common;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @date 2018/4/24
 */
public class ErrorCode {
    public static String getError(int code, String msg) {
        Map map = new HashMap();
        map.put("Error", code);
        map.put("Desc", msg);
        return JSON.toJSONString(map);
    }

    //account error
    public static String InvalidParams = getError(51001, "Account Error,invalid params");
    public static String UnsupportedKeyType = getError(51002, "Account Error,unsupported key type");
    public static String InvalidMessage = getError(51003, "Account Error,invalid message");
    public static String WithoutPrivate = getError(51004, "Account Error,account without private key cannot generate signature");
    public static String InvalidSM2Signature = getError(51005, "Account Error,invalid SM2 signature parameter, ID (String) excepted");
    public static String AccountInvalidInput = getError(51006, "Account Error,account invalid input");
    public static String AccountWithoutPublicKey = getError(51007, "Account Error,account without public key cannot verify signature");
    public static String UnknownKeyType = getError(51008, "Account Error,unknown key type");
    public static String NullInput = getError(51009, "Account Error,null input");
    public static String InvalidData = getError(51010, "Account Error,invalid data");
    public static String Decoded3bytesError = getError(51011, "Account Error,decoded 3 bytes error");
    public static String DecodePrikeyPassphraseError = getError(51012, "Account Error,decode prikey passphrase error.");
    public static String PrikeyLengthError = getError(51013, "Account Error,Prikey length error");
    public static String EncryptedPriKeyError = getError(51014, "Account Error,Prikey length error");
    public static String encryptedPriKeyAddressPasswordErr = getError(51015, "Account Error,encryptedPriKey address password not match.");
    public static String EncriptPrivateKeyError = getError(51016, "Account Error, encript privatekey error,");


    //
    public static String ParamLengthErr = getError(52001, "Uint256 Error,param length error");
    public static String ChecksumNotValidate = getError(52002, "Base58 Error,Checksum does not validate");
    public static String InputTooShort = getError(52003, "Base58 Error,Input too short");
    public static String UnknownCurve = getError(52004, "Curve Error,unknown curve");
    public static String UnknownCurveLabel = getError(52005, "Curve Error,unknown curve label");
    public static String UnknownAsymmetricKeyType = getError(52006, "keyType Error,unknown asymmetric key type");
    public static String InvalidSignatureData = getError(52007, "Signature Error,invalid signature data: missing the ID parameter for SM3withSM2");
    public static String InvalidSignatureDataLen = getError(52008, "Signature Error,invalid signature data length");
    public static String MalformedSignature = getError(52009, "Signature Error,malformed signature");
    public static String UnsupportedSignatureScheme = getError(52010, "Signature Error,unsupported signature scheme:");
    public static String DataSignatureErr = getError(52011, "Signature Error,Data signature error.");
    public static String UnSupportOperation = getError(52012, "Address Error, UnsupportedOperationException");


    //Core Error
    public static String TxDeserializeError = getError(53001, "Core Error,Transaction deserialize failed");
    public static String BlockDeserializeError = getError(53002, "Core Error,Block deserialize failed");


    //merkle error
    public static String MerkleVerifierErr = getError(54001, "Wrong params: the tree size is smaller than the leaf index");
    public static String TargetHashesErr = getError(54002, "targetHashes error");

    public static String ConstructedRootHashErr(String msg) {
        return getError(54003, "Other Error," + msg);
    }

    public static String AsserFailedHashFullTree = getError(54004, "assert failed in hash full tree");
    public static String LeftTreeFull = getError(54005, "left tree always full");


    //SmartCodeTx Error
    public static String SendRawTxError = getError(58001, "SmartCodeTx Error,sendRawTransaction error");
    public static String TypeError = getError(58002, "SmartCodeTx Error,type error");

    //OntIdTx Error
    public static String NullCodeHash = getError(58003, "OntIdTx Error,null codeHash");
    public static String ParamError = getError(58004, "param error,");

    public static String ParamErr(String msg) {
        return getError(58005, msg);
    }

    public static String DidNull = getError(58006, "OntIdTx Error,SendDid or receiverDid is null in metaData");
    public static String NotExistCliamIssuer = getError(58007, "OntIdTx Error,Not exist cliam issuer");
    public static String NotFoundPublicKeyId = getError(58008, "OntIdTx Error,not found PublicKeyId");
    public static String PublicKeyIdErr = getError(58009, "OntIdTx Error,PublicKeyId err");
    public static String BlockHeightNotMatch = getError(58010, "OntIdTx Error,BlockHeight not match");
    public static String NodesNotMatch = getError(58011, "OntIdTx Error,nodes not match");
    public static String ResultIsNull = getError(58012, "OntIdTx Error,result is null");
    public static String CreateOntIdClaimErr = getError(58013, "OntIdTx Error, createOntIdClaim error");
    public static String VerifyOntIdClaimErr = getError(58014, "OntIdTx Error, verifyOntIdClaim error");
    public static String WriteVarBytesError = getError(58015, "OntIdTx Error, writeVarBytes error");
    public static String SendRawTransactionPreExec = getError(58016, "OntIdTx Error, sendRawTransaction PreExec error");
    public static String SenderAmtNotEqPasswordAmt = getError(58017, "OntIdTx Error, senders amount is not equal password amount");
    public static String ExpireErr = getError(58017, "OntIdTx Error, expire is wrong");
    public static String GetStatusErr(String msg){return getError(58017, "GetStatus Error," + msg);} ;


    //OntAsset Error
    public static String AssetNameError = getError(58101, "OntAsset Error,asset name error");
    public static String DidError = getError(58102, "OntAsset Error,Did error");
    public static String NullPkId = getError(58103, "OntAsset Error,null pkId");
    public static String NullClaimId = getError(58104, "OntAsset Error,null claimId");
    public static String AmountError = getError(58105, "OntAsset Error,amount or gas is less than or equal to zero");
    public static String ParamLengthNotSame = getError(58105, "OntAsset Error,param length is not the same");

    //RecordTx Error
    public static String NullKeyOrValue = getError(58201, "RecordTx Error,null key or value");
    public static String NullKey = getError(58202, "RecordTx Error,null  key");


    //OntSdk Error
    public static String WebsocketNotInit = getError(58301, "OntSdk Error,websocket not init");
    public static String ConnRestfulNotInit = getError(58302, "OntSdk Error,connRestful not init");


    //abi error
    public static String SetParamsValueValueNumError = getError(58401, "AbiFunction Error,setParamsValue value num error");
    public static String InvalidUrlErr = getError(58402, "Interfaces Error,Invalid url:");

    public static String InvalidUrl(String msg) {
        return getError(58403, "Invalid url:" + msg);
    }

    //WalletManager Error
    public static String GetAccountByAddressErr = getError(58501, "WalletManager Error,getAccountByAddress err");

    public static String OtherError(String msg) {
        return getError(59000, "Other Error," + msg);
    }
}
