package com.forgeessentials.core.preloader.asm;

import com.forgeessentials.core.preloader.asm.forge.network_NetHandlerPlayServer;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Method replacement for adding FE hooks. Likely to be temporary until Forge gets its act together.
 * Originally by matthewprenger.
 */
public class EventInjector implements IClassTransformer{

    public static final Set<EventInjector.PatchNote> patches = new HashSet<>();

    private static final Logger log = LogManager.getLogger();
    private static final FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

    static{
        EventInjector.PatchNote nhps = new EventInjector.PatchNote("net.minecraft.network.NetHandlerPlayServer", network_NetHandlerPlayServer.class.getName());
        nhps.addMethodToPatch(new EventInjector.MethodNote("processUpdateSign", "func_147343_a", "(Lnet/minecraft/network/play/client/C12PacketUpdateSign;)V"));
        addPatch(nhps);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if (bytes == null)
            return null;

        log.trace("Class: {} | Transformed: {}", name, transformedName);

        for (PatchNote patchNote : patches) {
            if (patchNote.sourceClass.equals(transformedName)) {
                log.info("Found Class To Patch, Name:{}, TransformedName:{}", name, transformedName);
                return transform(name, patchNote, bytes);
            }
        }

        return bytes;
    }

    private static byte[] transform(String obfName, PatchNote patchNote, byte[] bytes) {

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        if (patchNote.methodsToPatch.isEmpty())
            return bytes;

        for (MethodNote methodNote : patchNote.methodsToPatch) {

            MethodNode sourceMethod = null;
            MethodNode replacementMethod = null;

            try {

                for (MethodNode method : classNode.methods) {
                    if (methodNote.srgMethodName.equals(remapper.mapMethodName(obfName, method.name, method.desc))) {
                        log.trace("Found Method to Patch: {}@{}", method.name, method.desc);
                        sourceMethod = method;
                        break;
                    } else if (methodNote.methodName.equals(method.name) && methodNote.deobfDesc.equals(method.desc)) {
                        log.trace("Found Deobfuscated Method to Patch: {}@{}", method.name, method.desc);
                        sourceMethod = method;
                    }
                }


                ClassNode replacementClass = loadClass(patchNote.replacementClass);
                for (MethodNode method : replacementClass.methods) {
                    if (methodNote.srgMethodName.equals(remapper.mapMethodName(patchNote.replacementClass, method.name, method.desc))) {
                        log.trace("Found Replacement Method: {}@{}", method.name, method.desc);
                        replacementMethod = method;
                        break;
                    } else if (methodNote.methodName.equals(method.name) && methodNote.deobfDesc.equals(method.desc)) {
                        log.trace("Found Deobfuscated Replacement Method: {}@{}", method.name, method.desc);
                        replacementMethod = method;
                        break;
                    }
                }
            } catch (Throwable t) {
                log.warn("Failed to Map Replacement Method: {}", methodNote.methodName, t);
            }

            if (sourceMethod != null && replacementMethod != null) {
                log.info("Successfully Mapped Method to be Replaced");
                log.debug("  Source: {}@{} Replacement: {}@{}", sourceMethod.name, sourceMethod.desc, replacementMethod.name, replacementMethod.desc);
                classNode.methods.remove(sourceMethod);
                classNode.methods.add(replacementMethod);

            } else {
                log.info("Couldn't match methods to patch, skipping");
                return bytes;
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    public static void addPatch(PatchNote patchNote) {

        log.trace("Registering ASM Patch: {}", patchNote.sourceClass);
        log.trace("  Replacement: {}", patchNote.replacementClass);

        for (MethodNote note : patchNote.methodsToPatch) {
            log.trace("  Method: {}", note.methodName);
            log.trace("  SRG: {}", note.srgMethodName);
        }

        patches.add(patchNote);
    }

    private static ClassNode loadClass(String className) throws IOException {

        LaunchClassLoader loader = (LaunchClassLoader) EventInjector.class.getClassLoader();
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(loader.getClassBytes(className));
        classReader.accept(classNode, 0);
        return classNode;
    }

    public static class PatchNote {

        public final String sourceClass;
        public final String replacementClass;

        public final Set<MethodNote> methodsToPatch = new HashSet<>();

        public PatchNote(String sourceClass, String replacementClass) {
            this.sourceClass = sourceClass;
            this.replacementClass = replacementClass;
        }

        public void addMethodToPatch(MethodNote methodNote) {

            methodsToPatch.add(methodNote);
        }
    }

    public static class MethodNote {

        public final String methodName;
        public final String srgMethodName;
        public final String deobfDesc;

        public MethodNote(String methodName, String srgMethodName, String deobfDesc) {

            this.methodName = methodName;
            this.srgMethodName = srgMethodName;
            this.deobfDesc = deobfDesc;
        }
    }
}
