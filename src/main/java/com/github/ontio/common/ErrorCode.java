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
    public static String getError(int code,String msg){
        Map map = new HashMap();
        map.put("Error",code);
        map.put("Desc",msg);
        return JSON.toJSONString(map);
    }
    //account error
    public static String InvalidParams = getError(51001,"Account Error,invalid params");
    public static String UnsupportedKeyType = getError(51002,"Account Error,unsupported key type");
    public static String InvalidMessage = getError(51003,"Account Error,invalid message");
    public static String WithoutPrivate = getError(51004,"Account Error,account without private key cannot generate signature");
    public static String InvalidSM2Signature = getError(51005,"Account Error,invalid SM2 signature parameter, ID (String) excepted");
    public static String AccountInvalidInput = getError(51006,"Account Error,account invalid input");
    public static String AccountWithoutPublicKey = getError(51007,"Account Error,account without public key cannot verify signature");
    public static String UnknownKeyType = getError(51008,"Account Error,unknown key type");
    public static String NullInput = getError(51009,"Account Error,null input");
    public static String InvalidData = getError(51010,"Account Error,invalid data");
    public static String Decoded3bytesError = getError(51011,"Account Error,decoded 3 bytes error");
    public static String DecodePrikeyPassphraseError = getError(51012,"Account Error,decode prikey passphrase error.");
    public static String PrikeyLengthError = getError(51013,"Account Error,Prikey length error");

    public static String InputError = getError(52001,"Uint256 Error,input error");
    public static String ChecksumNotValidate = getError(52002,"Base58 Error,Checksum does not validate");
    public static String InputTooShort = getError(52003,"Base58 Error,Input too short");
    public static String UnknownCurve = getError(52004,"Curve Error,unknown curve");
    public static String UnknownCurveLabel = getError(52005,"Curve Error,unknown curve label");
    public static String UnknownAsymmetricKeyType = getError(52006,"keyType Error,unknown asymmetric key type");
    public static String InvalidSignatureData = getError(52007,"Signature Error,invalid signature data: missing the ID parameter for SM3withSM2");
    public static String InvalidSignatureDataLen = getError(52008,"Signature Error,invalid signature data length");
    public static String MalformedSignature = getError(52009,"Signature Error,malformed signature");
    public static String UnsupportedSignatureScheme = getError(52010,"Signature Error,unsupported signature scheme:");



    //transaction
    public static String TxDeserializeError = getError(53001,"Core Error,Transaction deserialize failed");
    public static String BlockDeserializeError = getError(53002,"Core Error,Block deserialize failed");


    //manager Error
    public static String SendRawTxError = getError(58001,"SmartCodeTx Error,sendRawTransaction error");
    public static String TypeError = getError(58002,"SmartCodeTx Error,type error");

    public static String NullCodeHash = getError(58003,"OntIdTx Error,null codeHash");
    public static String ParamError = getError(58004,"param error");
    public static String DidNull  = getError(58005,"OntIdTx Error,SendDid or receiverDid is null in metaData");
    public static String NotExistCliamIssuer = getError(58006,"OntIdTx Error,Not exist cliam issuer");
    public static String NotFoundPublicKeyId = getError(58007,"OntIdTx Error,not found PublicKeyId");
    public static String PublicKeyIdErr = getError(58008,"OntIdTx Error,PublicKeyId err");
    public static String BlockHeightNotMatch = getError(58009,"OntIdTx Error,BlockHeight not match");
    public static String NodesNotMatch = getError(58010,"OntIdTx Error,nodes not match");
    public static String ResultIsNull = getError(58011,"OntIdTx Error,result is null");
    public static String AssetNameError = getError(58012,"OntAsset Error,asset name error");
    public static String DidError = getError(58013,"OntAsset Error,Did error");
    public static String NullPkId = getError(58014,"OntAsset Error,null pkId");
    public static String NullClaimId = getError(58015,"OntAsset Error,null claimId");
    public static String AmountError = getError(58016,"OntAsset Error,amount is less than or equal to zero");


    public static String NullKeyOrValue = getError(58017,"RecordTx Error,null key or value");
    public static String NullKey = getError(58018,"RecordTx Error,null  key");

    public static String GetAccountByAddressErr = getError(58019,"WalletManager Error,getAccountByAddress err");
    public static String WebsocketNotInit = getError(58020,"OntSdk Error,websocket not init");
    public static String ConnRestfulNotInit = getError(58021,"OntSdk Error,connRestful not init");

    //abi error
    public static String SetParamsValueValueNumError = getError(58021,"AbiFunction Error,setParamsValue value num error");
    public static String InvalidUrl = getError(58022,"Interfaces Error,Invalid url:");
    public static String InvalidUrl(String msg){
        return getError(58023, "Invalid url:" + msg);
    }
    public static String AESailed = getError(58024,"ECIES Error,AES failed initialisation -");

    public static String UnSupportOperation = getError(58025,"UnsupportedOperationException");
    public static String EncriptPrivateKeyError = getError(58026,"encript privatekey error,");



    public static String OtherError(String msg) {
        return getError(59000, "Other Error," + msg);
    }
}
