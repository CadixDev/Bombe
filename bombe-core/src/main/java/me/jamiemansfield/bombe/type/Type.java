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

import java.util.regex.Pattern;

/**
 * Represents a type within Java.
 *
 * @see BaseType
 * @see ObjectType
 * @see ArrayType
 * @see VoidType
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface Type {

    Pattern DOT_PATTERN = Pattern.compile(".", Pattern.LITERAL);

    /**
     * Gets the appropriate {@link Type} for the given type.
     *
     * @param type The type
     * @return The type
     */
    static Type of(final String type) {
        if (type.startsWith("L")) {
            // Remove off the 'L' and the ';'.
            return new ObjectType(type.substring(1, type.length() - 1));
        }
        else if (type.startsWith("[")) {
            // Get the array dimensions count
            final int arrayDims = type.lastIndexOf('[') + 1;
            return new ArrayType(arrayDims, Type.of(type.substring(arrayDims)));
        }
        else if (type.length() == 1 && BaseType.isValidBase(type.charAt(0))) {
            return BaseType.getFromKey(type.charAt(0));
        }
        else if (type.length() == 1 && type.charAt(0) == 'V') {
            return VoidType.INSTANCE;
        }
        throw new RuntimeException("Invalid type: " + type);
    }

    /**
     * Gets the appropriate {@link Type} for the given class.
     *
     * @param klass The class
     * @return The type
     */
    static Type of(final Class<?> klass) {
        if (klass.isPrimitive()) {
            if (klass == Boolean.TYPE) {
                return BaseType.BOOLEAN;
            }
            else if (klass == Character.TYPE) {
                return BaseType.CHAR;
            }
            else if (klass == Byte.TYPE) {
                return BaseType.BYTE;
            }
            else if (klass == Short.TYPE) {
                return BaseType.SHORT;
            }
            else if (klass == Integer.TYPE) {
                return BaseType.INT;
            }
            else if (klass == Long.TYPE) {
                return BaseType.LONG;
            }
            else if (klass == Float.TYPE) {
                return BaseType.FLOAT;
            }
            else if (klass == Double.TYPE) {
                return BaseType.DOUBLE;
            }
            else if (klass == Void.TYPE) {
                return VoidType.INSTANCE;
            }
            else {
                throw new RuntimeException("Invalid primitive type: " + klass.getName());
            }
        }
        else {
            return new ObjectType(DOT_PATTERN.matcher(klass.getName()).replaceAll("/"));
        }
    }

}
