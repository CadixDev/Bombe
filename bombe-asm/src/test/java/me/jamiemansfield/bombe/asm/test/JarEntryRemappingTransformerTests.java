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

package me.jamiemansfield.bombe.asm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.jamiemansfield.bombe.asm.jar.JarEntryRemappingTransformer;
import me.jamiemansfield.bombe.jar.JarClassEntry;
import me.jamiemansfield.bombe.jar.JarManifestEntry;
import me.jamiemansfield.bombe.jar.JarServiceProviderConfigurationEntry;
import me.jamiemansfield.bombe.jar.ServiceProviderConfiguration;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.Collections;
import java.util.jar.Manifest;

/**
 * Unit tests pertaining to {@link JarEntryRemappingTransformer}.
 */
public final class JarEntryRemappingTransformerTests {

    private static final Remapper REMAPPER = new Remapper() {
        @Override
        public String map(final String internalName) {
            if ("a".equals(internalName)) return "pkg/Demo";
            if ("b".equals(internalName)) return "pkg/DemoTwo";
            return internalName;
        }
    };
    private static final JarEntryRemappingTransformer TRANSFORMER =
            new JarEntryRemappingTransformer(REMAPPER);

    @Test
    public void remapsClasses() {
        // Create a test class
        final ClassWriter obf = new ClassWriter(0);
        obf.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "a", null, "java/lang/Object", null);

        // Run it through the transformer
        final JarClassEntry entry = TRANSFORMER.transform(new JarClassEntry("a.class", 0, obf.toByteArray()));
        assertEquals("pkg/Demo.class", entry.getName());

        // Verify the contents
        final ClassNode node = new ClassNode();
        final ClassReader reader = new ClassReader(entry.getContents());
        reader.accept(node, 0);
        assertEquals("pkg/Demo", node.name);
    }

    @Test
    public void remapsMainClass() throws IOException {
        final Manifest obfManifest = new Manifest();
        {
            obfManifest.getMainAttributes().putValue("Manifest-Version", "1.0");
            obfManifest.getMainAttributes().putValue("Main-Class", "a");
        }

        final JarManifestEntry manifestEntry = TRANSFORMER.transform(new JarManifestEntry(0, obfManifest));
        final Manifest deobfManifest = manifestEntry.getManifest();
        assertEquals("pkg.Demo", deobfManifest.getMainAttributes().getValue("Main-Class"));
    }

    @Test
    public void remapsConfig() {
        final ServiceProviderConfiguration obfConfig = new ServiceProviderConfiguration(
                "a",
                Collections.singletonList("b")
        );
        final ServiceProviderConfiguration deobfConfig =
                TRANSFORMER.transform(new JarServiceProviderConfigurationEntry(0, obfConfig)).getConfig();
        assertEquals("pkg.Demo", deobfConfig.getService());
        assertEquals(1, deobfConfig.getProviders().size());
        assertTrue(deobfConfig.getProviders().contains("pkg.DemoTwo"), "Provider not present!");
    }

}
