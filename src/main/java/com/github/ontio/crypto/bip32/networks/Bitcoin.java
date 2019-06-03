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

package com.github.ontio.crypto.bip32.networks;

import com.github.ontio.crypto.bip32.Network;

public enum Bitcoin implements Network {
    MAIN_NET {
        @Override
        public int getPrivateVersion() {
            return 0x488ade4;
        }

        @Override
        public int getPublicVersion() {
            return 0x0488b21e;
        }

        @Override
        public byte p2pkhVersion() {
            return 0;
        }

        @Override
        public byte p2shVersion() {
            return 0x05;
        }
    }
}