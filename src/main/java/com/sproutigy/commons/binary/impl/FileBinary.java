package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;
import com.sproutigy.commons.binary.BinaryException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author LukeAheadNET
 */
public class FileBinary extends AbstractStreamableBinary {

    private Path path;


    public FileBinary(String path) throws BinaryException {
        this(path, null);
    }

    public FileBinary(String path, Charset charset) throws BinaryException {
        this(Paths.get(path), charset);
    }

    public FileBinary(File file) throws BinaryException {
        this(file.toPath(), null);
    }

    public FileBinary(File file, Charset charset) throws BinaryException {
        this(file.toPath(), charset);
    }

    public FileBinary(Path path) throws BinaryException {
        this(path, null);
    }

    public FileBinary(Path path, Charset charset) throws BinaryException {
        super(fetchFileSize(path));
        this.path = path;
        this.setCharset(charset);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FileBinary) {
            if (getFile().equals(((FileBinary) other).getFile())) {
                return true;
            }
        }

        return super.equals(other);
    }

    public String getPathString() {
        return path.toString();
    }

    public File getFile() {
        return path.toFile();
    }

    public Path getPath() {
        return path;
    }

    @Override
    public Binary subrange(long offset, long length) throws BinaryException {
        try {
            final RandomAccessFile randomAccessFile = new RandomAccessFile(getFile(), "r");
            try {
                randomAccessFile.seek(offset);
                InputStream streamAdapter = new InputStream() {
                    @Override
                    public int read() {
                        try {
                            return randomAccessFile.read();
                        } catch (IOException e) {
                            throw new BinaryException(e);
                        }
                    }
                };
                return Binary.from(readBytesFromStream(streamAdapter, length));
            } finally {
                randomAccessFile.close();
            }
        } catch(IOException e) {
            throw new BinaryException(e);
        }
    }

    @Override
    public byte[] asByteArray(boolean modifiable) throws BinaryException {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new BinaryException(e);
        }
    }

    @Override
    public InputStream asStream() throws BinaryException {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new BinaryException(e);
        }
    }

    @Override
    public String toString() {
        return getPathString();
    }


    private static long fetchFileSize(Path path) throws BinaryException {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new BinaryException(e);
        }
    }
}
