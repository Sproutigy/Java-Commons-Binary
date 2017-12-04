package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author LukeAheadNET
 */
public abstract class AbstractStreamableBinary extends Binary {

    public AbstractStreamableBinary() {
    }

    public AbstractStreamableBinary(long length) {
        super(length);
    }

    @Override
    public byte[] asByteArray(boolean modifiable) throws IOException {
        long length = this.length;

        InputStream in = asStream();
        try {
            return readBytesFromStream(in, length);
        } finally {
            in.close();
        }
    }

    @Override
    public abstract InputStream asStream() throws IOException;

    @Override
    public Binary subrange(long offset, long length) throws IOException {
        InputStream stream = asStream();
        try {
            long skipped = stream.skip(offset);
            if (skipped < offset) {
                throw new IndexOutOfBoundsException("Out of data range");
            }

            return Binary.from(readBytesFromStream(stream, length));
        } finally {
            stream.close();
        }
    }

}
