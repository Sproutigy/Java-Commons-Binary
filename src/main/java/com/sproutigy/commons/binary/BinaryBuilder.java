package com.sproutigy.commons.binary;

import com.sproutigy.commons.binary.impl.ByteBufferBinary;
import com.sproutigy.commons.binary.impl.TempFileBinary;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * BinaryBuilder allows to append any type of low-level data to finally build Binary.
 * Allows to append data progressively.
 * When data is rather small it is kept in memory.
 * To prevent OutOfMemoryException, when it reaches predefined limits,
 * its content is written to temporary file and all
 * further append requests are targeting there.
 * BinaryBuilder implements OutputStream, so can be used as a target stream.
 *
 * @author LukeAheadNET
 */
public class BinaryBuilder extends OutputStream implements WritableByteChannel {

    public static final long DEFAULT_EXPECTED_SIZE = 1024;
    public static final int DEFAULT_MAX_MEMORY_SIZE_BYTES = 100*1024;
    public static final long DEFAULT_MAX_SIZE_BYTES_LIMIT = Integer.MAX_VALUE;

    public BinaryBuilder() {
        this(DEFAULT_EXPECTED_SIZE, DEFAULT_MAX_MEMORY_SIZE_BYTES, DEFAULT_MAX_SIZE_BYTES_LIMIT);
    }

    public BinaryBuilder(long expectedSize) {
        this(expectedSize, DEFAULT_MAX_MEMORY_SIZE_BYTES, DEFAULT_MAX_SIZE_BYTES_LIMIT);
    }

    public BinaryBuilder(long expectedSize, int maxMemorySizeBytes) {
        this(expectedSize, maxMemorySizeBytes, Long.MAX_VALUE);
    }

    public BinaryBuilder(long expectedSize, int maxMemorySizeBytes, long maxSizeBytesLimit) {
        if (expectedSize > maxMemorySizeBytes || expectedSize > Integer.MAX_VALUE) {
            prepareTempFile();
        } else {
            out = new ByteArrayOutputStream((int)expectedSize);
        }

        this.maxMemorySizeBytes = maxMemorySizeBytes;
        this.maxSizeBytesLimit = maxSizeBytesLimit;
    }

    private Charset charset;
    private long length = 0;
    private int maxMemorySizeBytes;
    private long maxSizeBytesLimit;
    private String filePath;
    private OutputStream out;
    private Binary data = null;

    public long length() {
        return length;
    }

    public BinaryBuilder append(Binary data) throws IOException {
        return append(data.asStream());
    }

    public BinaryBuilder append(String string) {
        if (charset == null) {
            charset = Charsets.UTF_8;
        }
        return append(string, charset);
    }

    public BinaryBuilder append(String string, String charsetName) throws IOException {
        return append(string.getBytes(Charset.forName(charsetName)));
    }

    public BinaryBuilder append(String string, Charset charset) {
        return append(string.getBytes(charset));
    }

    public BinaryBuilder appendASCII(String string) {
        return append(string, Charsets.US_ASCII);
    }

    public BinaryBuilder appendUTF8(String string) {
        return append(string, Charsets.UTF_8);
    }

    public BinaryBuilder append(byte[] bytes) {
        return append(bytes, 0, bytes.length);
    }

    public BinaryBuilder append(byte[] bytes, int offset, int length) {
        try {
            prepareAppend(length);
            out.write(bytes, offset, length);
            this.length += length;
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BinaryBuilder append(byte b) {
        try {
            prepareAppend(1);
            out.write(b);
            this.length += 1;
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BinaryBuilder append(ByteBuffer byteBuffer) {
        if (byteBuffer.hasArray()) {
            append(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.limit());
        } else {
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.flip();
            byteBuffer.get(bytes);
            append(bytes);
        }
        return this;
    }

    public BinaryBuilder append(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4*1024];
        int readlen;
        while ((readlen = inputStream.read(buffer)) != Binary.EOF) {
            append(buffer, 0, readlen);
        }
        return this;
    }

    public boolean append(InputStream inputStream, long maxLength) throws IOException {
        long remaining = maxLength;
        int b;
        while( true ) {
            if (remaining <= 0)
                return false;
            b = inputStream.read();
            if (b == Binary.EOF)
                return true;
            append((byte)b);
            remaining--;
        }
    }

    @Override
    public void write(int b) {
        append((byte)b);
    }

    @Override
    public void write(byte b[]) {
        append(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        append(b, off, len);
    }

    @Override
    public int write(ByteBuffer src) {
        byte[] data = new ByteBufferBinary(src).asByteArray(false);
        write(data);
        return data.length;
    }

    private void prepareAppend(int appendSize) throws IOException {
        if (data != null)
            throw new IllegalStateException("Data already built");

        if (filePath == null && length+appendSize > maxMemorySizeBytes) {
            byte[] current = ((ByteArrayOutputStream)out).toByteArray();
            prepareTempFile();
            out.write(current);
        }
        if (length+appendSize > maxSizeBytesLimit) {
            throw new IllegalStateException("Limit exceeded");
        }
    }

    private void prepareTempFile() {
        try {
            File file = File.createTempFile(UUID.randomUUID().toString(), ".binary.tmp");
            file.deleteOnExit();
            filePath = file.getPath();
            if (out != null) out.close();
            out = new FileOutputStream(file);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BinaryBuilder charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public UncheckedBinary build() {
        if (data == null) {
            if (filePath != null) {
                data = new TempFileBinary(filePath, true, false);
            } else if (out != null) {
                byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
                data = Binary.from(bytes);
            }
        }

        if (charset != null) {
            if (data == null || length == 0) {
                data = Binary.empty(charset);
            }
            else {
                data.setCharset(charset);
            }
        } else if (data == null) {
            data = Binary.EMPTY;
        }

        try {
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new UncheckedBinary(data);
    }

    @Override
    public boolean isOpen() {
        return out != null;
    }

    @Override
    public void close() throws IOException {
        if (data != null) {
            data.close();
        }

        if (out != null) {
            out.close();
            out = null;
        }

        length = -1;
        data = null;
        filePath = null;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } catch(Throwable ignore) { }

        super.finalize();
    }
}
