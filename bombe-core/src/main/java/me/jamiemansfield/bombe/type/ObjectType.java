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

package me.jamiemansfield.bombe.type;

import me.jamiemansfield.bombe.analysis.InheritanceProvider;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents an object type within Java.
 *
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-ObjectType">ObjectType</a>
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public class ObjectType implements FieldType {

    private static final Pattern DOT_PATTERN = Pattern.compile(".", Pattern.LITERAL);

    private static String normaliseClassName(final String className) {
        return DOT_PATTERN.matcher(className).replaceAll("/");
    }

    private final String className;
    private final String descriptor;

    /**
     * Creates a new object type, of the given class name.
     *
     * @param className The class name
     */
    public ObjectType(final String className) {
        this.className = normaliseClassName(className);
        this.descriptor = "L" + this.className + ";";
    }

    /**
     * Gets the name of the class of the object.
     *
     * @return The class name
     */
    public String getClassName() {
        return this.className;
    }

    @Override
    public boolean isInstanceOf(final Type obj, final InheritanceProvider inheritanceProvider) {
        if (this == obj) return true;
        if (!(obj instanceof ObjectType)) return false;
        final ObjectType that = (ObjectType) obj;
        return this.equals(that) ||
                that.getClassName().equals("java/lang/Object") ||
                inheritanceProvider.getParentsOf(this.className).contains(that.getClassName());
    }

    @Override
    public String toString() {
        return this.descriptor;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ObjectType)) return false;
        final ObjectType that = (ObjectType) obj;
        return Objects.equals(this.className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.className);
    }

}
