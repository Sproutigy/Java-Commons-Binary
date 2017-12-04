package com.sproutigy.commons.binary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class UncheckedBinary extends Binary {
    protected Binary decorated;

    UncheckedBinary() { }

    UncheckedBinary(long length) {
        super(length);
    }

    public UncheckedBinary(Binary decorated) {
        if (decorated == null) throw new NullPointerException("binary == null");
        this.decorated = decorated;
    }

    @Override
    public boolean isConsumable() {
        if (decorated != null) {
            return decorated.isConsumable();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        try {
            if (decorated == null) {
                return super.isEmpty();
            } else {
                return decorated.isEmpty();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasLength() {
        try {
            if (decorated == null) {
                return super.hasLength();
            } else {
                return decorated.hasLength();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long length() {
        try {
            if (decorated == null) {
                return super.length();
            } else {
                return decorated.length();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long length(boolean forceCalculate) {
        try {
            if (decorated == null) {
                return super.length(forceCalculate);
            } else {
                return decorated.length(forceCalculate);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected long provideLength() throws IOException {
        return super.provideLength();
    }

    @Override
    public byte[] asByteArray() {
        try {
            if (decorated == null) {
                return super.asByteArray();
            } else {
                return decorated.asByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] asByteArray(boolean modifiable) {
        try {
            if (decorated != null) {
                return decorated.asByteArray(modifiable);
            }
            throw new UnsupportedOperationException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream asStream() {
        try {
            if (decorated != null) {
                return decorated.asStream();
            }
            throw new UnsupportedOperationException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ByteBuffer asByteBuffer() {
        try {
            if (decorated == null) {
                return super.asByteBuffer();
            } else {
                return decorated.asByteBuffer();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ByteBuffer asByteBuffer(boolean modifiable) {
        try {
            if (decorated == null) {
                return super.asByteBuffer(modifiable);
            } else {
                return decorated.asByteBuffer(modifiable);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asStringASCII() {
        try {
            if (decorated == null) {
                return super.asStringASCII();
            } else {
                return decorated.asStringASCII();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asStringUTF8() {
        try {
            if (decorated == null) {
                return super.asStringUTF8();
            } else {
                return decorated.asStringUTF8();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asString() {
        try {
            if (decorated == null) {
                return super.toString();
            } else {
                return decorated.asString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asString(String charsetName) {
        try {
            if (decorated == null) {
                return super.asString(charsetName);
            } else {
                return decorated.asString(charsetName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asString(Charset charset) {
        try {
            if (decorated == null) {
                return super.asString(charset);
            } else {
                return decorated.asString(charset);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte asByte() {
        try {
            if (decorated == null) {
                return super.asByte();
            } else {
                return decorated.asByte();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short asShort() {
        try {
            if (decorated == null) {
                return super.asShort();
            } else {
                return decorated.asShort();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int asInteger() {
        try {
            if (decorated == null) {
                return super.asInteger();
            } else {
                return decorated.asInteger();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long asLong() {
        try {
            if (decorated == null) {
                return super.asLong();
            } else {
                return decorated.asLong();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float asFloat() {
        try {
            if (decorated == null) {
                return super.asFloat();
            } else {
                return decorated.asFloat();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double asDouble() {
        try {
            if (decorated == null) {
                return super.asDouble();
            } else {
                return decorated.asDouble();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public char asCharacter() {
        try {
            if (decorated == null) {
                return super.asCharacter();
            } else {
                return decorated.asCharacter();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toTempFile() {
        try {
            if (decorated == null) {
                return super.toTempFile();
            } else {
                return decorated.toTempFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toFile(String path) throws IOException {
        if (decorated == null) {
            super.toFile(path);
        } else {
            decorated.toFile(path);
        }
    }

    @Override
    public void toFile(String path, boolean append) throws IOException {
        if (decorated == null) {
            super.toFile(path, append);
        } else {
            decorated.toFile(path, append);
        }
    }

    @Override
    public void toFile(File file) throws IOException {
        if (decorated == null) {
            super.toFile(file);
        } else {
            decorated.toFile(file);
        }
    }

    @Override
    public void toFile(File file, boolean append) throws IOException {
        if (decorated == null) {
            super.toFile(file, append);
        } else {
            decorated.toFile(file, append);
        }
    }

    @Override
    public void toFile(Path path) throws IOException {
        if (decorated == null) {
            super.toFile(path);
        } else {
            decorated.toFile(path);
        }
    }

    @Override
    public void toFile(Path path, boolean append) throws IOException {
        if (decorated == null) {
            super.toFile(path, append);
        } else {
            decorated.toFile(path, append);
        }
    }

    @Override
    public void to(OutputStream out) throws IOException {
        if (decorated == null) {
            super.to(out);
        } else {
            decorated.to(out);
        }
    }

    @Override
    public void to(WritableByteChannel channel) throws IOException {
        if (decorated == null) {
            super.to(channel);
        } else {
            decorated.to(channel);
        }
    }

    @Override
    public void to(BinaryBuilder binaryBuilder) {
        if (decorated == null) {
            super.to(binaryBuilder);
        } else {
            decorated.to(binaryBuilder);
        }
    }

    @Override
    public int toByteArray(byte[] target) {
        try {
            if (decorated == null) {
                return super.toByteArray(target);
            } else {
                return decorated.toByteArray(target);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int toByteArray(byte[] target, int targetOffset) {
        try {
            if (decorated == null) {
                return super.toByteArray(target, targetOffset);
            } else {
                return decorated.toByteArray(target, targetOffset);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asHex() {
        try {
            if (decorated == null) {
                return super.asHex();
            } else {
                return decorated.asHex();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asBase64() {
        try {
            if (decorated == null) {
                return super.asBase64();
            } else {
                return decorated.asBase64();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asBase64(BaseEncoding.Dialect dialect) {
        try {
            if (decorated == null) {
                return super.asBase64(dialect);
            } else {
                return decorated.asBase64(dialect);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asBase64(BaseEncoding.Padding padding) {
        try {
            if (decorated == null) {
                return super.asBase64(padding);
            } else {
                return decorated.asBase64(padding);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asBase64(BaseEncoding.Dialect dialect, BaseEncoding.Padding padding) {
        try {
            if (decorated == null) {
                return super.asBase64(dialect, padding);
            } else {
                return decorated.asBase64(dialect, padding);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Binary subrange(long offset) {
        try {
            if (decorated == null) {
                return super.subrange(offset);
            } else {
                return decorated.subrange(offset);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UncheckedBinary subrange(long offset, long length) {
        try {
            if (decorated == null) {
                return (UncheckedBinary)super.subrange(offset, length);
            } else {
                return (UncheckedBinary) decorated.subrange(offset, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (decorated == null) {
            return super.equals(other);
        } else {
            return decorated.equals(other);
        }
    }

    @Override
    public int hashCode() {
        if (decorated == null) {
            return super.hashCode();
        } else {
            return decorated.hashCode();
        }
    }

    @Override
    public int compareTo(Binary other) {
        if (decorated == null) {
            return super.compareTo(other);
        } else {
            return decorated.compareTo(other);
        }
    }

    @Override
    public void close() {
        try {
            if (decorated == null) {
                super.close();
            } else {
                decorated.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasCharset() {
        if (decorated == null) {
            return super.hasCharset();
        } else {
            return decorated.hasCharset();
        }
    }

    @Override
    public Charset getCharset() {
        if (decorated == null) {
            return super.getCharset();
        } else {
            return decorated.getCharset();
        }
    }

    @Override
    protected UncheckedBinary setCharset(Charset charset) {
        if (decorated == null) {
            super.setCharset(charset);
        } else {
            decorated.setCharset(charset);
        }
        return this;
    }

    @Override
    public String toString() {
        if (decorated == null) {
            return super.toString();
        } else {
            return decorated.toString();
        }
    }

    @Override
    protected Binary clone() {
        if (decorated == null) {
            return super.clone();
        } else {
            return decorated.clone();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (decorated == null) {
            super.finalize();
        } else {
            decorated = null;
        }
    }
}
