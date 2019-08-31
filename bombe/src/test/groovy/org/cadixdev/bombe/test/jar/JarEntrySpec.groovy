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

package org.cadixdev.bombe.test.jar

import org.cadixdev.bombe.jar.AbstractJarEntry
import org.cadixdev.bombe.jar.JarResourceEntry
import spock.lang.Specification

/**
 * Tests for Bombe's jar tooling.
 */
class JarEntrySpec extends Specification {

    private static final AbstractJarEntry PACKAGED_ENTRY = new JarResourceEntry("pack/beep.boop", 0, null)
    private static final AbstractJarEntry ROOT_ENTRY = new JarResourceEntry("beep.boop", 0, null)

    def "reads name correctly"(final AbstractJarEntry entry,
                               final String packageName,
                               final String simpleName,
                               final String extension) {
        expect:
        entry.package == packageName
        entry.simpleName == simpleName
        entry.extension == extension

        where:
        entry          | packageName | simpleName | extension
        PACKAGED_ENTRY | 'pack'      | 'beep'     | 'boop'
        ROOT_ENTRY     | ''          | 'beep'     | 'boop'
    }

}
