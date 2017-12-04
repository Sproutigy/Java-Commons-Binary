package com.sproutigy.commons.binary;

import com.sproutigy.commons.binary.impl.ByteArrayBinary;
import com.sproutigy.commons.binary.impl.TempFileBinary;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * @author LukeAheadNET
 */
public class BinaryBuilderTest {

    @Test
    public void testBuildSmallData() throws Exception {
        BinaryBuilder builder = new BinaryBuilder().append("HELLO", "UTF-8").append((byte) 0);
        assertEquals(6, builder.length());
        Binary data = builder.build();
        assertEquals(6, data.length());
        assertEquals(true, ((UncheckedBinary)data).decorated instanceof ByteArrayBinary);
    }

    @Test
    public void testBuildBigData() throws Exception {
        int length = 1000;
        BinaryBuilder builder = new BinaryBuilder(0, 500, 1000);
        for (int i=0; i<length; i++) {
            builder.write(i % 256);
        }
        assertEquals(length, builder.length());
        Binary data = builder.build();
        assertEquals(length, data.length());
        assertEquals(true, ((UncheckedBinary)data).decorated instanceof TempFileBinary);
        File file = ((TempFileBinary)((UncheckedBinary)data).decorated).getFile();
        assertTrue(file.exists());
        data.close();
        assertFalse(file.exists());
    }

    @Test
    public void testBuildString() throws Exception {
        Charset charset = Charset.forName("US-ASCII");
        BinaryBuilder builder = new BinaryBuilder();
        builder.charset(charset);
        builder.append("HELLO");
        Binary binary = builder.build();
        assertEquals(charset, binary.getCharset());
        assertEquals("HELLO", binary.asString());
    }

    @Test
    public void testEmpty() throws Exception {
        BinaryBuilder builder1 = new BinaryBuilder();
        Binary binary1 = builder1.build();
        assertEquals(0, binary1.length());

        Charset charset = Charset.forName("US-ASCII");
        BinaryBuilder builder2 = new BinaryBuilder();
        builder2.charset(charset);
        Binary binary2 = builder2.build();
        assertEquals(0, binary2.length());
        assertEquals(charset, binary2.getCharset());
    }
}
