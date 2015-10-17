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

    @Test
    public void testHex() {
        Binary b = Binary.fromHex("48454c4c4f");
        assertEquals("HELLO", b.asStringASCII());
        assertEquals("48454C4C4F", b.asHex());
    }

    @Test
    public void testBase64() {
        Binary b = Binary.fromBase64("SEVMTE8");
        assertEquals("HELLO", b.asStringASCII());
        assertEquals("SEVMTE8=", b.asBase64(BaseEncoding.Padding.STANDARD));
        assertEquals("SEVMTE8.", b.asBase64(BaseEncoding.Padding.SAFE));
        assertEquals("SEVMTE8", b.asBase64(BaseEncoding.Padding.NO));

        Binary b2 = Binary.fromBase64("zs/Q0dI=");
        byte[] bytes = b2.asByteArray();
        assertEquals((byte)206, bytes[0]);
        assertEquals((byte)207, bytes[1]);
        assertEquals((byte)208, bytes[2]);
        assertEquals((byte)209, bytes[3]);
        assertEquals((byte)210, bytes[4]);
        assertEquals("zs_Q0dI", b2.asBase64(BaseEncoding.Dialect.SAFE, BaseEncoding.Padding.NO));

        assertArrayEquals(bytes, Binary.fromBase64("zs_Q0dI").asByteArray());
    }

}
