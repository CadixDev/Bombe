/*
 * Copyright (c) 2018, Jamie Mansfield <https://jamiemansfield.me/>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.jamiemansfield.bombe.test.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.jamiemansfield.bombe.type.ArrayType;
import me.jamiemansfield.bombe.type.BaseType;
import me.jamiemansfield.bombe.type.MethodDescriptor;
import me.jamiemansfield.bombe.type.ObjectType;
import me.jamiemansfield.bombe.type.VoidType;
import org.junit.jupiter.api.Test;

/**
 * Unit tests pertaining to the {@link MethodDescriptor} class.
 */
public final class MethodDescriptorTest {

    @Test
    public void noParams() {
        final String raw = "()V";
        final MethodDescriptor descriptor = MethodDescriptor.compile(raw);
        assertEquals(raw, descriptor.toString());
        assertTrue(descriptor.getParamTypes().isEmpty());
        assertEquals(descriptor.getReturnType(), VoidType.INSTANCE);
    }

    @Test
    public void arrayParam() {
        // array of objects
        {
            final String raw = "([Ljava/lang/String;)V";
            final MethodDescriptor descriptor = MethodDescriptor.compile(raw);
            assertEquals(raw, descriptor.toString());
            assertFalse(descriptor.getParamTypes().isEmpty());
            assertEquals(1, descriptor.getParamTypes().size());
            final ArrayType arr = new ArrayType(1, new ObjectType("java/lang/String"));
            assertEquals(arr, descriptor.getParamTypes().get(0));
            assertEquals(descriptor.getReturnType(), VoidType.INSTANCE);
        }
        // array of bases
        {
            final String raw = "([[I)V";
            final MethodDescriptor descriptor = MethodDescriptor.compile(raw);
            assertEquals(raw, descriptor.toString());
            assertFalse(descriptor.getParamTypes().isEmpty());
            assertEquals(1, descriptor.getParamTypes().size());
            final ArrayType arr = new ArrayType(2, BaseType.INT);
            assertEquals(arr, descriptor.getParamTypes().get(0));
            assertEquals(descriptor.getReturnType(), VoidType.INSTANCE);
        }
    }

    @Test
    public void objectParams() {
        final String raw = "(Ljava/lang/String;Ljava/lang/String;)V";
        final MethodDescriptor descriptor = MethodDescriptor.compile(raw);
        assertEquals(raw, descriptor.toString());
        assertFalse(descriptor.getParamTypes().isEmpty());
        assertEquals(2, descriptor.getParamTypes().size());
        final ObjectType obj = new ObjectType("java/lang/String");
        assertEquals(obj, descriptor.getParamTypes().get(0));
        assertEquals(obj, descriptor.getParamTypes().get(1));
        assertEquals(descriptor.getReturnType(), VoidType.INSTANCE);
    }

    @Test
    public void baseParams() {
        final String raw = "(II)V";
        final MethodDescriptor descriptor = MethodDescriptor.compile(raw);
        assertEquals(raw, descriptor.toString());
        assertFalse(descriptor.getParamTypes().isEmpty());
        assertEquals(2, descriptor.getParamTypes().size());
        assertEquals(BaseType.INT, descriptor.getParamTypes().get(0));
        assertEquals(BaseType.INT, descriptor.getParamTypes().get(1));
        assertEquals(descriptor.getReturnType(), VoidType.INSTANCE);
    }

    @Test
    public void invalidDescriptors() {
        // Void is not a FieldType, and cannot be used as a method parameter
        assertThrows(RuntimeException.class, () -> MethodDescriptor.compile("(V)V"));
    }

}
