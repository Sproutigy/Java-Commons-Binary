package com.sproutigy.commons.rawdata;

import com.sproutigy.commons.rawdata.impl.ByteArrayRawData;
import com.sproutigy.commons.rawdata.impl.ByteBufferRawData;
import com.sproutigy.commons.rawdata.impl.FileRawData;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.UUID;

/**
 * @author LukeAhead.net
 */
public abstract class RawData implements AutoCloseable, Comparable<RawData>, Cloneable {

    public static final long LENGTH_UNSPECIFIED = -1;

    protected static final int EOF = -1;

    protected static final int BITS_PER_BYTE = 8;


    protected long length = LENGTH_UNSPECIFIED;


    protected RawData() { }

    protected RawData(long length) {
        this.length = length;
    }


    public boolean hasLength() throws IOException {
        return length != LENGTH_UNSPECIFIED;
    }

    public final long length() throws IOException {
        return length(true);
    }

    public final long length(boolean forceCalculate) throws IOException {
        if (length == LENGTH_UNSPECIFIED) {
            if (forceCalculate) {
                length = provideLength();
            }
        }
        return length;
    }

    protected long provideLength() throws IOException {
        int counter = 0;
        try(InputStream in = asStream()) {
            while (in.read() != EOF) {
                counter++;
            }
        }
        return counter;
    }

    public final byte[] asByteArray() throws IOException {
        return asByteArray(true);
    }

    public abstract byte[] asByteArray(boolean modifiable) throws IOException;

    public abstract InputStream asStream() throws IOException;

    public ByteBuffer asByteBuffer() throws IOException {
        return asByteBuffer(true);
    }

    public ByteBuffer asByteBuffer(boolean modifiable) throws IOException {
        return (ByteBuffer)ByteBuffer.wrap(asByteArray(modifiable)).position(0);
    }

    public String asStringASCII() throws IOException {
        return asString("US-ASCII");
    }

    public String asStringUTF8() throws IOException {
        return asString("UTF-8");
    }

    public String asStringUTF16() throws IOException {
        return asString("UTF-16");
    }

    public String asStringUTF32() throws IOException {
        return asString("UTF-32");
    }

    public String asString(String charsetName) throws IOException {
        return new String(asByteArray(false), charsetName);
    }

    public String asString(Charset charset) throws IOException {
        return new String(asByteArray(false), charset);
    }

    public byte asByte() throws IOException {
        return asByteArray(false)[0];
    }

