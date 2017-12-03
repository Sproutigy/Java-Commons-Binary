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

    public BinaryBuilder() throws BinaryException {
        this(DEFAULT_EXPECTED_SIZE, DEFAULT_MAX_MEMORY_SIZE_BYTES, DEFAULT_MAX_SIZE_BYTES_LIMIT);
    }

    public BinaryBuilder(long expectedSize) throws BinaryException {
        this(expectedSize, DEFAULT_MAX_MEMORY_SIZE_BYTES, DEFAULT_MAX_SIZE_BYTES_LIMIT);
    }

    public BinaryBuilder(long expectedSize, int maxMemorySizeBytes) throws BinaryException {
        this(expectedSize, maxMemorySizeBytes, Long.MAX_VALUE);
    }

    public BinaryBuilder(long expectedSize, int maxMemorySizeBytes, long maxSizeBytesLimit) throws BinaryException {
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

    public BinaryBuilder append(Binary data) throws BinaryException {
        return append(data.asStream());
    }

    public BinaryBuilder append(String string) {
        if (charset == null) {
            charset = Charsets.UTF_8;
        }
        return append(string, charset);
    }

    public BinaryBuilder append(String string, String charsetName) throws BinaryException {
        return append(string.getBytes(Charset.forName(charsetName)));
    }

    public BinaryBuilder append(String string, Charset charset) {
        return append(string.getBytes(charset));
    }

    public BinaryBuilder appendASCII(String string) throws BinaryException {
        return append(string, Charsets.US_ASCII);
    }

    public BinaryBuilder appendISO(String string) {
        return append(string, Charsets.ISO_8859_1);
    }

    public BinaryBuilder appendUTF8(String string) throws BinaryException {
        return append(string, Charsets.UTF_8);
    }

    public BinaryBuilder append(byte[] bytes) throws BinaryException {
        return append(bytes, 0, bytes.length);
    }

    public BinaryBuilder append(byte[] bytes, int offset, int length) throws BinaryException {
        try {
            prepareAppend(length);
            out.write(bytes, offset, length);
            this.length += length;
            return this;
        } catch (IOException e) {
            throw new BinaryException(e);
        }
    }

    public BinaryBuilder append(byte b) throws BinaryException {
        try {
            prepareAppend(1);
            out.write(b);
            this.length += 1;
            return this;
        } catch (IOException e) {
            throw new BinaryException(e);
        }
    }

    public BinaryBuilder append(ByteBuffer byteBuffer) throws BinaryException {
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

    public BinaryBuilder append(InputStream inputStream) throws BinaryException {
        try {
            byte[] buffer = new byte[4*1024];
            int readlen;
            while ((readlen = inputStream.read(buffer)) != Binary.EOF) {
                append(buffer, 0, readlen);
            }
        } catch (IOException e) {
            throw new BinaryException(e);
        }
        return this;
    }

    public boolean append(InputStream inputStream, long maxLength) throws BinaryException {
        long remaining = maxLength;
        int b;
        try {
            while( true ) {
                if (remaining <= 0)
                    return false;
                b = inputStream.read();
                if (b == Binary.EOF)
                    return true;
                append((byte)b);
                remaining--;
            }
        } catch (IOException e) {
            throw new BinaryException(e);
        }
    }

    @Override
    public void write(int b) throws BinaryException {
        append((byte)b);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        byte[] data = new ByteBufferBinary(src).asByteArray(false);
        write(data);
        return data.length;
    }

    private void prepareAppend(int appendSize) throws BinaryException {
        if (data != null)
            throw new IllegalStateException("Data already built");

        if (filePath == null && length+appendSize > maxMemorySizeBytes) {
            byte[] current = ((ByteArrayOutputStream)out).toByteArray();
            prepareTempFile();
            try {
                out.write(current);
            } catch (IOException e) {
                throw new BinaryException(e);
            }
        }
        if (length+appendSize > maxSizeBytesLimit) {
            throw new BinaryException("Limit exceeded");
        }
    }

    private void prepareTempFile() throws BinaryException {
        try {
            File file = File.createTempFile(UUID.randomUUID().toString(), ".binary.tmp");
            file.deleteOnExit();
            filePath = file.getPath();
            if (out != null) out.close();
            out = new FileOutputStream(file);
        } catch(IOException e) {
            throw new BinaryException(e);
        }
    }

    public BinaryBuilder charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public Binary build() throws BinaryException {
        if (data == null) {
            if (filePath != null) {
                data = new TempFileBinary(filePath, true, false);
            } else if (out != null) {
                byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
                data = Binary.from(bytes);
            }
        }

        if (charset != null) {
            if (data == null) {
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
            throw new BinaryException(e);
        }

        return data;
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
