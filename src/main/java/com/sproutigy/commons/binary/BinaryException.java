package com.sproutigy.commons.binary;

/**
 * @author LukeAheadNET
 */
public class BinaryException extends RuntimeException {
    public BinaryException() {
    }

    public BinaryException(String message) {
        super(message);
    }

    public BinaryException(String message, Throwable cause) {
        super(message, cause);
    }

    public BinaryException(Throwable cause) {
        super(cause);
    }

    public BinaryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
