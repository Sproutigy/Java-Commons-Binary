package com.sproutigy.commons.rawdata.impl;

import com.sproutigy.commons.rawdata.RawData;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author LukeAhead.net
 */
public abstract class AbstractStreamableRawData extends RawData {

    public AbstractStreamableRawData() {
    }

    public AbstractStreamableRawData(long length) {
        super(length);
    }

    @Override
    public byte[] asByteArray(boolean modifiable) throws IOException {
        long length = this.length;

        try(InputStream in = asStream()) {
            return readBytesFromStream(in, length);
        }
    }

    @Override
    public abstract InputStream asStream() throws IOException;

}
