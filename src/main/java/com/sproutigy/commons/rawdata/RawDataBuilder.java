package com.sproutigy.commons.rawdata;

import com.sproutigy.commons.rawdata.impl.TempFileRawData;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author LukeAhead.net
 */
public class RawDataBuilder extends OutputStream {

    public static final int DEFAULT_EXPECTED_SIZE = 1024;
    public static final int DEFAULT_MAX_MEMORY_SIZE_BYTES = 50*1024;
    public static final int DEFAULT_MAX_SIZE_BYTES_LIMIT = Integer.MAX_VALUE;

    public RawDataBuilder() {
        this(DEFAULT_EXPECTED_SIZE, DEFAULT_MAX_MEMORY_SIZE_BYTES, DEFAULT_MAX_SIZE_BYTES_LIMIT);
    }

    public RawDataBuilder(int expectedSize, int maxMemorySizeBytes, int maxSizeBytesLimit) {
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
    private RawData data = null;

    public int length() {
        return length;
    }

    public RawDataBuilder append(RawData data) throws IOException {
        return append(data.asStream());
    }

    public RawDataBuilder append(String string, String charsetName) {
        return append(string.getBytes(Charset.forName(charsetName)));
    }

    public RawDataBuilder appendASCII(String string) {
        return append(string, "ASCII");
    }

    public RawDataBuilder appendUTF8(String string) {
        return append(string, "UTF-8");
    }

    public RawDataBuilder appendUTF16(String string) {
        return append(string, "UTF-16");
    }

    public RawDataBuilder appendUTF32(String string) {
        return append(string, "UTF-32");
    }

    public RawDataBuilder append(byte[] bytes) {
        return append(bytes, 0, bytes.length);
    }

    public RawDataBuilder append(byte[] bytes, int offset, int length) {
        try {
            prepareAppend(length);
            out.write(bytes, offset, length);
            this.length += length;
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RawDataBuilder append(byte b) {
        try {
            prepareAppend(1);
            out.write(b);
            this.length += 1;
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RawDataBuilder append(ByteBuffer byteBuffer) {
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

    public RawDataBuilder append(InputStream inputStream) throws IOException {
        int b;
        while( (b = inputStream.read()) != RawData.EOF) {
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
        File file = File.createTempFile(UUID.randomUUID().toString(), ".rawdata.tmp");
        file.deleteOnExit();
        filePath = file.getPath();
        if (out != null) out.close();
        out = new FileOutputStream(file);
    }

    public RawData build() {
        if (data == null) {
            if (filePath != null) {
                data = new TempFileRawData(filePath, true, false);
            } else {
                byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
                data = RawData.fromByteArray(bytes);
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
