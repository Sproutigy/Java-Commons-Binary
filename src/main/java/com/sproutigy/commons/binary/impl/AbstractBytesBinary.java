package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.AbstractUncheckedBinary;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author LukeAheadNET
 */
public abstract class AbstractBytesBinary extends AbstractUncheckedBinary {

    public AbstractBytesBinary() {
    }

    public AbstractBytesBinary(long length) {
        super(length);
    }

    @Override
    protected long provideLength() {
        return asByteArray(false).length;
    }

    @Override
    public InputStream asStream() {
        return new ByteArrayInputStream(asByteArray(false));
    }

    @Override
    public String toString() {
        try {
            if (hasCharset()) {
                return asString(getCharset());
            } else {
                if (!hasLength()) {
                    return "(bytes)";
                } else {
                    return "(bytes[" + length() + "])";
                }
            }
        } catch (Exception e) {
            return super.toString();
        }
    }
}
