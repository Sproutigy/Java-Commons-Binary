package com.sproutigy.commons.binary;

import com.sproutigy.commons.binary.impl.ByteArrayBinary;
import com.sproutigy.commons.binary.impl.TempFileBinary;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author LukeAheadNET
 */
public class BinaryBuilderTest {

    @Test
    public void testBuildSmallData() throws IOException {
        BinaryBuilder builder = new BinaryBuilder().append("HELLO", "UTF-8").append((byte) 0);
        assertEquals(6, builder.length());
        Binary data = builder.build();
        assertEquals(6, data.length());
        assertEquals(true, data instanceof ByteArrayBinary);
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
        assertEquals(true, data instanceof TempFileBinary);
        File file = ((TempFileBinary)data).getFile();
        assertTrue(file.exists());
        data.close();
        assertFalse(file.exists());
    }
}
