package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.*;

public class ReadableByteChannelBinary extends Binary {
    protected ReadableByteChannel channel;
    protected Binary buffered;
    protected Long offset;

    public ReadableByteChannelBinary(ReadableByteChannel channel) {
        this(channel, LENGTH_UNSPECIFIED);
    }

    public ReadableByteChannelBinary(ReadableByteChannel channel, long length) {
        super(length);
        this.channel = channel;
    }

    public ReadableByteChannel getChannel() {
        return channel;
    }

    @Override
    public byte[] asByteArray(boolean modifiable) throws IOException {
        if (buffered != null) {
            return buffered.asByteArray(modifiable);
        }
        return readBytesFromStream(asStream());
    }

    @Override
    public InputStream asStream() throws IOException {
        if (buffered != null) {
            return buffered.asStream();
        }

        if (channel instanceof SeekableByteChannel) {
            if (this.offset == null) {
                this.offset = ((SeekableByteChannel) channel).position();
            } else {
                ((SeekableByteChannel) channel).position(offset);
            }
        }

        return Channels.newInputStream(channel);
    }

    @Override
    public void to(WritableByteChannel channel) throws IOException {
        if (buffered == null && this.channel instanceof FileChannel) {
            FileChannel sourceChannel = (FileChannel) this.channel;
            sourceChannel.transferTo(sourceChannel.position(), sourceChannel.size() - sourceChannel.position(), channel);
            return;
        }

        super.to(channel);
    }

    @Override
    public void close() throws IOException {
        channel.close();
        super.close();
    }

    @Override
    public boolean isConsumable() {
        return !(channel instanceof SeekableByteChannel);
    }

    @Override
    public boolean hasLength() throws IOException {
        return channel instanceof SeekableByteChannel || super.hasLength();
    }

    @Override
    protected long provideLength() throws IOException {
        if (channel instanceof SeekableByteChannel) {
            return ((SeekableByteChannel) channel).size() - ((SeekableByteChannel) channel).position();
        }

        buffered = clone();
        return buffered.length();
    }
}
