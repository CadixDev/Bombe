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

package org.cadixdev.bombe.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility for working with byte streams.
 *
 * @author Kyle Wood
 * @since 0.3.0
 */
public final class ByteStreams {

    /**
     * Copy all of the data from the {@code from} input to the {@code to} output.
     *
     * @param from The input to copy from.
     * @param to The output to copy to.
     * @param buffer The byte array to use as the copy buffer.
     * @throws IOException If an IO error occurs.
     */
    public static void copy(final InputStream from, final OutputStream to, final byte[] buffer) throws IOException {
        int read;
        while ((read = from.read(buffer)) != -1) {
            to.write(buffer, 0, read);
        }
    }

    /**
     * Copy all of the data from the {@code from} input to the {@code to} output,
     * using a default buffer.
     *
     * @param from The input to copy from.
     * @param to The output to copy to.
     * @throws IOException If an IO error occurs.
     */
    public static void copy(final InputStream from, final OutputStream to) throws IOException {
        copy(from, to, new byte[8192]);
    }

    private ByteStreams() {
    }

}
