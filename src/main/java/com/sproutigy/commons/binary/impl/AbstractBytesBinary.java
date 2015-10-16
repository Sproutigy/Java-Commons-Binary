package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author LukeAheadNET
 */
public abstract class AbstractBytesBinary extends Binary {

    public AbstractBytesBinary() {
    }

    public AbstractBytesBinary(long length) {
        super(length);
    }

    @Override
    protected long provideLength() throws IOException {
        return asByteArray(false).length;
    }

    @Override
    public InputStream asStream() throws IOException {
        return new ByteArrayInputStream(asByteArray(false));
    }

}
