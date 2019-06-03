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
import com.github.ontio.crypto.bip32.Networks;

public enum DefaultNetworks implements Networks {
    INSTANCE(new NetworkCollection(Bitcoin.MAIN_NET));

    private final Networks networks;

    DefaultNetworks(final Networks networks) {

        this.networks = networks;
    }

    @Override
    public Network findByPrivateVersion(final int privateVersion) {
        return networks.findByPrivateVersion(privateVersion);
    }

    @Override
    public Network findByPublicVersion(final int publicVersion) {
        return networks.findByPublicVersion(publicVersion);
    }
}