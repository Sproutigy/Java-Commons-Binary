package com.sproutigy.commons.binary;

import com.sproutigy.commons.binary.impl.TempFileBinary;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author LukeAheadNET
 */
public class BinaryBuilder extends OutputStream {

    public static final int DEFAULT_EXPECTED_SIZE = 1024;
    public static final int DEFAULT_MAX_MEMORY_SIZE_BYTES = 50*1024;
    public static final int DEFAULT_MAX_SIZE_BYTES_LIMIT = Integer.MAX_VALUE;

    public BinaryBuilder() {
        this(DEFAULT_EXPECTED_SIZE, DEFAULT_MAX_MEMORY_SIZE_BYTES, DEFAULT_MAX_SIZE_BYTES_LIMIT);
    }

    public BinaryBuilder(int expectedSize, int maxMemorySizeBytes, int maxSizeBytesLimit) {
        if (expectedSize > maxMemorySizeBytes) {
            try {
                prepareTempFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            out = new ByteArrayOutputStream(expectedSize);
        }

        this.maxMemorySizeBytes = maxMemorySizeBytes;
        this.maxSizeBytesLimit = maxSizeBytesLimit;
    }

    private int length = 0;
    private int maxMemorySizeBytes;
    private int maxSizeBytesLimit;
    private String filePath;
    private OutputStream out;
    private Binary data = null;

    public int length() {
        return length;
    }

    public BinaryBuilder append(Binary data) throws IOException {
        return append(data.asStream());
    }

    public BinaryBuilder append(String string, String charsetName) {
        return append(string.getBytes(Charset.forName(charsetName)));
    }

    public BinaryBuilder appendASCII(String string) {
        return append(string, "ASCII");
    }

    public BinaryBuilder appendUTF8(String string) {
        return append(string, "UTF-8");
    }

    public BinaryBuilder appendUTF16(String string) {
        return append(string, "UTF-16");
    }

    public BinaryBuilder appendUTF32(String string) {
        return append(string, "UTF-32");
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
        int b;
        while( (b = inputStream.read()) != Binary.EOF) {
            append((byte)b);
        }
        return this;
    }

    @Override
    public void write(int b) throws IOException {
        append((byte)b);
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
            throw new IOException("Limit exceeded");
        }
    }

    private void prepareTempFile() throws IOException {
        File file = File.createTempFile(UUID.randomUUID().toString(), ".binary.tmp");
        file.deleteOnExit();
        filePath = file.getPath();
        if (out != null) out.close();
        out = new FileOutputStream(file);
    }

    public Binary build() {
        if (data == null) {
            if (filePath != null) {
                data = new TempFileBinary(filePath, true, false);
            } else {
                byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
                data = Binary.fromByteArray(bytes);
            }
        }

        try {
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException ignore) { }

        return data;
    }
}
