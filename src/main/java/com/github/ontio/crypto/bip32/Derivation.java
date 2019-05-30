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