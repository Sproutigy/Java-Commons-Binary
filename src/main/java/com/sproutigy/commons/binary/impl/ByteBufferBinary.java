package com.sproutigy.commons.binary.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author LukeAheadNET
 */
public class ByteBufferBinary extends AbstractBytesBinary {

    private ByteBuffer byteBuffer;

    public ByteBufferBinary(ByteBuffer byteBuffer) {
        super(byteBuffer.limit());
        this.byteBuffer = byteBuffer;
    }

    public ByteBuffer getUnderlyingByteBuffer() {
        return byteBuffer;
    }

    @Override
    public boolean isConsumable() {
        return false;
    }

    @Override
    public byte[] asByteArray(boolean modifiable) {
        if (byteBuffer.hasArray()) {
            if (!modifiable) {
                if (byteBuffer.arrayOffset() == 0 && byteBuffer.limit() == byteBuffer.array().length) {
                    return byteBuffer.array();
                }
            }

            byte[] bytes = new byte[byteBuffer.limit()];
            System.arraycopy(byteBuffer.array(), byteBuffer.arrayOffset(), bytes, 0, byteBuffer.limit());
            return bytes;
        } else {
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.rewind();
            byteBuffer.get(bytes);
            return bytes;
        }
    }

    @Override
    public ByteBuffer asByteBuffer(boolean modifiable) {
        if (!modifiable) return byteBuffer;
        return super.asByteBuffer(true);
    }

    @Override
    public InputStream asStream() {
        return new InputStream() {
            private int position = 0;

            @Override
            public int read() throws IOException {
                if (position >= byteBuffer.limit()) {
                    return EOF;
                }

                return byteBuffer.get(position++);
            }
        };
    }
}
