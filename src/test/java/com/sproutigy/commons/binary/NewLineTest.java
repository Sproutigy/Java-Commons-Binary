package com.sproutigy.commons.binary;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author LukeAheadNET
 */
public class NewLineTest {
    @Test
    public void test() {
        assertEquals(System.lineSeparator(), NewLine.LOCAL);
        assertEquals("Hello\r\nWorld", NewLine.normalize("Hello\nWorld", NewLine.WINDOWS));
        assertEquals("Hello\nWorld", NewLine.normalize("Hello\r\nWorld", NewLine.UNIX));
        assertEquals("Hello\nWorld\nGoodbye\nWorld", NewLine.normalize("Hello\rWorld\r\nGoodbye\nWorld", NewLine.MAC));


        String s = "Line 1" + NewLine.WINDOWS + "Line 2" + NewLine.UNIX + "Line 3" + NewLine.DEPRECATED + "Line 4" + NewLine.LOCAL;
        String t = NewLine.normalize(s, NewLine.UNIX);
        assertFalse(t.contains("\r\n"));
        assertFalse(t.contains("\r"));
        assertTrue(t.contains("\n"));
    }


}
