package com.sproutigy.commons.binary;

import com.sproutigy.commons.binary.impl.ByteArrayBinary;
import com.sproutigy.commons.binary.impl.ByteBufferBinary;
import com.sproutigy.commons.binary.impl.FileBinary;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

/**
 * Binary represents raw data that may be represented by different data structures and easily converted from one to another.
 * This abstract class may be safely extended to provide own Binary handlers.
 *
 * @author LukeAheadNET
 */
public abstract class Binary implements Closeable, Comparable<Binary>, Cloneable {

    public static final long LENGTH_UNSPECIFIED = -1;

    protected static final int EOF = -1;

    protected static final int BITS_PER_BYTE = 8;

    public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;


    protected long length = LENGTH_UNSPECIFIED;

    protected Charset charset;


    protected Binary() { }

    protected Binary(long length) {
        this.length = length;
    }


    public boolean hasLength() throws BinaryException {
        return length != LENGTH_UNSPECIFIED;
    }

    public final long length() throws BinaryException {
        return length(true);
    }

    public final long length(boolean forceCalculate) throws BinaryException {
        if (length == LENGTH_UNSPECIFIED) {
            if (forceCalculate) {
                length = provideLength();
            }
        }
        return length;
    }

    protected long provideLength() throws BinaryException {
        int counter = 0;
        InputStream in = asStream();
        try {
            try {
                while (in.read() != EOF) {
                    counter++;
                }
            } finally {
                in.close();
            }
        } catch(IOException e) {
            throw new BinaryException(e);
        }

        return counter;
    }

    public final byte[] asByteArray() throws BinaryException {
        return asByteArray(true);
    }

    public abstract byte[] asByteArray(boolean modifiable) throws BinaryException;

    public abstract InputStream asStream() throws BinaryException;

    public ByteBuffer asByteBuffer() throws BinaryException {
        return asByteBuffer(true);
    }

    public ByteBuffer asByteBuffer(boolean modifiable) throws BinaryException {
        return (ByteBuffer)ByteBuffer.wrap(asByteArray(modifiable)).position(0);
    }

    public String asStringASCII() throws BinaryException {
        return asString(Charsets.US_ASCII);
    }

    public String asStringISO() throws BinaryException {
        return asString(Charsets.ISO_8859_1);
    }

    public String asStringUTF8() throws BinaryException {
        return asString(Charsets.UTF_8);
    }

    /**
     * Returns string in specified setCharsetInternal or in default (UTF-8) setCharsetInternal when not specified
     *
     * @return String representation of binary
     * @throws BinaryException
     */
    public String asString() throws BinaryException {
        Charset charset = getCharset();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        return asString(charset);
    }

