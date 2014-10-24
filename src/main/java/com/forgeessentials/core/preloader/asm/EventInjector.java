/*
 * Copyright 2014 ServerTools (licensed to FE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.forgeessentials.core.preloader.asm;

import com.forgeessentials.core.preloader.FEPreLoader;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Method replacement for adding FE hooks. Likely to be temporary until Forge gets its act together.
 */
public class EventInjector implements IClassTransformer{

    private static final Map<String, ClassPatch> classPatches = new THashMap<>();

    public static final List<String> injectedPatches = new ArrayList<>();

    public static void addClassPatch(ClassPatch classPatch) {
        classPatches.put(classPatch.targetClass, classPatch);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if (classPatches.containsKey(transformedName)) {
            ClassPatch cp = classPatches.get(transformedName);
            ClassNode cn = new ClassNode();
            ClassReader cr = new ClassReader(bytes);
            cr.accept(cn, 0);


            Iterator<MethodNode> iter = cn.methods.iterator();

            while (iter.hasNext()) {
                MethodNode mn = iter.next();
                for (MethodMapping mm : cp.methodMappings) {
                    if (mm.getName().equals(mn.name) && mm.desc.equals(mn.desc)) {
                        iter.remove();
                    }
                }
            }

            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);

            for (MethodMapping mm : cp.methodMappings) {
                mm.defineMethod(cw);
                injectedPatches.add(mm.friendlyName);
            }

            System.out.println("Injected patches for class " + transformedName);

            return cw.toByteArray();
        }

        return bytes;
    }


    public static class ClassPatch {
        public final String targetClass;

        public final Set<MethodMapping> methodMappings = new THashSet<>();

        public ClassPatch(String targetClass) {
            this.targetClass = targetClass;
        }
    }

    public abstract static class MethodMapping {

        public final String srgName;
        public final String mcpName;
        public final String desc;
        public final String friendlyName;

        public MethodMapping(String srgName, String mcpName, String desc, String friendlyName) {
            this.srgName = srgName;
            this.mcpName = mcpName;
            this.desc = desc;
            this.friendlyName = friendlyName;
        }

        public String getName() {
            if (FEPreLoader.runtimeDeobfEnabled)
                return srgName;
            else
                return mcpName;
        }

        public abstract void defineMethod(ClassWriter classWriter);
    }
}
