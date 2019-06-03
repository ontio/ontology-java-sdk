/*
 *  BIP32 library, a Java implementation of BIP32
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
 *  Original source: https://github.com/NovaCrypto/BIP32
 *  You can contact the authors via github issues.
 */

package com.github.ontio.crypto.bip32.derivation;

public enum CharSequenceDerivation implements Derivation<CharSequence> {
    INSTANCE;

    public static int hard(final int index) {
        return index | 0x80000000;
    }

    public static boolean isHardened(final int i) {
        return (i & 0x80000000) != 0;
    }

    @Override
    public <T> T derive(final T rootKey, final CharSequence derivationPath, final CkdFunction<T> ckdFunction) {
        final int length = derivationPath.length();
        T currentKey = rootKey;
        int buffer = 0;
        for (int i = 0; i < length; i++) {
            final char c = derivationPath.charAt(i);
            switch (c) {
                case '\'':
                    buffer = hard(buffer);
                    break;
                case '/':
                    currentKey = ckdFunction.deriveChildKey(currentKey, buffer);
                    buffer = 0;
                    break;
                default:
                    buffer *= 10;
                    if (c < '0' || c > '9')
                        throw new IllegalArgumentException("Illegal character in path: " + c);
                    buffer += c - '0';
                    if (isHardened(buffer))
                        throw new IllegalArgumentException("Index number too large");
            }
        }
        return ckdFunction.deriveChildKey(currentKey, buffer);
    }
}