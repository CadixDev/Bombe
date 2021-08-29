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

package org.cadixdev.bombe.type;

import me.jamiemansfield.string.StringReader;

/**
 * An {@link StringReader} for reading {@link Type}s from their
 * raw {@link String} representation.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class TypeReader extends StringReader {

    public TypeReader(final String source) {
        super(source);
    }

    /**
     * Reads the next {@link Type} from source.
     *
     * @return The type
     * @throws IllegalStateException If the descriptor is invalid
     */
    public Type readType() {
        // Void Type
        if (this.peek() == 'V') {
            this.advance();
            return VoidType.INSTANCE;
        }

        // Field Type
        return this.readFieldType();
    }

    /**
     * Reads the next {@link FieldType} from source.
     *
     * @return The type
     * @throws IllegalStateException If the descriptor is invalid
     */
    public FieldType readFieldType() {
        // Array Type
        if (this.peek() == '[') {
            return this.readArrayType();
        }

        // Base Type
        if (BaseType.isValidBase(this.peek())) {
            return this.readBaseType();
        }

        // Object Type
        if (this.peek() == 'L') {
            return this.readObjectType();
        }

        throw new IllegalStateException("Invalid descriptor provided!");
    }

    /**
     * Reads the next {@link ArrayType} from source.
     *
     * @return The type
     * @throws IllegalStateException If the descriptor is invalid
     */
    public ArrayType readArrayType() {
        int count = 0;

        while (this.available() && this.peek() == '[') {
            this.advance();
            count++;
        }

        return new ArrayType(count, this.readFieldType());
    }

    /**
     * Reads the next {@link BaseType} from source.
     *
     * @return The type
     * @throws IllegalStateException If the descriptor is invalid
     */
    public BaseType readBaseType() {
        return BaseType.getFromKey(this.advance());
    }

    /**
     * Reads the next {@link ObjectType} from source.
     *
     * @return The type
     * @throws IllegalStateException If the descriptor is invalid
     */
    public ObjectType readObjectType() {
        final int start = this.index();

        if (this.peek() != 'L') throw new IllegalStateException("Incomplete descriptor provided!");
        this.advance();

        while (this.available() && this.peek() != ';') {
            this.advance();
        }

        if (!this.available() || this.peek() != ';') throw new IllegalStateException("Incomplete descriptor provided!");
        this.advance();

        return new ObjectType(this.substring(start + 1, this.index() - 1));
    }

    /**
     * Reads the next {@link PrimitiveType} from source.
     *
     * @return The type
     * @throws IllegalStateException If the descriptor is invalid
     */
    public PrimitiveType readPrimitiveType() {
        // Void Type
        if (this.peek() == 'V') {
            this.advance();
            return VoidType.INSTANCE;
        }

        // Base Type
        if (BaseType.isValidBase(this.peek())) {
            return this.readBaseType();
        }

        throw new IllegalStateException("Invalid descriptor provided!");
    }

}
