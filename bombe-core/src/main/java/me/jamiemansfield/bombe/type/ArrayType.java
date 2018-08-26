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
import java.util.Objects;

/**
 * Represents an array type within Java.
 *
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-ArrayType">ArrayType</a>
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public class ArrayType implements FieldType {

    private final int dimCount;
    private final String arrayDims;
    private final Type component;
    private final String descriptor;

    /**
     * Creates a new array type, of the specified array
     * dimensions, and {@link Type} component.
     *
     * @param arrayDims The array dimensions count
     * @param component The component type
     */
    public ArrayType(final int arrayDims, final Type component) {
        this.dimCount = arrayDims;

        char[] dims = new char[arrayDims];
        Arrays.fill(dims, '[');
        this.arrayDims = new String(dims);

        this.component = component;
        this.descriptor = this.arrayDims + component.toString();
    }

    /**
     * Gets the dimension count of the array.
     *
     * @return The dimension count
     */
    public int getDimCount() {
        return this.dimCount;
    }

    /**
     * Gets the {@link Type} of the array.
     *
     * @return The array's type
     */
    public Type getComponent() {
        return this.component;
    }

    @Override
    public String toString() {
        return this.descriptor;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ArrayType)) return false;
        final ArrayType that = (ArrayType) obj;
        return Objects.equals(this.arrayDims, that.arrayDims) &&
                Objects.equals(this.component, that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.arrayDims, this.component);
    }

}
