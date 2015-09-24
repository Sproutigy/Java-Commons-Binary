package com.sproutigy.commons.rawdata.impl;

import com.sproutigy.commons.rawdata.RawData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author LukeAhead.net
 */
public class ByteArrayRawData extends RawData {

    private byte[] bytes;
    private int offset;


    public ByteArrayRawData(byte[] bytes) {
        super(bytes.length);
        this.bytes = bytes;
        this.offset = 0;
    }

    public ByteArrayRawData(byte[] bytes, int offset, int length) {
        super(length);
        this.bytes = bytes;
        this.offset = offset;
    }

    public byte[] getUnderlyingByteArray() {
        return bytes;
    }

    public int getUnderlyingByteArrayOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ByteArrayRawData)) {
            return super.equals(other);
        }

        return Arrays.equals(asByteArray(false), ((ByteArrayRawData) other).asByteArray(false));
    }

    @Override
    public int hashCode() {
        if (bytes == null || bytes.length == 0)
            return 0;

        int result = 1;
        for (int i=offset; i<length; i++) {
            result = 31 * result + bytes[i];
        }

        return result;
    }

    @Override
    public byte[] asByteArray(boolean modifiable) {
        if (!modifiable) {
            if (offset == 0 && bytes.length == length)
                return bytes;
        }

        byte[] out = new byte[(int)length];
        System.arraycopy(bytes, offset, out, 0, (int)length);
        return out;
    }

    @Override
    public String asString(String charsetName) throws IOException {
        return new String(bytes, offset, (int)length, charsetName);
    }

    @Override
    public String asString(Charset charset) throws IOException {
        return new String(bytes, offset, (int)length, charset);
    }

    @Override
    public InputStream asStream() {
        return new ByteArrayInputStream(bytes, offset, (int)length);
    }

    @Override
    public RawData subdata(long offset, long length) throws IOException {
        if (offset > Integer.MAX_VALUE || length > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("Offset and/or length higher than Integer.MAX_VALUE");
        }
        if (this.length >= 0 && length >= 0 && (length-offset) > this.length) {
            throw new IndexOutOfBoundsException("Outside data scope");
        }

        int newLength = length >= 0 ? (int)length : this.bytes.length-(int)(this.offset+offset);
        return new ByteArrayRawData(this.bytes, (int)(this.offset + offset), newLength);
    }

    @Override
    public void close() throws Exception {
        bytes = null;
        offset = 0;
        length = 0;
    }

    @Override
    public String toString() {
        return "bytes["+length+"]";
    }
}