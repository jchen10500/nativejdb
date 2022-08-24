/*
 * Copyright (C) 2022 IBM Corporation
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License v2 with Classpath Exception.
 * The text of the license is available in the file LICENSE.TXT.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See LICENSE.TXT for more details.
 */

package jdwp;

import gdb.mi.service.command.output.MIFrame;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tests the Translator class for normalizing C/C++ and Java signatures.
 */
public class TestTranslator {

    /**
     * Tests that Java and GDB MIFrame both normalize to the same filename.
     */
    @Test
    public void testNormalizeName() {

        String gdbFilename = "HelloMethod/HelloMethod.java";
        String javaNameWithParams = "HelloMethod/HelloMethod::hello(I)";
        String javaNameWithoutParams = "HelloMethod/HelloMethod::hello()";
        String expected = "HelloMethod.HelloMethod";

        assertEquals("Java and GDB signature of class method should be normalized to the same string",
                expected,
                Translator.normalizeMethodName(gdbFilename, false));

        assertEquals("Java and GDB signature of class method should be normalized to the same string",
                expected,
                Translator.normalizeMethodName(javaNameWithParams, true));

        assertEquals("Java and GDB signature of class method should be normalized to the same string",
                expected,
                Translator.normalizeMethodName(javaNameWithoutParams, true));
    }
}
