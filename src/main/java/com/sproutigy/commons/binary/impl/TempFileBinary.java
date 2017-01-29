package com.sproutigy.commons.binary.impl;

import com.sproutigy.commons.binary.BinaryException;

import java.io.File;

/**
 * @author LukeAheadNET
 */
public class TempFileBinary extends FileBinary {

    private boolean deleteOnClose;


    public TempFileBinary(String path, boolean deleteOnClose, boolean deleteOnExit) {
        super(path);
        if (deleteOnExit) getFile().deleteOnExit();
        this.deleteOnClose = deleteOnClose;
    }

    public TempFileBinary(File file, boolean deleteOnClose, boolean deleteOnExit) {
        super(file);
        if (deleteOnExit) file.deleteOnExit();
        this.deleteOnClose = deleteOnClose;
    }

    @Override
    public String toTempFile() throws BinaryException {
        return getFile().getAbsolutePath();
    }

    @Override
    public void close() throws BinaryException {
        if (deleteOnClose) {
            try {
                File f = getFile();
                if (f != null && f.exists()) {
                    if (!f.delete()) {
                        f.deleteOnExit();
                    }
                }
            } catch(Throwable ignore) { }
        }
        super.close();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } catch(Throwable ignore) { }

        super.finalize();
    }
}
