package com.sproutigy.commons.binary;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author LukeAheadNET
 */
public class BinaryTest {

    @Test
    public void testEmpty() {
        assertEquals(0, Binary.EMPTY.length());
        assertEquals(0, Binary.EMPTY.asByteArray().length);
    }

    @Test
    public void testStrings() throws UnsupportedEncodingException {

        Binary dataFromString = Binary.fromByteArray("HELLO".getBytes("UTF-8"));
        assertEquals(5, dataFromString.length());
        assertArrayEquals("HELLO".getBytes("UTF-8"), dataFromString.asByteArray());
        assertEquals("HELLO", Binary.fromStringASCII("HELLO").asStringASCII());
    }

    @Test
    public void testFileAsString() throws IOException {
        File file = Files.createTempFile("", ".tmp").toFile().getAbsoluteFile();
        file.deleteOnExit();
        FileOutputStream output = new FileOutputStream(file);
        try {
            output.write("Hello".getBytes("UTF-8"));
            Binary.fromStringASCII(" World").toStream(output);
        } finally {
            output.close();
        }
        assertEquals("Hello World", Binary.fromFile(file).asStringUTF8());
    }

    @Test
    public void testSubrange() {
        assertEquals("ello", Binary.fromStringASCII("Hello World").subrange(1, 4).asStringASCII());
        String file = Binary.fromStringASCII("ABCDEFGHIJK").toTempFile();
        assertEquals("BC", Binary.fromFile(file).subrange(1,2).asStringASCII());
    }
}