    public String asString(String charsetName) throws BinaryException {
        try {
            return new String(asByteArray(false), charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new BinaryException(e);
        }
    }

    public String asString(Charset charset) throws BinaryException {
        return new String(asByteArray(false), charset);
    }

    public byte asByte() throws BinaryException {
        return asByteArray(false)[0];
    }

    public short asShort() throws BinaryException {
        return ((ByteBuffer)ByteBuffer.allocate(Short.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getShort();
    }

    public int asInteger() throws BinaryException {
        return ((ByteBuffer)ByteBuffer.allocate(Integer.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getInt();
    }

    public long asLong() throws BinaryException {
        return ((ByteBuffer)ByteBuffer.allocate(Long.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getLong();
    }

    public float asFloat() throws BinaryException {
        return ((ByteBuffer)ByteBuffer.allocate(Float.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getFloat();
    }

    public double asDouble() throws BinaryException {
        return ((ByteBuffer)ByteBuffer.allocate(Double.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getDouble();
    }

    public char asCharacter() throws BinaryException {
        return ((ByteBuffer)ByteBuffer.allocate(Character.SIZE / BITS_PER_BYTE).put(asByteArray(false)).flip()).getChar();
    }

    /**
     * Creates new temporary file with random name and returns full path to the file
     *
     * @return Temporary file path string
     * @throws BinaryException wrapped IOException
     */
    public String toTempFile() throws BinaryException {
        try {
            File file = File.createTempFile(UUID.randomUUID().toString(), ".binary.tmp");
            file.deleteOnExit();
            toFile(file);
            return file.getAbsolutePath();
        } catch(IOException e) {
            throw new BinaryException(e);
        }
    }

    public void toFile(String path) throws BinaryException {
        toFile(new File(path));
    }

    public void toFile(File file) throws BinaryException {
        toFile(file, false);
    }

    public void toFile(File file, boolean append) throws BinaryException {
        try {
            OutputStream out = new FileOutputStream(file, append);
            try {
                toStream(out);
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new BinaryException(e);
        }
    }

    public void toFile(Path path) throws BinaryException {
        toFile(path, false);
    }

    public void toFile(Path path, boolean append) throws BinaryException {
        try {
            OutputStream out;
            if (append) {
                out = Files.newOutputStream(path, StandardOpenOption.APPEND);
            } else {
                out = Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            try {
                toStream(out);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new BinaryException(e);
        }
    }

    public void toStream(OutputStream out) throws BinaryException {
        byte[] buffer;
        if (hasLength() && length() < 4096) {
            buffer = new byte[(int)length()];
        } else {
            buffer = new byte[4096];
        }

        try {
            InputStream in = asStream();
            try {
                int len;
                while ((len = in.read(buffer)) != EOF) {
                    out.write(buffer, 0, len);
                }
            } finally {
                in.close();
            }
        } catch(IOException e) {
            throw new BinaryException(e);
        }
    }

    /**
     * Writes data to some existing byte array
     *
     * @param target byte array destination
     * @return length of source (written) data
     * @throws BinaryException wrapped IOException or validation exception
     */
    public int toByteArray(byte[] target) throws BinaryException {
        return toByteArray(target, 0);
    }

    /**
     * Writes data to some existing byte array starting from specific offset
     *
     * @param target byte array destination
     * @param targetOffset destination offset to start
     * @return length of source (written) data
     * @throws BinaryException wrapped IOException or validation exception
     */
    public int toByteArray(byte[] target, int targetOffset) throws BinaryException {
        long length = length();
        if ((long)targetOffset + length > Integer.MAX_VALUE) {
            throw new BinaryException("Unable to write - too big data");
        }
        if (target.length < targetOffset + length) {
            throw new BinaryException("Insufficient target byte array size");
        }

        if (length < 0) {
            try {
                length = 0;
                int curOffset = targetOffset;
                InputStream in = asStream();
                try {
                    int readbyte;
                    while ((readbyte = in.read()) != EOF) {
                        target[curOffset] = (byte) readbyte;
                        curOffset++;
                        length++;
                    }
                } finally {
                    in.close();
                }
            } catch(IOException e) {
                throw new BinaryException(e);
            }
        }
        else
        {
            System.arraycopy(asByteArray(false), 0, target, targetOffset, (int)length);
        }
        return (int)length;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Hexadecimal representation containing digits (0-9) with upper-case letters (A-F)
     * @return Hexadecimal encoded string
     */
    public String asHex() {
        byte[] bytes = asByteArray();
        char[] hex = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hex[j * 2] = HEX_ARRAY[v >>> 4];
            hex[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hex);
    }

    /**
     * Represent as Base 64 with standard dialect and standard padding
     * @return Base 64 encoded string
     */
    public String asBase64() {
        return asBase64(BaseEncoding.Dialect.STANDARD, BaseEncoding.Padding.STANDARD);
    }

    /**
     * Represent as Base 64 with customized dialect and standard padding
     * @return Base 64 encoded string
     */
    public String asBase64(BaseEncoding.Dialect dialect) {
        return asBase64(dialect, BaseEncoding.Padding.STANDARD);
    }

    /**
     * Represent as Base 64 with standard dialect and customized padding
     * @return Base 64 encoded string
     */
    public String asBase64(BaseEncoding.Padding padding) {
        return asBase64(BaseEncoding.Dialect.STANDARD, padding);
    }

    /**
     * Represent as Base 64 with customized dialect and padding
     * @return Base 64 encoded string
     */
    public String asBase64(BaseEncoding.Dialect dialect, BaseEncoding.Padding padding) {
        String standardBase64 = DatatypeConverter.printBase64Binary(asByteArray(false));
        if (dialect == BaseEncoding.Dialect.STANDARD && padding == BaseEncoding.Padding.STANDARD) {
            return standardBase64;
        }

        StringBuilder safeBase64 = new StringBuilder(standardBase64.length());
        for(int i=0; i<standardBase64.length(); i++) {
            char c = standardBase64.charAt(i);

            if (dialect == BaseEncoding.Dialect.SAFE) {
                if (c == '+') c = '-';
                else if (c == '/') c = '_';
            }

            if (c == '=') {
                if (padding == BaseEncoding.Padding.STANDARD) {
                    safeBase64.append('=');
                }
                else if (padding == BaseEncoding.Padding.SAFE) {
                    safeBase64.append('.');
                }
            } else {
                safeBase64.append(c);
            }
        }
        return safeBase64.toString();
    }

    public Binary subrange(long offset) throws BinaryException {
        return subrange(offset, LENGTH_UNSPECIFIED);
    }

    public Binary subrange(long offset, long length) throws BinaryException {
        if (offset > Integer.MAX_VALUE || length > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("Offset and/or length higher than Integer.MAX_VALUE");
        }
        long curLength = length();
        if (curLength >= 0 && (length-offset) > curLength) {
            throw new IndexOutOfBoundsException("Out of data range");
        }

        byte[] bytes = asByteArray(false);
        int newLength = length > 0 ? (int)length : (int)(bytes.length-offset);
        return new ByteArrayBinary(bytes, (int)offset, newLength).setCharset(getCharset());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Binary)) return false;
        return compareTo((Binary)other) == 0;
    }

    @Override
    public int hashCode() {
        int result = 1;

        try {
            InputStream in = asStream();
            try {
                int readbyte;
                while ((readbyte = in.read()) != EOF) {
                    result = 31 * result + readbyte;
                }
            } finally {
                in.close();
            }
        } catch(IOException e) {
            throw new BinaryException(e);
        }

        return result;
    }

    @Override
    public int compareTo(Binary other) {
        if (other == null) return 1;
        if (this == other) return 0;

        try {
            InputStream thisStream = asStream();
            try {
                InputStream otherStream = other.asStream();
                try {
                    while(true) {
                        int thisReadByte = thisStream.read();
                        int otherReadByte = otherStream.read();

                        if (thisReadByte == otherReadByte) continue;
                        if (thisReadByte == EOF) return -1;
                        if (otherReadByte == EOF) return 1;
                        return thisReadByte - otherReadByte;
                    }
                } finally {
                    otherStream.close();
                }
            } finally {
                thisStream.close();
            }
        } catch(IOException e) {
            throw new BinaryException(e);
        }
    }

    @Override
    public void close() throws BinaryException {

    }

    protected static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int readlen;
        while ((readlen = in.read(buffer)) != EOF) {
            out.write(buffer, 0, readlen);
        }
        out.flush();
    }

    protected static byte[] readBytesFromStream(InputStream in) throws BinaryException {
        return readBytesFromStream(in, LENGTH_UNSPECIFIED);
    }

    protected static byte[] readBytesFromStream(InputStream in, long length) throws BinaryException {
        if (length == 0)
            return EMPTY_BYTE_ARRAY;

        if (length > Integer.MAX_VALUE)
            throw new BinaryException("Stream is longer than maximal byte array size");

        try {
            if (length <= 0) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                copyStream(in, out);
                return out.toByteArray();
            } else {
                int offset = 0;
                byte[] bytes = new byte[(int) length];
                int lenToRead;
                int readlen;
                while (true) {
                    lenToRead = bytes.length - offset;
                    if (lenToRead == 0) break;
                    readlen = in.read(bytes, offset, lenToRead);
                    if (readlen == EOF || readlen == lenToRead) break;
                    offset += readlen;
                }
                return bytes;
            }
        } catch(IOException e) {
            throw new BinaryException(e);
        }
    }

    public static Binary from(InputStream in) throws BinaryException {
        return from(in, LENGTH_UNSPECIFIED);
    }

    public static Binary from(InputStream in, long length) throws BinaryException {
        if (length == 0)
            return Binary.EMPTY;

        long expectedLength = (length > 0) ? length : BinaryBuilder.DEFAULT_EXPECTED_SIZE;
        BinaryBuilder builder = new BinaryBuilder(expectedLength);
        try {
            copyStream(in, builder);
            return builder.build();
        } catch(IOException e) {
            throw new BinaryException(e);
        }
    }

    public static Binary from(byte[] bytes) {
        return new ByteArrayBinary(bytes);
    }

    public static Binary from(byte[] bytes, int offset, int length) {
        return new ByteArrayBinary(bytes, offset, length);
    }

    public static Binary from(ByteBuffer byteBuffer) {
        return new ByteBufferBinary(byteBuffer);
    }

    /**
     * Creates UTF-8 string Binary
     * @param s
     * @return
     */
    public static Binary fromString(String s) {
        return fromString(s, DEFAULT_CHARSET);
    }

    public static Binary fromString(String s, Charset charset) {
        return new ByteArrayBinary(s.getBytes(charset)).setCharset(charset);
    }

    public static Binary fromString(String s, String charsetName) {
        try {
            return new ByteArrayBinary(s.getBytes(charsetName)).setCharset(Charset.forName(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Binary fromHex(String hex) {
        byte[] bytes = DatatypeConverter.parseHexBinary(hex);
        return Binary.from(bytes);
    }

    /**
     * Reads standard and URL/filename-safe Base 64 dialects as described in RFC 4686.
     * Additionally it accepts not Base 64 encoded strings without padding or allows to use dot character ('.') as padding character.
     * @param base64 Base 64 encoded string
     * @return Binary instance containing provided data
     */
    public static Binary fromBase64(String base64) {
        StringBuilder normalizedBase64 = new StringBuilder(base64.length()+3);
        for(int i = 0; i<base64.length(); i++) {
            char c = base64.charAt(i);
            if (c == '-') c = '+'; //translate from URL-safe
            if (c == '_') c = '/'; //translate from URL-safe
            if (c == '.') c = '='; //translate from custom URL-safe padding
            normalizedBase64.append(c);
        }

        //recreate padding
        while (normalizedBase64.length()%4 != 0) {
            normalizedBase64.append('=');
        }

        byte[] bytes = DatatypeConverter.parseBase64Binary(normalizedBase64.toString());
        return Binary.from(bytes);
    }

    public static Binary fromFile(String path) {
        return new FileBinary(path);
    }

    public static Binary fromFile(String path, Charset charset) {
        return new FileBinary(path, charset);
    }

    public static Binary fromFile(File file) {
        return new FileBinary(file);
    }

    public static Binary fromFile(File file, Charset charset) throws BinaryException {
        return new FileBinary(file, charset);
    }

    public static Binary fromFile(Path path) throws BinaryException {
        return new FileBinary(path);
    }

    public static Binary fromFile(Path path, Charset charset) {
        return new FileBinary(path, charset);
    }

    protected static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    protected static final InputStream EMPTY_INPUT_STREAM = new InputStream() {
        @Override
        public int read() throws BinaryException {
            return -1;
        }
    };

    public static final Binary EMPTY = new Binary(0) {

        @Override
        public byte[] asByteArray(boolean modifiable) throws BinaryException {
            return EMPTY_BYTE_ARRAY;
        }

        @Override
        public InputStream asStream() throws BinaryException {
            return EMPTY_INPUT_STREAM;
        }

        @Override
        public int compareTo(Binary other) {
            try {
                long otherLength = other.length();
                if (otherLength < 0) {
                    try {
                        InputStream in = other.asStream();
                        try {
                            if (in.read() == EOF)
                                return 0;
                            else
                                return -1;
                        } finally {
                            in.close();
                        }
                    } catch(IOException e) {
                        throw new BinaryException(e);
                    }
                } else {
                    if (otherLength > 0) return -1;
                    return 0;
                }
            } catch (BinaryException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public boolean hasCharset() {
        return charset != null;
    }

    public Charset getCharset() {
        return charset;
    }

    protected Binary setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    void setCharsetInternal(Charset charset) {
        setCharset(charset);
    }

    @Override
    public String toString() {
        try {
            if (hasCharset()) {
                return asString(getCharset());
            }
        } catch (BinaryException ignore) { }

        return super.toString();
    }
}
