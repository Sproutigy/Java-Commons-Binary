package com.sproutigy.commons.rawdata.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author LukeAhead.net
 */
public class TempFileRawData extends FileRawData {

    private boolean deleteOnClose;


    public TempFileRawData(String path, boolean deleteOnClose, boolean deleteOnExit) {
        super(path);
        if (deleteOnExit) getFile().deleteOnExit();
        this.deleteOnClose = deleteOnClose;
    }

    public TempFileRawData(Path path, boolean deleteOnClose, boolean deleteOnExit) {
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
    public String toTempFile(boolean modifiable) throws IOException {
        if (!modifiable) {
            return getFile().getAbsolutePath();
        }
        return super.toTempFile(true);
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (deleteOnClose) {
            if (!getFile().delete()) {
                getFile().deleteOnExit();
            }
        }
    }

}
