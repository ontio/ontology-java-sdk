/*
 *  BIP32derivation
 *  Copyright (C) 2017-2019 Alan Evans, NovaCrypto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  Original source: https://github.com/NovaCrypto/BIP32derivation
 *  You can contact the authors via github issues.
 */

package com.github.ontio.crypto.bip32.derivation;

public interface CkdFunction<KeyNode> {
    /**
     * Derives the child at the given index on the parent.
     *
     * @param parent     The parent to find the child of
     * @param childIndex The index of the child
     * @return the {@link KeyNode} for the child
     */
    KeyNode deriveChildKey(final KeyNode parent, final int childIndex);

}
