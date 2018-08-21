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

import me.jamiemansfield.bombe.util.AbstractReader;

public class TypeReader extends AbstractReader {

    public TypeReader(final String source) {
        super(source);
    }

    public Type readType() {
        // Void Type
        if (this.peek() == 'V') {
            this.advance();
            return VoidType.INSTANCE;
        }

        // Field Type
        return this.readFieldType();
    }

    public FieldType readFieldType() {
        // Array Type
        if (this.peek() == '[') {
            int count = 0;

            while (this.hasNext() && this.peek() == '[') {
                this.advance();
                count++;
            }

            return new ArrayType(count, this.readFieldType());
        }

        // Base Type
        if (BaseType.isValidBase(this.peek())) {
            return BaseType.getFromKey(this.advance());
        }

        // Object Type
        if (this.peek() == 'L') {
            final int start = this.current;
            this.advance();

            while (this.hasNext() && this.peek() != ';') {
                this.advance();
            }

            if (this.peek() != ';') throw new IllegalStateException("Incomplete descriptor provided!");
            this.advance();

            return new ObjectType(this.source.substring(start + 1, this.current - 1));
        }

        throw new IllegalStateException("Invalid descriptor provided!");
    }

}
