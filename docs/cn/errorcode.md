<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# 错误码

| 返回代码 | 描述信息 | 说明 |
| :---- | ----------------------------- | ----------------- |
| 0 | SUCCESS | 成功 |
| 41001 | SESSION_EXPIRED | 会话无效或已过期（ 需要重新登录） |
| 41002 | SERVICE_CEILING | 达到服务上限 |
| 41003 | ILLEGAL_DATAFORMAT | 不合法数据格式 |
| 41004 | INVALID_VERSION| 不合法的版本 |
| 42001 | INVALID_METHOD | 无效的方法 |
| 42002 | INVALID_PARAMS | 无效的参数 |
| 43001 | INVALID_TRANSACTION | 无效的交易 |
| 43002 | INVALID_ASSET | 无效的资产 |
| 43003 | INVALID_BLOCK | 无效的块 |
| 44001 | UNKNOWN_TRANSACTION | 找不到交易 |
| 44002 | UNKNOWN_ASSET | 找不到资产 |
| 44003 | UNKNOWN_BLOCK | 找不到块 |
| 44004 | UNKNWN_CONTRACT | 找不到合约 |
| 45001 | INTERNAL_ERROR | 内部错误 |
| 47001 | SMARTCODE_ERROR| 智能合约错误 |
|51001  |  InvalidParams |Account Error,invalid params|
|51002  |  UnsupportedKeyType |Account Error,unsupported key type|
|51003  |  InvalidMessage |Account Error,invalid message|
|51004  |  WithoutPrivate |Account Error,account without private key cannot generate signature|
|51005  |  InvalidSM2Signature |Account Error,invalid SM2 signature parameter, ID (String) excepted|
|51006  |  AccountInvalidInput |Account Error,account without public key cannot verify signature|
|51007  |  AccountWithoutPublicKey |Account Error,unknown key type|
|51008  |  UnknownKeyType |Account Error,null input|
|51009  |  NullInput |Account Error,invalid data|
|51010  |  InvalidData |Account Error,invalid params|
|51011  |  Decoded3bytesError |Account Error,decoded 3 bytes error|
|51012  |  DecodePrikeyPassphraseError |Account Error,decode prikey passphrase error|
|51013  |  PrikeyLengthError |Account Error,Prikey length error|
|52001  |  InputError |Uint256 Error,input error|
|52002  |  ChecksumNotValidate |Base58 Error,Checksum does not validate|
|52003  |  InputTooShort |Base58 Error,Input too short|
|52004  |  UnknownCurve |Curve Error,unknown curve|
|52005  |  UnknownCurveLabel |Curve Error,unknown curve label|
|52006  |  UnknownAsymmetricKeyType |keyType Error,unknown asymmetric key type|
|52007  |  InvalidSignatureData |Signature Error,invalid signature data: missing the ID parameter for SM3withSM2|
|52008  |  InvalidSignatureDataLen |Signature Error,invalid signature data length|
|52009  |  MalformedSignature |Signature Error,malformed signature|
|52010  |  UnsupportedSignatureScheme |Signature Error,unsupported signature scheme:|
|53001  |  TxDeserializeError |Core Error,Transaction deserialize failed|
|53002  |  BlockDeserializeError |Core Error,Block deserialize failed|
|58001  |  SendRawTxError |SmartCodeTx Error,sendRawTransaction error|
|58002  |  TypeError |SmartCodeTx Error,type error|
|58003  |  NullCodeHash |OntIdTx Error,null codeHash|
|58004  |  ParamError |OntIdTx Error,param error|
|58005  |  DidNull |OntIdTx Error,SendDid or receiverDid is null in metaData|
|58006  |  NotExistCliamIssuer |OntIdTx Error,Not exist cliam issuer|
|58007  |  NotFoundPublicKeyId |OntIdTx Error,not found PublicKeyId|
|58008  |  PublicKeyIdErr |OntIdTx Error,PublicKeyId err|
|58009  |  BlockHeightNotMatch |OntIdTx Error,BlockHeight not match|
|58010  |  NodesNotMatch |OntIdTx Error,nodes not match|
|58011  |  ResultIsNull |OntIdTx Error,result is null|
|58012  |  AssetNameError |OntAsset Error,asset name error|
|58013  |  DidError |OntAsset Error,Did error|
|58014  |  NullPkId |OntAsset Error,null pkId|
|58015  |  NullClaimId |OntAsset Error,null claimId|
|58016  |  NullKeyOrValue |RecordTx Error,null key or value|
|58017  |  NullKey |RecordTx Error,null  key|
|58018  |  GetAccountByAddressErr |WalletManager Error,getAccountByAddress err|
|58019  |  WebsocketNotInit |OntSdk Error,websocket not init|
|58020  |  ConnRestfulNotInit |OntSdk Error,connRestful not init|
|58021  |  SetParamsValueValueNumError |AbiFunction Error,setParamsValue value num error|
|58022  |  InvalidUrl |Interfaces Error,Invalid url:|
|58023  |  AESailed |ECIES Error,AES failed initialisation -|
|59000  |  OtherError| other error|
