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

package me.jamiemansfield.bombe.asm.analysis;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.io.ByteStreams;
import me.jamiemansfield.bombe.analysis.InheritanceProvider;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.util.Optional;

/**
 * An {@link InheritanceProvider} that obtains all of its information
 * from a given {@link ClassLoader}.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public class ClassLoaderInheritanceProvider implements InheritanceProvider {

    private final LoadingCache<String, ClassInfo> cache;

    public ClassLoaderInheritanceProvider(final ClassLoader classLoader) {
        this.cache = Caffeine.newBuilder()
                .build(key -> {
                    final String internalName = key + ".class";

                    try (final InputStream in = classLoader.getResourceAsStream(internalName)) {
                        if (in == null) return null;

                        // I read the class using ASM as getting the information required using
                        // reflection is awkward.
                        // Additionally, it allows me to share code - which is always a positive!
                        final ClassReader reader = new ClassReader(ByteStreams.toByteArray(in));
                        final ClassNode node = new ClassNode();
                        reader.accept(node, 0);

                        return new ClassNodeClassInfo(node);
                    }
                });
    }

    @Override
    public Optional<ClassInfo> provide(final String klass) {
        return Optional.ofNullable(this.cache.get(klass));
    }

}
