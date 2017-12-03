package com.sproutigy.commons.binary.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;

public class ReadableByteChannelInputStream extends InputStream {
    private ReadableByteChannel channel;
    private ByteBuffer singleByteBuffer = ByteBuffer.allocate(1);
    private long mark = 0;

    public ReadableByteChannelInputStream(ReadableByteChannel channel) {
        if (channel == null) throw new NullPointerException("channel == null");
        this.channel = channel;
    }

    public ReadableByteChannel getChannel() {
        return channel;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (channel == null) {
            throw new IllegalStateException("Closed");
        }

        return channel.read(ByteBuffer.wrap(b, off, len));
    }

    @Override
    public int read() throws IOException {
        singleByteBuffer.rewind();

        int ret;
        do {
            ret = channel.read(singleByteBuffer);
            if (ret < 0) {
                return ret;
            }
        } while (ret == 0);

        return singleByteBuffer.get(0);
    }

    @Override
    public void mark(int readlimit) {
        if (markSupported()) {
            try {
                mark = ((SeekableByteChannel)channel).position();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean markSupported() {
        return channel instanceof SeekableByteChannel;
    }

    @Override
    public void reset() throws IOException {
        if (markSupported()) {
            ((SeekableByteChannel)channel).position(mark);
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
