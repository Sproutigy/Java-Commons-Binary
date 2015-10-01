package com.sproutigy.commons.rawdata.impl;

import com.sproutigy.commons.rawdata.RawData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author LukeAheadNET
 */
public abstract class AbstractBytesRawData extends RawData {

    public AbstractBytesRawData() {
    }

    public AbstractBytesRawData(long length) {
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
