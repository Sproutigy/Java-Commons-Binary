package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.BinaryException;

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
    public byte[] asByteArray(boolean modifiable) throws BinaryException {
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
            byteBuffer.flip();
            byteBuffer.get(bytes);
            return bytes;
        }
    }

    @Override
    public ByteBuffer asByteBuffer(boolean modifiable) throws BinaryException {
        if (!modifiable) return byteBuffer;
        return super.asByteBuffer(true);
    }

    @Override
    public InputStream asStream() throws BinaryException {
        return new InputStream() {
            private int position = 0;

            @Override
            public int read() throws BinaryException {
                if (position >= byteBuffer.limit()) {
                    return EOF;
                }

                return byteBuffer.get(position++);
            }
        };
    }
}
