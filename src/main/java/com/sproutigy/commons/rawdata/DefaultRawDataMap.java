package com.sproutigy.commons.rawdata;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LukeAhead.net
 */
public class DefaultRawDataMap extends LinkedHashMap<RawData, RawData> implements RawDataMap {

    public DefaultRawDataMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public DefaultRawDataMap(int initialCapacity) {
        super(initialCapacity);
    }

    public DefaultRawDataMap() {
    }

    public DefaultRawDataMap(Map<? extends RawData, ? extends RawData> m) {
        super(m);
    }

    public DefaultRawDataMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public DefaultRawDataMap(RawData key, RawData value) {
        this(1, 1);
        put(key, value);
    }

    public DefaultRawDataMap(RawData... allKeysAndValues) {
        this(allKeysAndValues.length/2, 1);

        if (allKeysAndValues.length%2 != 0) {
            throw new IllegalArgumentException("Constructor arguments with keys and values should be even");
        }

        for(int i=0; i<allKeysAndValues.length; i+=2) {
            put(allKeysAndValues[i], allKeysAndValues[i+1]);
        }
    }

}
