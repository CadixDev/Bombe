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

package org.cadixdev.bombe.jar;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * Represents an entry within a jar file.
 *
 * @author Jamie Mansfield
 * @since 0.3.0
 */
public abstract class AbstractJarEntry {

    /**
     * A {@link #getVersion()} value for jar entries that are at the base
     * version in the jar.
     */
    public static final int UNVERSIONED = -1;

    private static final String META_INF = "META-INF/";
    private static final String VERSIONS_PREFIX = META_INF + "versions/";

    protected final String name;
    protected final long time;
    private String unversionedName;
    private int version = UNVERSIONED;
    private String packageName;
    private String simpleName;

    /**
     * Create a new jar entry for a specific multi-release variant.
     *
     * <p>If {@code unversionedName} starts with {@code META-INF}, it will be
     * treated as being in the base version no matter what value is provided for
     * {@code version}, to match the behavior of the JDK's {@link JarFile}.</p>
     *
     * @param version the Java version number to associate this entry with
     * @param unversionedName the name without any versioned prefix
     * @param time the time the entry was created at
     */
    protected AbstractJarEntry(final int version, final String unversionedName, final long time) {
        if (version == UNVERSIONED || unversionedName.startsWith(META_INF)) {
            this.name = unversionedName;
        } else {
            this.version = version;
            this.unversionedName = unversionedName;
            this.name = VERSIONS_PREFIX + version + '/' + unversionedName;
        }
        this.time = time;
    }

    protected AbstractJarEntry(final String name, final long time) {
        this.name = name;
        this.time = time;
    }

    /**
     * Gets the fully-qualified name of the jar entry.
     *
     * <p>This method does not have any special handling for multi-release jars.</p>
     *
     * @return The name
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the time the jar entry was last modified.
     *
     * @return The time
     */
    public final long getTime() {
        return this.time;
    }

    /**
     * Get the name of this entry, as it will be seen by a multi-release-aware
     * jar handler.
     *
     * <p>When a file path is in the {@code META-INF/versions/} folder but does
     * not provide a valid multi-release version, it will be treated as if it
     * were an ordinary, un-versioned resource.</p>
     *
     * <p>This will always handle multi-release paths, even when the
     * {@code Multi-Release} manifest attribute is set to false.</p>
     *
     * @return the full entry name, without any version prefix
     */
    public final String getUnversionedName() {
        if (this.unversionedName != null) return this.unversionedName;

        if (!this.name.startsWith(VERSIONS_PREFIX)) {
            return this.unversionedName = this.name;
        }
        // <version number>/<path>
        final String trimmed = this.name.substring(VERSIONS_PREFIX.length());
        final int divider = trimmed.indexOf('/');
        if (divider == -1) { // malformed, ignore
            return this.unversionedName = this.name;
        }

        final String version = trimmed.substring(0, divider);
        final String unversioned = trimmed.substring(divider + 1);
        try {
            if (!unversioned.startsWith(META_INF)) { // Files already within META-INF cannot be versioned
                final int parsedVersion = Integer.parseInt(version);
                if (parsedVersion >= 0) {
                    this.version = parsedVersion;
                    return this.unversionedName = unversioned;
                }
            }
        } catch (final NumberFormatException ignored) { // invalid integer, treat as unversioned
            // fall through
        }
        return this.unversionedName = this.name;
    }

    /**
     * Gets the package that contains the jar entry, an empty
     * string if in the root package.
     *
     * @return The package name
     */
    public final String getPackage() {
        if (this.packageName != null) return this.packageName;
        final String name = this.getUnversionedName();
        final int index = name.lastIndexOf('/');
        if (index == -1) return this.packageName = "";
        return this.packageName = name.substring(0, index);
    }

    /**
     * Gets the simple name (without any packages or extension).
     *
     * @return The simple name
     */
    public final String getSimpleName() {
        if (this.simpleName != null) return this.simpleName;
        final int packageLength = this.getPackage().isEmpty() ? -1 : this.getPackage().length();
        final int extensionLength = this.getExtension().isEmpty() ? -1 : this.getExtension().length();
        final String name = this.getUnversionedName();
        return this.simpleName = name.substring(
                packageLength + 1,
                name.length() - (extensionLength + 1)
        );
    }

    /**
     * If this is a multi-release variant of a class file in a multi-release
     * jar, the version associated with this variant.
     *
     * @return the version, or {@link #UNVERSIONED} if this is the base version,
     *     or a file that would not be interpreted as a multi-release variant
     *     within the version folder.
     * @see #getUnversionedName() for a description of the conditions on multi-release jars
     */
    public int getVersion() {
        if (this.unversionedName != null) return this.version;
        this.getUnversionedName(); // initialize versions
        return this.version;
    }

    /**
     * Gets the extension of the jar entry.
     *
     * @return The extension
     */
    public abstract String getExtension();

    /**
     * Gets the contents of the jar entry.
     *
     * @return The contents
     */
    public abstract byte[] getContents();

    /**
     * Writes the jar entry to the given {@link JarOutputStream}.
     *
     * @param jos The jar output stream
     * @throws IOException If an I/O exception occurs
     */
    public final void write(final JarOutputStream jos) throws IOException {
        // Create entry
        final JarEntry entry = new JarEntry(this.name);
        entry.setTime(this.time);

        // Write entry
        jos.putNextEntry(entry);
        jos.write(this.getContents());
        jos.closeEntry();
    }

    /**
     * Processes the jar entry with the given transformer.
     *
     * @param visitor The transformer
     * @return The jar entry
     */
    public abstract AbstractJarEntry accept(final JarEntryTransformer visitor);

}
