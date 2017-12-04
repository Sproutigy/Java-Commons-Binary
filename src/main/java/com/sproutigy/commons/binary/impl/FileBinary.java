package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;

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


    public FileBinary(String path) {
        this(path, null);
    }

    public FileBinary(String path, Charset charset) {
        this(Paths.get(path), charset);
    }

    public FileBinary(File file) {
        this(file.toPath(), null);
    }

    public FileBinary(File file, Charset charset) {
        this(file.toPath(), charset);
    }

    public FileBinary(Path path) {
        this(path, null);
    }

    public FileBinary(Path path, Charset charset) {
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
    public Binary subrange(long offset, long length) throws IOException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(getFile(), "r");
        try {
            randomAccessFile.seek(offset);
            InputStream streamAdapter = new InputStream() {
                @Override
                public int read() throws IOException {
                    return randomAccessFile.read();
                }
            };
            return Binary.from(readBytesFromStream(streamAdapter, length));
        } finally {
            randomAccessFile.close();
        }
    }

    @Override
    public boolean isConsumable() {
        return false;
    }

    @Override
    public byte[] asByteArray(boolean modifiable) throws IOException {
        return Files.readAllBytes(path);
    }

    @Override
    public InputStream asStream() throws IOException {
        return Files.newInputStream(path);
    }

    @Override
    public String toString() {
        return getPathString();
    }


    private static long fetchFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            return LENGTH_UNSPECIFIED;
        }
    }
}
