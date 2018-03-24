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

import com.github.ontio.common.Fixed8;
import com.github.ontio.common.UInt256;

/**
 *  交易结果，表示交易中资产的变化量
 */
public class TransactionResult {
    /**
     *  资产编号
     */
    public final UInt256 assetId;
    /**
     *  该资产的变化量
     */
    public final Fixed8 amount;
    
    public TransactionResult(UInt256 assetId, Fixed8 amount) {
    	this.assetId = assetId;
    	this.amount = amount;
    }
}
