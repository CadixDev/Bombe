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

import me.jamiemansfield.bombe.type.signature.FieldSignature;
import me.jamiemansfield.bombe.type.signature.MethodSignature;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A simple implementation of {@link InheritanceProvider} based on Java's
 * reflection API.
 *
 * @author Minecrell
 * @since 0.3.0
 */
public class ReflectionInheritanceProvider implements InheritanceProvider {

    private final ClassLoader classLoader;

    public ReflectionInheritanceProvider(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Optional<ClassInfo> provide(String klass) {
        try {
            return Optional.of(provide(Class.forName(klass.replace('/', '.'), false, this.classLoader)));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClassInfo> provide(String klass, Object context) {
        if (context instanceof Class) {
            // Avoid looking up class if it is provided in context
            return Optional.of(provide((Class<?>) context));
        } else {
            return provide(klass);
        }
    }

    public ClassInfo provide(Class<?> clazz) {
        return new ReflectionClassInfo(clazz).lazy();
    }

    private static class ReflectionClassInfo extends ClassInfo.Abstract {

        private final Class<?> clazz;

        private ReflectionClassInfo(Class<?> clazz) {
            this.clazz = clazz;
        }

        private static String getInternalName(Class<?> clazz) {
            return clazz.getName().replace('.', '/');
        }

        @Override
        public String getName() {
            return getInternalName(this.clazz);
        }

        @Override
        public boolean isInterface() {
            return this.clazz.isInterface();
        }

        @Override
        public String getSuperName() {
            Class<?> superClass = this.clazz.getSuperclass();
            return superClass != null ? getInternalName(superClass) : "";
        }

        @Override
        public List<String> getInterfaces() {
            return Collections.unmodifiableList(Arrays.stream(this.clazz.getInterfaces())
                    .map(ReflectionClassInfo::getInternalName)
                    .collect(Collectors.toList()));
        }

        @Override
        public Map<FieldSignature, InheritanceType> getFields() {
            return Collections.unmodifiableMap(Arrays.stream(this.clazz.getDeclaredFields())
                    .collect(Collectors.toMap(FieldSignature::of, f -> InheritanceType.fromModifiers(f.getModifiers()))));
        }

        @Override
        public Map<MethodSignature, InheritanceType> getMethods() {
            return Collections.unmodifiableMap(Arrays.stream(this.clazz.getDeclaredMethods())
                    .collect(Collectors.toMap(MethodSignature::of, m -> InheritanceType.fromModifiers(m.getModifiers()))));
        }

        private void provideParent(InheritanceProvider provider, Class<?> parent, Collection<ClassInfo> parents) {
            if (parent == null) {
                return;
            }

            ClassInfo parentInfo = provider.provide(getInternalName(parent), parent).orElse(null);
            if (parentInfo != null) {
                parentInfo.provideParents(provider, parents);
                parents.add(parentInfo);
            }
        }

        @Override
        public void provideParents(InheritanceProvider provider, Collection<ClassInfo> parents) {
            provideParent(provider, this.clazz.getSuperclass(), parents);
            for (Class<?> iface : this.clazz.getInterfaces()) {
                provideParent(provider, iface, parents);
            }
        }

    }

}
