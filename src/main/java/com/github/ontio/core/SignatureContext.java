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

package com.github.ontio.core;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.ontio.common.Address;
import com.github.ontio.core.asset.Sig;
import org.bouncycastle.math.ec.ECPoint;

import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.crypto.ECC;
import com.github.ontio.io.json.JArray;
import com.github.ontio.io.json.JBoolean;
import com.github.ontio.io.json.JObject;
import com.github.ontio.io.json.JString;

/**
 *
 */
public class SignatureContext {
    public final Signable signable;
    public final Address[] addressU160;
    private final Map<ECPoint, byte[]>[] signatures;
    
    private final boolean[] completed;

    public boolean isCompleted() {
        for (boolean b : completed) {
        	if (!b) {
        		return false;
        	}
        }
        return true;
    }


	public SignatureContext(Signable signable) {
        this.signable = signable;
        this.addressU160 = signable.getAddressU160ForVerifying();
        this.signatures = (Map<ECPoint, byte[]>[]) Array.newInstance(Map.class, addressU160.length);
        this.completed = new boolean[addressU160.length];
    }


    public boolean add(Address address, ECPoint pubkey, byte[] signature) {
        for (int i = 0; i < addressU160.length; i++) {
            if (addressU160[i].equals(address)) {
                if (signatures[i] == null) {
                	signatures[i] = new HashMap<ECPoint, byte[]>();
                }
                signatures[i].put(pubkey, signature);
                completed[i] = true;
                return true;
            }
        }
        return false;
    }

    public Sig[] getSigs() {
        if (!isCompleted()) {
            throw new IllegalStateException();
        }
        Sig[] sigs = new Sig[signatures.length];
        for (int i = 0; i < sigs.length; i++) {
            sigs[i] = new Sig();
            sigs[i].M++;
            sigs[i].pubKeys = new ECPoint[signatures[i].size()];
            sigs[i].sigData = new byte[signatures[i].size()][];
            int j = 0;
            for (Map.Entry e : signatures[i].entrySet()) {
                sigs[i].pubKeys[j] = (ECPoint) e.getKey();
                sigs[i].sigData[j++] = (byte[]) e.getValue();
            }
        }
        return sigs;
    }

}
