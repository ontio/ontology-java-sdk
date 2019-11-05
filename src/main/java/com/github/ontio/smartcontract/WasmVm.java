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

package com.github.ontio.smartcontract;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.scripts.WasmScriptBuilder;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.wasmvm.DeployWasmCode;
import com.github.ontio.smartcontract.wasmvm.InvokeWasmCode;

import java.util.List;

public class WasmVm {

    private OntSdk sdk;

    public WasmVm(OntSdk sdk) {
        this.sdk = sdk;
    }

    public DeployWasmCode makeDeployCodeTransaction(String codeStr, String name, String codeVersion, String author,
                                                    String email, String description, Address payer, long gasLimit,
                                                    long gasPrice) throws SDKException {
        if (name == null || name.equals("") || codeVersion == null || codeVersion.equals("") || author == null || author.equals("") || email == null || email.equals("") || description == null || description.equals("")) {
            throw new SDKException(ErrorCode.InvalidInterfaceParam);
        }
        return new DeployWasmCode(codeStr, name, codeVersion, author, email, description, payer, gasLimit, gasPrice);
    }

    public InvokeWasmCode makeInvokeCodeTransaction(String contractHash, String method, List<Object> params, Address payer,
                                            long gasLimit, long gasPrice) {
        byte[] invokeCode = WasmScriptBuilder.createWasmInvokeCode(contractHash, method, params);
        return new InvokeWasmCode(invokeCode, payer, gasLimit, gasPrice);
    }

}
