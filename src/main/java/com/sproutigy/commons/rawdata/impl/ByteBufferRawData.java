package com.sproutigy.commons.rawdata.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author LukeAheadNET
 */
public class ByteBufferRawData extends AbstractBytesRawData {

    private ByteBuffer byteBuffer;

    public ByteBufferRawData(ByteBuffer byteBuffer) {
        super(byteBuffer.limit());
        this.byteBuffer = byteBuffer;
    }

    public ByteBuffer getUnderlyingByteBuffer() {
        return byteBuffer;
    }

    @Override
    public byte[] asByteArray(boolean modifiable) throws IOException {
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
    public ByteBuffer asByteBuffer(boolean modifiable) throws IOException {
        if (!modifiable) return byteBuffer;
        return super.asByteBuffer(true);
    }

    @Override
    public InputStream asStream() throws IOException {
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
