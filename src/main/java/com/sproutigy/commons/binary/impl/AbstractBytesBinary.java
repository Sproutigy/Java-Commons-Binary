package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;
import com.sproutigy.commons.binary.BinaryException;

import java.io.ByteArrayInputStream;
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
    protected long provideLength() throws BinaryException {
        return asByteArray(false).length;
    }

    @Override
    public InputStream asStream() throws BinaryException {
        return new ByteArrayInputStream(asByteArray(false));
    }

    @Override
    public String toString() {
        if (hasCharset()) {
            return asString(getCharset());
        } else {
            if (!hasLength()) {
                return "(bytes)";
            } else {
                return "(bytes[" + length() + "])";
            }
        }
    }
}
