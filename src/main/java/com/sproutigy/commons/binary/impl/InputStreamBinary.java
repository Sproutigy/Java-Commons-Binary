package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamBinary extends Binary {
    protected InputStream stream;
    protected Binary buffered;

    public InputStreamBinary(InputStream stream) {
        this(stream, LENGTH_UNSPECIFIED);
    }

    public InputStreamBinary(InputStream stream, long length) {
        super(length);
        this.stream = stream;
    }

    public InputStream getStream() {
        return stream;
    }

    @Override
    public boolean isConsumable() {
        return true;
    }

    @Override
    public boolean isEmpty() throws IOException {
        if (stream.available() > 0) {
            return false;
        }

        if (stream.markSupported()) {
            stream.mark(2);
            int r = stream.read();
            if (r == EOF) {
                return true;
            }
            stream.reset();
            return false;
        }

        return super.isEmpty();
    }

    @Override
    protected long provideLength() throws IOException {
        buffered = clone();
        return buffered.length();
    }

    @Override
    public byte[] asByteArray(boolean modifiable) throws IOException {
        if (buffered != null) {
            return buffered.asByteArray(modifiable);
        }
        return readBytesFromStream(stream, length);
    }

    @Override
    public InputStream asStream() throws IOException {
        if (buffered != null) {
            return buffered.asStream();
        }
        return stream;
    }

    @Override
    public void close() throws IOException {
        if (buffered != null) {
            buffered.close();
            buffered = null;
        }
        stream.close();
        super.close();
    }
}
