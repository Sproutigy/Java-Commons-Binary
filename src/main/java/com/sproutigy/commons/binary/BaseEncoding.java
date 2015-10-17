package com.sproutigy.commons.binary;

/**
 * @author LukeAheadNET
 */
public class BaseEncoding {

    public BaseEncoding() {
    }

    public enum Dialect {
        /**
         * Standard Base 64 Encoding
         */
        STANDARD,

        /**
         * URL-safe (and filename-safe) Base 64 Encoding
         */
        SAFE
    }

    public enum Padding {
        /**
         * No padding at all
         */
        NO,

        /**
         * Use equals sign character ('=') as padding
         */
        STANDARD,

        /**
         * Use dot sign character ('.') as padding
         */
        SAFE
    }

}
