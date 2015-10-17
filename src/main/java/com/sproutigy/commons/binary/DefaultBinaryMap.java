package com.sproutigy.commons.binary;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default implementation of BinaryMap.
 *
 * @author LukeAheadNET
 */
public class DefaultBinaryMap extends LinkedHashMap<Binary, Binary> implements BinaryMap {

    public DefaultBinaryMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public DefaultBinaryMap(int initialCapacity) {
        super(initialCapacity);
    }

    public DefaultBinaryMap() {
    }

    public DefaultBinaryMap(Map<? extends Binary, ? extends Binary> m) {
        super(m);
    }

    public DefaultBinaryMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public DefaultBinaryMap(Binary key, Binary value) {
        this(1, 1);
        put(key, value);
    }

    public DefaultBinaryMap(Binary... allKeysAndValues) {
        this(allKeysAndValues.length/2, 1);

        if (allKeysAndValues.length%2 != 0) {
            throw new IllegalArgumentException("Constructor arguments with keys and values should be even");
        }

        for(int i=0; i<allKeysAndValues.length; i+=2) {
            put(allKeysAndValues[i], allKeysAndValues[i+1]);
        }
    }

}
