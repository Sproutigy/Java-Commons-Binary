package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.Binary;

import java.io.*;

/**
 * @author LukeAheadNET
 */
public class FileBinary extends AbstractStreamableBinary {

    private File file;


    public FileBinary(String path) {
        this(new File(path));
    }

    public FileBinary(File file) {
        super(file.length());
        this.file = file;
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
        return file.getPath();
    }

    public File getFile() {
        return file;
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
            return Binary.fromByteArray(readBytesFromStream(streamAdapter, length));
        } finally {
            randomAccessFile.close();
        }
    }

    @Override
    public InputStream asStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public String toString() {
        return file.getPath();
    }
}
