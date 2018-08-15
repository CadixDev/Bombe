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

package me.jamiemansfield.bombe.asm.jar;

import com.google.common.io.ByteStreams;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.jar.JarFile;

/**
 * An implementation of {@link Walker} for walking a jar file.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public class JarWalker implements Walker {

    private final File jarFile;

    /**
     * Creates a new jar walker, from the given {@link Path}.
     *
     * @param jarPath The path of the jar
     */
    public JarWalker(final Path jarPath) {
        this(jarPath.toFile());
    }

    /**
     * Creates a new jar walker, from the given {@link File}.
     *
     * @param jarFile The file of the jar
     */
    public JarWalker(final File jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public void walk(final SourceSet sourceSet) {
        try (final JarFile jarFile = new JarFile(this.jarFile)) {
            walk(jarFile, sourceSet);
        } catch (final IOException ex) {
            System.err.println("Failed to read the jar file!");
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Walks through the given source, and loads the {@link ClassNode}s
     * into the given {@link SourceSet}.
     *
     * @param jarFile The jar file to walk
     * @param sourceSet The source set
     */
    public static void walk(final JarFile jarFile, final SourceSet sourceSet) {
        jarFile.stream()
                // Filter out directories
                .filter(entry -> !entry.isDirectory())
                // I only want to get classes
                .filter(entry -> entry.getName().endsWith(".class"))
                // Now to read the class
                .forEach(entry -> {
                    try (final InputStream in = jarFile.getInputStream(entry)) {
                        final ClassReader reader = new ClassReader(ByteStreams.toByteArray(in));
                        final ClassNode node = new ClassNode();
                        reader.accept(node, 0);
                        sourceSet.add(node);
                    } catch (final IOException ex) {
                        System.err.println("Failed to get an input stream for " + entry.getName() + "!");
                        ex.printStackTrace(System.err);
                    }
                });
    }

}
