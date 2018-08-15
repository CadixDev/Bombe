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

package me.jamiemansfield.bombe.type.signature;

import com.google.common.base.MoreObjects;
import me.jamiemansfield.bombe.type.FieldType;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a field within a class, by its name and descriptor.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public class FieldSignature extends MemberSignature {

    private final FieldType type;

    /**
     * Creates a field signature, with the given name and type.
     *
     * @param name The name of the field
     * @param type The type of the field
     */
    public FieldSignature(final String name, final FieldType type) {
        super(name);
        this.type = type;
    }

    /**
     * Creates a field signature, with the given name and type.
     *
     * @param name The name of the field
     * @param type The type of the field
     */
    public FieldSignature(final String name, final String type) {
        this(name, FieldType.of(type));
    }

    /**
     * Gets the {@link FieldType} of the field, if present.
     *
     * @return The field's type, wrapped in an {@link Optional}
     */
    public Optional<FieldType> getType() {
        return Optional.ofNullable(this.type);
    }

    @Override
    protected MoreObjects.ToStringHelper buildToString() {
        return super.buildToString()
                .add("type", this.type);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FieldSignature)) return false;
        final FieldSignature that = (FieldSignature) obj;
        if (this.type != null && that.type != null) {
            return Objects.equals(this.name, that.name) &&
                    Objects.equals(this.type, that.type);
        }
        else {
            return Objects.equals(this.name, that.name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

}
