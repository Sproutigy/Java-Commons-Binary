package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;
import com.sproutigy.commons.binary.BinaryException;
import com.sproutigy.commons.binary.adapters.ReadableByteChannelInputStream;

import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ReadableByteChannelBinary extends Binary {
    protected ReadableByteChannel channel;
    protected Binary buffered;

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
    public byte[] asByteArray(boolean modifiable) throws BinaryException {
        if (buffered != null) {
            return buffered.asByteArray(modifiable);
        }
        return readBytesFromStream(asStream());
    }

    @Override
    public InputStream asStream() throws BinaryException {
        if (buffered != null) {
            return buffered.asStream();
        }
        return new ReadableByteChannelInputStream(channel);
    }

    @Override
    public void toChannel(WritableByteChannel channel) throws BinaryException {
        try {
            if (buffered == null && this.channel instanceof FileChannel) {
                FileChannel sourceChannel = (FileChannel) this.channel;
                sourceChannel.transferTo(sourceChannel.position(), sourceChannel.size() - sourceChannel.position(), channel);
                return;
            }
        } catch (Exception e) {
            throw new BinaryException(e);
        }

        super.toChannel(channel);
    }

    @Override
    public boolean hasLength() throws BinaryException {
        return channel instanceof SeekableByteChannel || super.hasLength();
    }

    @Override
    protected long provideLength() throws BinaryException {
        if (channel instanceof SeekableByteChannel) {
            try {
                return ((SeekableByteChannel) channel).size() - ((SeekableByteChannel) channel).position();
            } catch (Exception e) {
                throw new BinaryException(e);
            }
        }

        buffered = clone();
        return buffered.length();
    }
}
