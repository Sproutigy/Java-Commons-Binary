package com.sproutigy.commons.rawdata.impl;

import com.sproutigy.commons.rawdata.RawData;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author LukeAhead.net
 */
public class FileRawData extends AbstractStreamableRawData {

    private File file;


    public FileRawData(String path) {
        this(new File(path));
    }

    public FileRawData(Path path) {
        this(path.toFile());
    }

    public FileRawData(File file) {
        super(file.length());
        this.file = file;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FileRawData) {
            if (getFile().equals(((FileRawData) other).getFile())) {
                return true;
            }
        }

        return super.equals(other);
    }

    public String getPathString() {
        return file.getPath();
    }

    public Path getPath() {
        return Paths.get(getPathString());
    }

    public File getFile() {
        return file;
    }

    @Override
    public RawData subrange(long offset, long length) throws IOException {
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(getFile(), "r")) {
            randomAccessFile.seek(offset);
            InputStream streamAdapter = new InputStream() {
                @Override
                public int read() throws IOException {
                    return randomAccessFile.read();
                }
            };
            return RawData.fromByteArray(readBytesFromStream(streamAdapter, length));
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
