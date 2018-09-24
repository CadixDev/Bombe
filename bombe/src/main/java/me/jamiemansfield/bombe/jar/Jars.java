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

package me.jamiemansfield.bombe.jar;

import me.jamiemansfield.bombe.util.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

/**
 * Utilities for working with jar files.
 *
 * @author Jamie Mansfield
 * @since 0.3.0
 */
public final class Jars {

    public static Stream<AbstractJarEntry> walk(final Path jarPath) throws IOException {
        try (final JarFile jarFile = new JarFile(jarPath.toFile())) {
            return walk(jarFile);
        }
    }

    public static Stream<AbstractJarEntry> walk(final JarFile jarFile) {
        return jarFile.stream().filter(entry -> !entry.isDirectory()).map(entry -> {
            final String name = entry.getName();
            try (final InputStream stream = jarFile.getInputStream(entry)) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ByteStreams.copy(stream, baos);

                if (entry.getName().endsWith(".class")) {
                    return new JarClassEntry(name, baos.toByteArray());
                }
                else {
                    return new JarResourceEntry(name, baos.toByteArray());
                }
            }
            catch (final IOException ignored) {
                // TODO: handle?
                return null;
            }
        });
    }

    public static JarOutputStream transform(final JarOutputStream jos, final JarFile jarFile, final JarEntryTransformer... transformers) {
        final JarEntryTransformer masterTransformer = new JarEntryTransformer() {
            @Override
            public JarClassEntry transform(final JarClassEntry entry) {
                JarClassEntry lastEntry = entry;
                for (final JarEntryTransformer transformer : transformers) {
                    lastEntry = entry.accept(transformer);
                }
                return lastEntry;
            }

            @Override
            public JarResourceEntry transform(final JarResourceEntry entry) {
                JarResourceEntry lastEntry = entry;
                for (final JarEntryTransformer transformer : transformers) {
                    lastEntry = entry.accept(transformer);
                }
                return lastEntry;
            }
        };

        final Set<String> packages = new HashSet<>();
        walk(jarFile)
                .map(entry -> entry.accept(masterTransformer))
                .forEach(entry -> {
                    try {
                        if (!packages.contains(entry.getPackage())) {
                            packages.add(entry.getPackage());
                            jos.putNextEntry(new JarEntry(entry.getPackage() + "/"));
                        }

                        entry.write(jos);
                    }
                    catch (final IOException ex) {
                        ex.printStackTrace();
                        // todo:
                    }
                });

        return jos;
    }

    private Jars() {
    }

}
