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

import java.util.Arrays;

/**
 * Represents a primitive type within Java.
 *
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-BaseType">BaseType</a>
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public enum PrimitiveType implements Type {

    BYTE('B'),
    CHAR('C'),
    DOUBLE('D'),
    FLOAT('F'),
    INT('I'),
    LONG('J'),
    SHORT('S'),
    BOOLEAN('Z'),
    ;

    private final char key;
    private final String obfuscatedView;

    /**
     * Creates a new primitive type, with the given character type.
     *
     * @param key The character key
     */
    PrimitiveType(final char key) {
        this.key = key;
        this.obfuscatedView = "" + key;
    }

    @Override
    public String toString() {
        return this.obfuscatedView;
    }

    /**
     * Establishes whether the given key, is a valid primitive
     * key.
     *
     * @param key The key
     * @return {@code True} if the key represents a primitive,
     *         {@code false} otherwise
     */
    public static boolean isValidPrimitive(final char key) {
        return Arrays.stream(values())
                .anyMatch(type -> type.key == key);
    }

    /**
     * Gets the {@link PrimitiveType} from the given key.
     *
     * @param key The key
     * @return The primitive type
     */
    public static PrimitiveType getFromKey(final char key) {
        return Arrays.stream(values())
                .filter(type -> type.key == key)
                .findFirst().orElse(null);
    }

}
