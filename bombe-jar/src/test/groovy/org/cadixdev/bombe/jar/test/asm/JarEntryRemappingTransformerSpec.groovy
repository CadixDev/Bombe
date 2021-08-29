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

package org.cadixdev.bombe.jar.test.asm

import org.cadixdev.bombe.jar.asm.JarEntryRemappingTransformer
import org.cadixdev.bombe.jar.JarClassEntry
import org.cadixdev.bombe.jar.JarManifestEntry
import org.cadixdev.bombe.jar.JarServiceProviderConfigurationEntry
import org.cadixdev.bombe.jar.ServiceProviderConfiguration
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.Remapper
import org.objectweb.asm.tree.ClassNode
import spock.lang.Specification

import java.util.jar.Attributes
import java.util.jar.Manifest

/**
 * Tests for Bombe's ASM tooling for the jar tooling.
 */
class JarEntryRemappingTransformerSpec extends Specification {

    private static final Remapper REMAPPER = new Remapper() {
        @Override
        String map(final String internalName) {
            if ('a' == internalName) return 'pkg/Demo'
            if ('b' == internalName) return 'pkg/DemoTwo'
            return internalName
        }
    }
    private static final JarEntryRemappingTransformer TRANSFORMER =
            new JarEntryRemappingTransformer(REMAPPER)

    def "remaps class"() {
        given:
        // Create a test class
        def obf = new ClassWriter(0)
        obf.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, 'a', null, 'java/lang/Object', null)

        // Run it through the transformer
        def entry = TRANSFORMER.transform(new JarClassEntry('a.class', 0, obf.toByteArray()))

        // Use a ClassNode for convenience
        def node = new ClassNode()
        def reader = new ClassReader(entry.contents)
        reader.accept(node, 0)

        expect:
        entry.name == 'pkg/Demo.class'
        node.name == 'pkg/Demo'
    }

    def "remaps multi-release class"() {
        given:
        // Create a test class
        def obf = new ClassWriter(0)
        obf.visit(Opcodes.V9, Opcodes.ACC_PUBLIC, 'a', null, 'java/lang/Object', null)

        // Run it through the transformer
        def entry = TRANSFORMER.transform(new JarClassEntry('META-INF/versions/9/a.class', 0, obf.toByteArray()))

        // Use a ClassNode for convenience
        def node = new ClassNode()
        def reader = new ClassReader(entry.contents)
        reader.accept(node, 0)

        expect:
        entry.name == 'META-INF/versions/9/pkg/Demo.class'
        entry.version == 9
        entry.unversionedName == 'pkg/Demo.class'
        node.name == 'pkg/Demo'

    }

    def "remaps manifest"() {
        given:
        // Create a test Manifest
        def obf = new Manifest()
        obf.with {
            mainAttributes[name('Manifest-Version')] = '1.0'
            mainAttributes[name('Main-Class')] = 'a'
        }

        // Run it through the transformer
        def deobf = TRANSFORMER.transform(new JarManifestEntry(0, obf)).manifest

        expect:
        deobf.mainAttributes[name('Main-Class')] == 'pkg.Demo'
    }

    def "remaps simple manifest"() {
        given:
        // Create a test Manifest
        def obf = new Manifest()
        obf.with {
            mainAttributes[name('Manifest-Version')] = '1.0'
        }

        // Run it through the transformer
        TRANSFORMER.transform(new JarManifestEntry(0, obf)).manifest
    }

    def "remaps service config"() {
        given:
        // Create a test service config
        def obf = new ServiceProviderConfiguration('a', ['b'])

        // Run it through the transformer
        def deobf = TRANSFORMER.transform(new JarServiceProviderConfigurationEntry(0, obf)).config

        expect:
        deobf.service == 'pkg.Demo'
        deobf.providers.size() == 1
        deobf.providers == ['pkg.DemoTwo']
    }

    static def name(final String name) {
        new Attributes.Name(name)
    }

}
