package com.sproutigy.commons.binary;

import java.nio.charset.Charset;

public final class Charsets {
    private Charsets() { }

    public static final Charset US_ASCII = forName("US-ASCII");
    public static final Charset ISO_8859_1 = forName("ISO-8859-1");
    public static final Charset UTF_8 = forName("UTF-8");
    public static final Charset UTF_16BE = forName("UTF-16BE");
    public static final Charset UTF_16LE = forName("UTF-16LE");
    public static final Charset UTF_16 = forName("UTF-16");
    public static final Charset UTF_32BE = forName("UTF-32BE");
    public static final Charset UTF_32LE = forName("UTF-32LE");
    public static final Charset UTF_32 = forName("UTF-32");

    public static Charset forName(String charsetName) {
        return Charset.forName(charsetName);
    }
}
