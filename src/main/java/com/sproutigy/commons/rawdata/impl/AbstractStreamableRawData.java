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

    @Override
    public RawData subrange(long offset, long length) throws IOException {
        try(InputStream stream = asStream()) {
            long skipped = stream.skip(offset);
            if (skipped < offset) {
                throw new IndexOutOfBoundsException("Out of data range");
            }

            return RawData.fromByteArray(readBytesFromStream(stream, length));
        }
    }

}
