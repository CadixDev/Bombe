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

package org.cadixdev.bombe.test.type

import org.cadixdev.bombe.type.ArrayType
import org.cadixdev.bombe.type.BaseType
import org.cadixdev.bombe.type.FieldType
import org.cadixdev.bombe.type.ObjectType
import org.cadixdev.bombe.type.Type
import org.cadixdev.bombe.type.VoidType
import spock.lang.Specification

/**
 * Tests for the Bombe type model.
 */
class TypeSpec extends Specification {

    def "reads array type"(final String raw, final int dimCount, final Type component) {
        given:
        def type = Type.of(raw)

        expect:
        type instanceof ArrayType
        final ArrayType arrayType = (ArrayType) type
        type.toString() == raw
        arrayType.dimCount == dimCount
        arrayType.component == component

        where:
        raw                     | dimCount | component
        '[[I'                   | 2        | BaseType.INT
        '[[[Ljava/lang/String;' | 3        | new ObjectType('java/lang/String')
    }

    def "reads object type"(final String raw) {
        given:
        def type = Type.of(raw)

        expect:
        type instanceof ObjectType
        type.toString() == raw

        where:
        raw                              | _
        'Ljava/lang/String;'             | _
        'Lorg/cadixdev/demo/Test$Inner;' | _
    }

    def "reads base type"(final String raw, final BaseType expected) {
        given:
        def type = Type.of(raw)

        expect:
        type instanceof BaseType
        type.toString() == raw
        type == expected

        where:
        raw | expected
        'B' | BaseType.BYTE
        'C' | BaseType.CHAR
        'D' | BaseType.DOUBLE
        'F' | BaseType.FLOAT
        'I' | BaseType.INT
        'J' | BaseType.LONG
        'S' | BaseType.SHORT
        'Z' | BaseType.BOOLEAN
    }

    def "reads void type"() {
        given:
        def type = Type.of("V")

        expect:
        type instanceof VoidType
        type.toString() == "V"
        type == VoidType.INSTANCE
    }

    def "normalises class names"(final String source, final String binary) {
        given:
        def type = new ObjectType(source)

        expect:
        type.className == binary

        where:
        source                         | binary
        'java.lang.String'             | 'java/lang/String'
        'org.cadixdev.demo.Test$Inner' | 'org/cadixdev/demo/Test$Inner'
    }

    def "throw exception on bad types"(final String raw) {
        when:
        Type.of(raw)

        then:
        thrown(IllegalStateException)

        where:
        raw                | _
        'java/lang/String' | _
        'H'                | _
    }

    def "throw exception on bad field types"(final String raw) {
        when:
        FieldType.of(raw)

        then:
        thrown(IllegalStateException)

        where:
        raw | _
        'V' | _
    }

    def "type from class"(final Class<?> raw, final Type expected) {
        given:
        def type = Type.of(raw)

        expect:
        type == expected

        where:
        raw            | expected
        String.class   | new ObjectType("java/lang/String")
        Boolean.TYPE   | BaseType.BOOLEAN
        Character.TYPE | BaseType.CHAR
        Byte.TYPE      | BaseType.BYTE
        Short.TYPE     | BaseType.SHORT
        Integer.TYPE   | BaseType.INT
        Long.TYPE      | BaseType.LONG
        Float.TYPE     | BaseType.FLOAT
        Double.TYPE    | BaseType.DOUBLE
        Void.TYPE      | VoidType.INSTANCE
    }

}
