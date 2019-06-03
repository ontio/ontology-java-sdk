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

package com.github.ontio.crypto.bip32;

import com.github.ontio.crypto.bip32.derivation.CkdFunction;

public interface Derivation<Path> {

    /**
     * Traverse the nodes from the root key node to find the node referenced by the path.
     *
     * @param rootKey     The root of the path
     * @param path        The path to follow
     * @param ckdFunction Allows you to follow one link
     * @param <Key>       The type of node we are visiting
     * @return The final node found at the end of the path
     */
    <Key> Key derive(final Key rootKey, final Path path, final CkdFunction<Key> ckdFunction);
}