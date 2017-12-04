package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;
import com.sproutigy.commons.binary.BinaryException;

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
    protected long provideLength() throws BinaryException {
        buffered = clone();
        return buffered.length();
    }

    @Override
    public byte[] asByteArray(boolean modifiable) throws BinaryException {
        if (buffered != null) {
            return buffered.asByteArray(modifiable);
        }
        return readBytesFromStream(stream, length);
    }

    @Override
    public InputStream asStream() throws BinaryException {
        if (buffered != null) {
            return buffered.asStream();
        }
        return stream;
    }

    @Override
    public void close() throws BinaryException {
        buffered = null;
        try {
            stream.close();
        } catch (IOException e) {
            throw new BinaryException(e);
        }
        super.close();
    }
}
