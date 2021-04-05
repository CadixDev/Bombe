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

package org.cadixdev.bombe.jar.asm;

import static java.util.jar.Attributes.Name.MAIN_CLASS;

import org.cadixdev.bombe.jar.JarClassEntry;
import org.cadixdev.bombe.jar.JarEntryTransformer;
import org.cadixdev.bombe.jar.JarManifestEntry;
import org.cadixdev.bombe.jar.JarResourceEntry;
import org.cadixdev.bombe.jar.JarServiceProviderConfigurationEntry;
import org.cadixdev.bombe.jar.ServiceProviderConfiguration;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.jar.Attributes;
import java.util.stream.Collectors;

/**
 * An implementation of {@link JarEntryTransformer} for remapping classes
 * using a {@link Remapper}.
 *
 * @author Jamie Mansfield
 * @since 0.3.0
 */
public class JarEntryRemappingTransformer implements JarEntryTransformer {

    private static final Attributes.Name SHA_256_DIGEST = new Attributes.Name("SHA-256-Digest");

    private final Remapper remapper;
    private final BiFunction<ClassVisitor, Remapper, ClassRemapper> clsRemapper;

    public JarEntryRemappingTransformer(final Remapper remapper, final BiFunction<ClassVisitor, Remapper, ClassRemapper> clsRemapper) {
        this.remapper = remapper;
        this.clsRemapper = clsRemapper;
    }

    public JarEntryRemappingTransformer(final Remapper remapper) {
        this(remapper, ClassRemapper::new);
    }

    @Override
    public JarClassEntry transform(final JarClassEntry entry) {
        // Remap the class
        final ClassReader reader = new ClassReader(entry.getContents());
        final ClassWriter writer = new ClassWriter(reader, 0);
        reader.accept(this.clsRemapper.apply(
                writer,
                this.remapper
        ), 0);

        // Create the jar entry
        final String originalName = entry.getUnversionedName().substring(0, entry.getUnversionedName().length() - ".class".length());
        final String name = this.remapper.map(originalName) + ".class";
        return new JarClassEntry(entry.getVersion(), name, entry.getTime(), writer.toByteArray());
    }

    @Override
    public JarManifestEntry transform(final JarManifestEntry entry) {
        // Remap the Main-Class attribute, if present
        if (entry.getManifest().getMainAttributes().containsKey(MAIN_CLASS)) {
            final String mainClassObf = entry.getManifest().getMainAttributes().getValue(MAIN_CLASS)
                    .replace('.', '/');
            final String mainClassDeobf = this.remapper.map(mainClassObf)
                    .replace('/', '.');

            // Since Manifest is mutable, we needn't create a new entry \o/
            entry.getManifest().getMainAttributes().put(MAIN_CLASS, mainClassDeobf);
        }

        // Remove all signature entries
        for (final Iterator<Map.Entry<String, Attributes>> it = entry.getManifest().getEntries().entrySet().iterator(); it.hasNext();) {
            final Map.Entry<String, Attributes> section = it.next();
            if (section.getValue().remove(SHA_256_DIGEST) != null) {
                if (section.getValue().isEmpty()) {
                    it.remove();
                }
            }
        }

        return entry;
    }

    @Override
    public JarServiceProviderConfigurationEntry transform(final JarServiceProviderConfigurationEntry entry) {
        // Remap the Service class
        final String obfServiceName = entry.getConfig().getService()
                .replace('.', '/');
        final String deobfServiceName = this.remapper.map(obfServiceName)
                .replace('/', '.');

        // Remap the Provider classes
        final List<String> deobfProviders = entry.getConfig().getProviders().stream()
                .map(provider -> provider.replace('.', '/'))
                .map(this.remapper::map)
                .map(provider -> provider.replace('/', '.'))
                .collect(Collectors.toList());

        // Create the new entry
        final ServiceProviderConfiguration config = new ServiceProviderConfiguration(deobfServiceName, deobfProviders);
        return new JarServiceProviderConfigurationEntry(entry.getTime(), config);
    }

    @Override
    public JarResourceEntry transform(final JarResourceEntry entry) {
        // Strip signature files from metadata
        if (entry.getName().startsWith("META-INF")) {
            if (entry.getExtension().equals("RSA")
                || entry.getExtension().equals("SF")) {
                return null;
            }
        }
        return entry;
    }

}
