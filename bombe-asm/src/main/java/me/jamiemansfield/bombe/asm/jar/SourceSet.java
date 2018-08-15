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

package me.jamiemansfield.bombe.asm.jar;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a container for a set of {@link ClassNode}s.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public final class SourceSet {

    private final Map<String, ClassNode> classes = new HashMap<>();

    public SourceSet() {
    }

    /**
     * Adds the given {@link ClassNode} to the source set.
     *
     * @param node The class node
     */
    public void add(final ClassNode node) {
        this.classes.put(node.name, node);
    }

    /**
     * Gets all of the {@link ClassNode}s loaded in the source set.
     *
     * @return The classes
     */
    public Collection<ClassNode> getClasses() {
        return this.classes.values();
    }

    /**
     * Gets the {@link ClassNode} of the given name.
     *
     * @param className The class name
     * @return The class node, or null should one not exists of
     *         the given class name
     */
    public ClassNode get(final String className) {
        return this.classes.get(className);
    }

    public boolean has(final String className) {
        return this.classes.containsKey(className);
    }

    /**
     * Accepts the given {@link ClassVisitor} on all {@link ClassNode}s
     * loaded by the source set.
     *
     * @param visitor The class visitor
     */
    public void accept(final ClassVisitor visitor) {
        this.classes.values()
                .forEach(node -> node.accept(visitor));
    }

}
