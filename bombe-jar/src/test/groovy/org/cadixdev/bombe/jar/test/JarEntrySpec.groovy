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

package org.cadixdev.bombe.jar.test

import org.cadixdev.bombe.jar.AbstractJarEntry
import org.cadixdev.bombe.jar.JarClassEntry
import org.cadixdev.bombe.jar.JarResourceEntry
import spock.lang.Specification

/**
 * Tests for Bombe's jar tooling.
 */
class JarEntrySpec extends Specification {

    private static final AbstractJarEntry PACKAGED_ENTRY = new JarResourceEntry("pack/beep.boop", 0, null)
    private static final AbstractJarEntry ROOT_ENTRY = new JarResourceEntry("beep.boop", 0, null)
    private static final AbstractJarEntry VERSION_BY_PATH = new JarClassEntry("META-INF/versions/9/module-info.class", 0, null)
    private static final AbstractJarEntry VERSION_EXPLICIT = new JarClassEntry(11, "pack/a/b.class", 0, null)
    private static final AbstractJarEntry VERSION_MALFORMED = new JarClassEntry("META-INF/versions/ab/module-info.class", 0, null)
    private static final AbstractJarEntry VERSION_UNVERSIONABLE = new JarClassEntry("META-INF/versions/14/META-INF/services/a.b\$Provider", 0, null)

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

    def "handles multirelease paths correctly"(final AbstractJarEntry entry,
                                                final String fullName,
                                                final int version,
                                                final String name) {
        expect:
        entry.name == fullName
        entry.version == version
        entry.unversionedName == name

        where:
        entry                 | fullName                                               | version                      | name
        PACKAGED_ENTRY        | "pack/beep.boop"                                       | AbstractJarEntry.UNVERSIONED | "pack/beep.boop"
        VERSION_BY_PATH       | "META-INF/versions/9/module-info.class"                | 9                            | "module-info.class"
        VERSION_EXPLICIT      | "META-INF/versions/11/pack/a/b.class"                  | 11                           | "pack/a/b.class"
        VERSION_MALFORMED     | "META-INF/versions/ab/module-info.class"               | AbstractJarEntry.UNVERSIONED | "META-INF/versions/ab/module-info.class"
        VERSION_UNVERSIONABLE | "META-INF/versions/14/META-INF/services/a.b\$Provider" | AbstractJarEntry.UNVERSIONED | "META-INF/versions/14/META-INF/services/a.b\$Provider"
    }

}
