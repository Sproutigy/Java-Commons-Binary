package com.sproutigy.commons.binary.adapters;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class WritableByteChannelOutputStream extends OutputStream {
    private WritableByteChannel channel;
    private ByteBuffer singleByteBuffer = ByteBuffer.allocate(1);

    public WritableByteChannelOutputStream(WritableByteChannel channel) {
        if (channel == null) throw new NullPointerException("channel == null");
        this.channel = channel;
    }

    public WritableByteChannel getChannel() {
        return channel;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (channel == null) {
            throw new IllegalStateException("Closed");
        }

        channel.write(ByteBuffer.wrap(b, off, len));
    }

    @Override
    public void write(int b) throws IOException {
        singleByteBuffer.rewind();
        singleByteBuffer.put((byte)b);
        channel.write(singleByteBuffer);
    }

    @Override
    public void flush() throws IOException {
        if (channel instanceof FileChannel) {
            ((FileChannel) channel).force(true);
        }
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
            channel = null;
        }
    }
}
