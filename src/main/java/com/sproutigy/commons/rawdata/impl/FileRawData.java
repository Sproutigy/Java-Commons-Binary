package com.sproutigy.commons.rawdata.impl;

import com.sproutigy.commons.rawdata.RawData;

import java.io.*;

/**
 * @author LukeAheadNET
 */
public class FileRawData extends AbstractStreamableRawData {

    private File file;


    public FileRawData(String path) {
        this(new File(path));
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

    public File getFile() {
        return file;
    }

    @Override
    public RawData subrange(long offset, long length) throws IOException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(getFile(), "r");
        try {
            randomAccessFile.seek(offset);
            InputStream streamAdapter = new InputStream() {
                @Override
                public int read() throws IOException {
                    return randomAccessFile.read();
                }
            };
            return RawData.fromByteArray(readBytesFromStream(streamAdapter, length));
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
