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

package org.cadixdev.bombe.jar;

import org.cadixdev.bombe.util.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities for working with jar files.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public final class Jars2 {

    public static Stream<AbstractJarEntry> walk(final FileSystem fs) throws IOException {
        final Path root = fs.getPath("/");

        final List<AbstractJarEntry> entries = new ArrayList<>();

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                entries.add(read(file));
                return FileVisitResult.CONTINUE;
            }
        });

        return entries.stream();
    }

    /**
     * Creates an {@link AbstractJarEntry} for a given path.
     *
     * @param entry The path of the jar entry
     * @return The jar entry
     * @throws IOException Should the entry fail to read
     */
    public static AbstractJarEntry read(final Path entry) throws IOException {
        try (final InputStream is = Files.newInputStream(entry)) {
            final String name = entry.toString().substring(1); // Remove '/' prefix
            final long time = Files.getLastModifiedTime(entry).toMillis();

            if (Objects.equals("META-INF/MANIFEST.MF", name)) {
                return new JarManifestEntry(time, new Manifest(is));
            }
            else if (name.startsWith("META-INF/services/")) {
                final String serviceName = name.substring("META-INF/services/".length());

                final ServiceProviderConfiguration config = new ServiceProviderConfiguration(serviceName);
                config.read(is);
                return new JarServiceProviderConfigurationEntry(time, config);
            }

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteStreams.copy(is, baos);

            if (name.endsWith(".class")) {
                return new JarClassEntry(name, time, baos.toByteArray());
            }
            else {
                return new JarResourceEntry(name, time, baos.toByteArray());
            }
        }
    }

    public static void transform(final FileSystem input, final Path output, final JarEntryTransformer... transformers) throws IOException {
        final URI uri = URI.create("jar:" + output.toUri());
        final Map<String, String> options = new HashMap<String, String>() {
            {
                this.put("create", "true");
            }
        };

        try (final FileSystem fs = FileSystems.newFileSystem(uri, options)) {
            for (final AbstractJarEntry entry : walk(input)
                    .map(entry -> {
                        for (final JarEntryTransformer transformer : transformers) {
                            entry = entry.accept(transformer);
                        }
                        return entry;
                    }).collect(Collectors.toList())) {
                final Path path = fs.getPath("/", entry.getName());

                // create parent directories, if required
                if (Files.notExists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }

                try (final OutputStream os = Files.newOutputStream(path)) {
                    os.write(entry.getContents());
                }
            }
        }
    }

    private Jars2() {
    }

}
