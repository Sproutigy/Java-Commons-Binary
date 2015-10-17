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
        super.close();
        if (deleteOnClose) {
            if (!getFile().delete()) {
                getFile().deleteOnExit();
            }
        }
    }

}