    public short asShort() throws IOException {
        return ((ByteBuffer)ByteBuffer.allocate(Short.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getShort();
    }

    public int asInteger() throws IOException {
        return ((ByteBuffer)ByteBuffer.allocate(Integer.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getInt();
    }

    public long asLong() throws IOException {
        return ((ByteBuffer)ByteBuffer.allocate(Long.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getLong();
    }

    public float asFloat() throws IOException {
        return ((ByteBuffer)ByteBuffer.allocate(Float.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getFloat();
    }

    public double asDouble() throws IOException {
        return ((ByteBuffer)ByteBuffer.allocate(Double.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getDouble();
    }

    public char asCharacter() throws IOException {
        return ((ByteBuffer)ByteBuffer.allocate(Character.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getChar();
    }

    public String toTempFile(boolean modifiable) throws IOException {
        File file = File.createTempFile(UUID.randomUUID().toString(), ".rawdata.tmp");
        file.deleteOnExit();
        toFile(file);
        return file.getAbsolutePath();
    }

    public void toFile(String path) throws IOException {
        toFile(new File(path));
    }

    public void toFile(File file) throws IOException {
        try(OutputStream out = new FileOutputStream(file)) {
            toStream(out);
        }
    }

    public void toFile(Path path) throws IOException {
        toFile(path.toFile());
    }

    public void toStream(OutputStream out) throws IOException {
        byte[] buffer;
        if (hasLength() && length() < 4096) {
            buffer = new byte[(int)length()];
        } else {
            buffer = new byte[4096];
        }

        try(InputStream in = asStream()) {
            int len;
            while ((len = in.read(buffer)) != EOF) {
                out.write(buffer, 0, len);
            }
        }
    }

    public void toByteArray(byte[] target) throws IOException {
        toByteArray(target, 0);
    }

    public void toByteArray(byte[] target, int targetOffset) throws IOException {
        long length = length();
        if ((long)targetOffset + length > Integer.MAX_VALUE) {
            throw new IOException("Unable to write - too big data");
        }
        if (target.length < targetOffset + length) {
            throw new IOException("Insufficient target byte array size");
        }

        if (length < 0) {
            int curOffset = targetOffset;
            try(InputStream in = asStream()) {
                int readbyte;
                while ((readbyte = in.read()) != EOF) {
                    target[curOffset] = (byte)readbyte;
                    curOffset++;
                }
            }
        }
        else
        {
            System.arraycopy(asByteArray(false), 0, target, targetOffset, (int)length);
        }
    }

    public RawData subdata(long offset) throws IOException {
        return subdata(offset, LENGTH_UNSPECIFIED);
    }

    public RawData subdata(long offset, long length) throws IOException {
        if (offset > Integer.MAX_VALUE || length > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("Offset and/or length higher than Integer.MAX_VALUE");
        }
        long curLength = length();
        if (curLength >= 0 && (length-offset) > curLength) {
            throw new IndexOutOfBoundsException("Outside data scope");
        }

        byte[] bytes = asByteArray(false);
        int newLength = length > 0 ? (int)length : (int)(bytes.length-offset);
        return new ByteArrayRawData(bytes, (int)offset, newLength);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RawData)) return false;
        return compareTo((RawData)other) == 0;
    }

    @Override
    public int hashCode() {
        int result = 1;

        try {
            try(InputStream in = asStream()) {
                int readbyte;
                while ((readbyte = in.read()) != EOF) {
                    result = 31 * result + readbyte;
                }
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        
        return result;
    }

    @Override
    public int compareTo(RawData other) {
        if (other == null) return 1;
        if (this == other) return 0;

        try {
            try(InputStream thisStream = asStream()) {
                try(InputStream otherStream = other.asStream()) {
                    while(true) {
                        int thisReadByte = thisStream.read();
                        int otherReadByte = otherStream.read();

                        if (thisReadByte == otherReadByte) continue;
                        if (thisReadByte == EOF) return -1;
                        if (otherReadByte == EOF) return 1;
                        return thisReadByte - otherReadByte;
                    }
                }
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {

    }

    protected static byte[] readBytesFromStream(InputStream in) throws IOException {
        return readBytesFromStream(in, LENGTH_UNSPECIFIED);
    }

    protected static byte[] readBytesFromStream(InputStream in, long length) throws IOException {
        if (length == 0)
            return EMPTY_BYTE_ARRAY;

        if (length > Integer.MAX_VALUE)
            throw new IOException("Stream is longer than maximal byte array size");

        if (length <= 0) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readlen;
            while ((readlen = in.read(buffer)) != EOF) {
                out.write(buffer, 0, readlen);
            }
            out.flush();
            return out.toByteArray();
        } else {
            int offset = 0;
            byte[] bytes = new byte[(int)length];
            int lenToRead;
            int readlen;
            while(true) {
                lenToRead = bytes.length-offset;
                if (lenToRead == 0) break;
                readlen = in.read(bytes,offset,lenToRead);
                if (readlen == EOF || readlen == lenToRead) break;
                offset += readlen;
            }
            return bytes;
        }
    }

    public static RawData fromStream(InputStream in) throws IOException {
        return fromByteArray(readBytesFromStream(in));
    }

    public static RawData fromStream(InputStream in, long length) throws IOException {
        return fromByteArray(readBytesFromStream(in, length));
    }

    public static RawData fromByte(byte value) {
        return fromByteArray(new byte[]{value});
    }

    public static RawData fromShort(short value) {
        return fromByteBuffer(ByteBuffer.allocate(Short.SIZE / BITS_PER_BYTE).putShort(value));
    }

    public static RawData fromInteger(int value) {
        return fromByteBuffer(ByteBuffer.allocate(Integer.SIZE / BITS_PER_BYTE).putInt(value));
    }

    public static RawData fromLong(long value) {
        return fromByteBuffer(ByteBuffer.allocate(Long.SIZE / BITS_PER_BYTE).putLong(value));
    }

    public static RawData fromFloat(float value) {
        return fromByteBuffer(ByteBuffer.allocate(Float.SIZE / BITS_PER_BYTE).putFloat(value));
    }

    public static RawData fromDouble(double value) {
        return fromByteBuffer(ByteBuffer.allocate(Double.SIZE / BITS_PER_BYTE).putDouble(value));
    }

    public static RawData fromCharacter(char value) {
        return fromByteBuffer(ByteBuffer.allocate(Character.SIZE / BITS_PER_BYTE).putChar(value));
    }

    public static RawData fromByteArray(byte[] bytes) {
        return new ByteArrayRawData(bytes);
    }

    public static RawData fromByteArray(byte[] bytes, int offset, int length) {
        return new ByteArrayRawData(bytes, offset, length);
    }

    public static RawData fromByteBuffer(ByteBuffer byteBuffer) {
        return new ByteBufferRawData(byteBuffer);
    }

    public static RawData fromStringASCII(String s) {
        return fromString(s, "US-ASCII");
    }

    public static RawData fromStringUTF8(String s) {
        return fromString(s, "UTF-8");
    }

    public static RawData fromStringUTF16(String s) {
        return fromString(s, "UTF-16");
    }

    public static RawData fromStringUTF32(String s) {
        return fromString(s, "UTF-32");
    }

    public static RawData fromString(String s, Charset charset) {
        return new ByteArrayRawData(s.getBytes(charset));
    }

    public static RawData fromString(String s, String charsetName) {
        try {
            return new ByteArrayRawData(s.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static RawData fromFile(String path) {
        return new FileRawData(path);
    }

    public static RawData fromFile(File file) {
        return new FileRawData(file);
    }

    public static RawData fromFile(Path path) {
        return new FileRawData(path);
    }

    protected static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    protected static final InputStream EMPTY_INPUT_STREAM = new InputStream() {
        @Override
        public int read() throws IOException {
            return -1;
        }
    };

    public static final RawData EMPTY = new RawData(0) {

        @Override
        public byte[] asByteArray(boolean modifiable) throws IOException {
            return EMPTY_BYTE_ARRAY;
        }

        @Override
        public InputStream asStream() throws IOException {
            return EMPTY_INPUT_STREAM;
        }

        @Override
        public int compareTo(RawData other) {
            try {
                long otherLength = other.length();
                if (otherLength < 0) {
                    try (InputStream in = other.asStream()) {
                        if (in.read() == EOF)
                            return 0;
                        else
                            return -1;
                    }
                } else {
                    if (otherLength > 0) return -1;
                    return 0;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Override
    public String toString() {
        try {
            return asStringUTF8();
        } catch (IOException e) {
            return super.toString();
        }
    }
}
