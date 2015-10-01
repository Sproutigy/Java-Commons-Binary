package com.sproutigy.commons.rawdata.impl;

import java.io.File;
import java.io.IOException;

/**
 * @author LukeAheadNET
 */
public class TempFileRawData extends FileRawData {

    private boolean deleteOnClose;


    public TempFileRawData(String path, boolean deleteOnClose, boolean deleteOnExit) {
        super(path);
        if (deleteOnExit) getFile().deleteOnExit();
        this.deleteOnClose = deleteOnClose;
    }

    public TempFileRawData(File file, boolean deleteOnClose, boolean deleteOnExit) {
        super(file);
        if (deleteOnExit) file.deleteOnExit();
        this.deleteOnClose = deleteOnClose;
    }

    @Override
    public String toTempFile() throws IOException {
        return getFile().getAbsolutePath();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (deleteOnClose) {
            if (!getFile().delete()) {
                getFile().deleteOnExit();
            }
        }
    }

}
