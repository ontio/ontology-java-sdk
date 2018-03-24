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
	public static final long Code_TxHashError = 60015;
	public static final long Code_ParmError = 60016;
}
