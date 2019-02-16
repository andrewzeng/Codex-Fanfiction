package com.qan.fiction.util.storage;

import java.io.Serializable;

public class SerPair<A extends Serializable, B extends Serializable> implements Serializable {
    public final A first;
    public final B second;

    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public SerPair(A first, B second) {

        this.first = first;
        this.second = second;
    }
}
