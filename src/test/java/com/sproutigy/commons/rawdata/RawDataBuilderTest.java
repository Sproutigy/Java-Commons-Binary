package com.sproutigy.commons.rawdata;

import com.sproutigy.commons.rawdata.impl.ByteArrayRawData;
import com.sproutigy.commons.rawdata.impl.TempFileRawData;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author LukeAheadNET
 */
public class RawDataBuilderTest {

    @Test
    public void testBuildSmallData() throws IOException {
        RawDataBuilder builder = new RawDataBuilder().append("HELLO", "UTF-8").append((byte) 0);
        assertEquals(6, builder.length());
        RawData data = builder.build();
        assertEquals(6, data.length());
        assertEquals(true, data instanceof ByteArrayRawData);
    }

    @Test
    public void testBuildBigData() throws Exception {
        int length = 1000;
        RawDataBuilder builder = new RawDataBuilder(0, 500, 1000);
        for (int i=0; i<length; i++) {
            builder.write(i % 256);
        }
        assertEquals(length, builder.length());
        RawData data = builder.build();
        assertEquals(length, data.length());
        assertEquals(true, data instanceof TempFileRawData);
        File file = ((TempFileRawData)data).getFile();
        assertTrue(file.exists());
        data.close();
        assertFalse(file.exists());
    }
}
