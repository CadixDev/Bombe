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

package me.jamiemansfield.bombe.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.jamiemansfield.bombe.type.signature.MethodSignature;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public abstract class ReflectionInheritanceProviderTest {

    protected final InheritanceProvider provider;

    protected ReflectionInheritanceProviderTest(InheritanceProvider provider) {
        this.provider = provider;
    }

    @Test
    public void testObject(InheritanceProvider provider) {
        final InheritanceProvider.ClassInfo info = provider.provide("java/lang/Object").get();
        assertEquals(info.getName(), "java/lang/Object");
        assertEquals(info.getPackage(), "java/lang");
        assertFalse(info.isInterface());
        assertEquals(info.getSuperName(), "");
        assertEquals(info.getInterfaces(), Collections.emptyList());
    }

    @Test
    public void testList(InheritanceProvider provider) {
        final InheritanceProvider.ClassInfo info = provider.provide("java/util/List").get();
        assertEquals(info.getName(), "java/util/List");
        assertEquals(info.getPackage(), "java/util");
        assertTrue(info.isInterface());
        assertEquals(info.getSuperName(), "");
        assertEquals(info.getInterfaces(), Collections.singletonList("java/util/Collection"));

        assertEquals(info.getMethods().get(MethodSignature.of("isEmpty()Z")), InheritanceType.PUBLIC);
    }

    @Test
    public void testArrayList(InheritanceProvider provider) {
        final InheritanceProvider.ClassInfo info = provider.provide("java/util/ArrayList").get();
        assertEquals(info.getName(), "java/util/ArrayList");
        assertEquals(info.getPackage(), "java/util");
        assertEquals(info.getSuperName(), "java/util/AbstractList");

        // Interfaces should not be recursive
        assertFalse(info.getInterfaces().contains("java/util/Collection"));
        assertTrue(info.getInterfaces().contains("java/util/RandomAccess"));

        assertEquals(info.getMethods().get(MethodSignature.of("grow(I)V")), InheritanceType.NONE);
    }

}
