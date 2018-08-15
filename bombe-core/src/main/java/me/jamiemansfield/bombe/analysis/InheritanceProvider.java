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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * An inheritance provider stores inheritance information on classes, which
 * will be obtained upon request (if not present in the cache) as opposed
 * to all in one bulk operation.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface InheritanceProvider {

    /**
     * Gets the class information for the given class name, if available.
     *
     * @param klass The class name
     * @return The class information wrapped in an {@link Optional}
     */
    Optional<ClassInfo> provide(final String klass);

    /**
     * A wrapper used to store inheritance information about classes.
     */
    interface ClassInfo {

        /**
         * Gets the name of the class.
         *
         * @return The class' name
         */
        String getName();

        /**
         * Gets the name of this class' super class.
         *
         * @return The super name
         */
        String getSuperName();

        /**
         * Gets an immutable-view of all the interfaces of the class.
         *
         * @return The class' interfaces
         */
        List<String> getInterfaces();

        /**
         * Gets an immutable-view of all the fields of the class.
         *
         * @return The class' fields
         */
        List<FieldSignature> getFields();

        /**
         * Gets an immutable-view of all the methods.
         *
         * @return The methods
         */
        List<MethodSignature> getMethods();

        /**
         * A default implementation of {@link ClassInfo}.
         */
        abstract class Impl implements ClassInfo {

            protected final String name;
            protected final String superName;
            protected final List<String> interfaces = new ArrayList<>();
            protected final List<FieldSignature> fields = new ArrayList<>();
            protected final List<MethodSignature> methods = new ArrayList<>();

            protected Impl(final String name, final String superName) {
                this.name = name;
                this.superName = superName;
            }

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            public String getSuperName() {
                return this.superName;
            }

            @Override
            public List<String> getInterfaces() {
                return Collections.unmodifiableList(this.interfaces);
            }

            @Override
            public List<FieldSignature> getFields() {
                return Collections.unmodifiableList(this.fields);
            }

            @Override
            public List<MethodSignature> getMethods() {
                return Collections.unmodifiableList(this.methods);
            }

        }

    }

}
