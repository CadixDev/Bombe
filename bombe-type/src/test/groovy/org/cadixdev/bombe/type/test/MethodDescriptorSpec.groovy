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

package org.cadixdev.bombe.type.test

import org.cadixdev.bombe.type.ArrayType
import org.cadixdev.bombe.type.BaseType
import org.cadixdev.bombe.type.FieldType
import org.cadixdev.bombe.type.MethodDescriptor
import org.cadixdev.bombe.type.ObjectType
import org.cadixdev.bombe.type.Type
import org.cadixdev.bombe.type.VoidType
import spock.lang.Specification

/**
 * Tests for Bombe's model of a method descriptor.
 */
class MethodDescriptorSpec extends Specification {

    private static final FieldType STRING = new ObjectType('java/lang/String')

    def "throw exception on invalid descriptor"(final String raw) {
        when:
        MethodDescriptor.of(raw)

        then:
        thrown(IllegalStateException)

        where:
        raw    | _
        '(V)V' | _ // Void is not a field type, and therefor an invalid param
        '(I)H' | _ // H isn't a type
    }

    def "reads with no params"() {
        given:
        def desc = MethodDescriptor.of("()V")

        expect:
        desc.paramTypes.size() == 0
        desc.returnType == VoidType.INSTANCE
    }

    def "reads with params"(final String raw, final List<Type> params, final Type ret) {
        given:
        def desc = MethodDescriptor.of(raw)

        expect:
        desc.paramTypes == params
        desc.returnType == ret

        where:
        raw                                       | params                           | ret
        // Array params
        '([Ljava/lang/String;)V'                  | [new ArrayType(1, STRING)]       | VoidType.INSTANCE
        '([[I)V'                                  | [new ArrayType(2, BaseType.INT)] | VoidType.INSTANCE
        // Object params
        '(Ljava/lang/String;Ljava/lang/String;)V' | [STRING, STRING]                 | VoidType.INSTANCE
        // Base params
        '(II)V'                                   | [BaseType.INT, BaseType.INT]     | VoidType.INSTANCE
    }

}
