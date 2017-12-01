package com.sproutigy.commons.binary;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

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
        Binary dataFromString = Binary.from("HELLO".getBytes("UTF-8"));
        assertEquals(5, dataFromString.length());
        assertArrayEquals("HELLO".getBytes("UTF-8"), dataFromString.asByteArray());
        assertEquals("HELLO", Binary.fromString("HELLO", Charset.forName("US-ASCII")).asStringASCII());
    }

    @Test
    public void testFileAsString() throws IOException {
        File file = File.createTempFile("binary-test", ".tmp").getAbsoluteFile();
        file.deleteOnExit();
        FileOutputStream output = new FileOutputStream(file);
        try {
            output.write("Hello".getBytes("UTF-8"));
            Binary.fromString(" World", Charset.forName("US-ASCII")).toStream(output);
        } finally {
            output.close();
        }
        assertEquals("Hello World", Binary.fromFile(file).asStringUTF8());
    }

    @Test
    public void testSubrange() {
        assertEquals("ello", Binary.fromString("Hello World", Charset.forName("US-ASCII")).subrange(1, 4).asStringASCII());
        String file = Binary.fromString("ABCDEFGHIJK", Charset.forName("US-ASCII")).toTempFile();
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

    @Test
    public void testStringCharset() {
        String s = "tęśt";

        Binary b1 = Binary.fromString(s, Charsets.UTF_32);
        assertTrue(b1.hasCharset());
        assertEquals(Charsets.UTF_32, b1.getCharset());
        String t1 = b1.asString();
        assertEquals(s, t1);

        Binary b2 = Binary.fromString(s, Charsets.ISO_8859_1);
        assertTrue(b2.hasCharset());
        assertEquals(Charsets.ISO_8859_1, b2.getCharset());
        String t2 = b2.asString();
        assertNotEquals(s, t2);
        assertEquals("t??t", t2);
    }

}
