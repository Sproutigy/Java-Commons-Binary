package com.sproutigy.commons.binary;

/**
 * @author LukeAheadNET
 */
public class NewLine {
    private NewLine() { }

    public static final String LOCAL = System.lineSeparator();

    public static final String UNIX = "\n";
    public static final String MAC = "\n";
    public static final String WINDOWS = "\r\n";
    public static final String DEPRECATED = "\r";

    /**
     * Normalizes string using local system's new line separator
     *
     * @param source
     * @return string with normalized new lines
     */
    public static String normalize(String source) {
        return normalize(source, LOCAL);
    }

    public static String normalize(String source, String targetLineSeparator) {
        String target = source.replace("\r\n", "\n").replace("\r", "\n");
        if (!targetLineSeparator.equals("\n")) {
            target = target.replace("\n", targetLineSeparator);
        }
        return target;
    }
}
