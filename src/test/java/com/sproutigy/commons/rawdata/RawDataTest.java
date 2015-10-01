package com.sproutigy.commons.rawdata;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author LukeAheadNET
 */
public class RawDataTest {

    @Test
    public void testEmpty() throws IOException {
        assertEquals(0, RawData.EMPTY.length());
        assertEquals(0, RawData.EMPTY.asByteArray().length);
    }

    @Test
    public void testStrings() throws IOException {

        RawData dataFromString = RawData.fromByteArray("HELLO".getBytes("UTF-8"));
        assertEquals(5, dataFromString.length());
        assertArrayEquals("HELLO".getBytes("UTF-8"), dataFromString.asByteArray());
        assertEquals("HELLO", RawData.fromStringASCII("HELLO").asStringASCII());
    }

    @Test
    public void testFileAsString() throws IOException {
        File file = Files.createTempFile("", ".tmp").toFile().getAbsoluteFile();
        file.deleteOnExit();
        FileOutputStream output = new FileOutputStream(file);
        try {
            output.write("Hello".getBytes("UTF-8"));
            RawData.fromStringASCII(" World").toStream(output);
        } finally {
            output.close();
        }
        assertEquals("Hello World", RawData.fromFile(file).asStringUTF8());
    }

    @Test
    public void testSubrange() throws IOException {
        assertEquals("ello", RawData.fromStringASCII("Hello World").subrange(1, 4).asStringASCII());
        String file = RawData.fromStringASCII("ABCDEFGHIJK").toTempFile();
        assertEquals("BC", RawData.fromFile(file).subrange(1,2).asStringASCII());
    }
}
