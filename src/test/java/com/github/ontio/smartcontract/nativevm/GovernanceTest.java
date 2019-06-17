/*
 * Copyright (C) 2018-2019 The ontology Authors
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

package com.github.ontio.smartcontract.nativevm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GovernanceTest {
    @Test
    public void newPeerAttributes() {
        PeerAttributes peerAttributes1 = new PeerAttributes();
        assertNull(peerAttributes1.peerPubkey);
        assertEquals(0, peerAttributes1.maxAuthorize);
        assertEquals(0, peerAttributes1.t1PeerCost);
        assertEquals(0, peerAttributes1.t2PeerCost);
        assertEquals(0, peerAttributes1.tPeerCost);
        String peerPubKey = "0379eff8cc07441daad01234291ba3f3da3e323119d97d6f1875da5f414be470b9";
        PeerAttributes peerAttributes2 = new PeerAttributes(peerPubKey);
        assertEquals(peerPubKey, peerAttributes2.peerPubkey);
        assertEquals(0, peerAttributes2.maxAuthorize);
        assertEquals(100, peerAttributes2.t1PeerCost);
        assertEquals(100, peerAttributes2.t2PeerCost);
        assertEquals(100, peerAttributes2.tPeerCost);
    }
}
