package com.forgeessentials.core.preloader;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.forgeessentials.core.preloader.asminjector.ASMClassWriter;
import com.forgeessentials.core.preloader.asminjector.ASMUtil;
import com.forgeessentials.core.preloader.asminjector.ClassInjector;

public class EventTransformer implements IClassTransformer
{

    public static final boolean isObfuscated = !((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));

    private ClassInjector attackEntityFromInjector;

    private ClassInjector testInjector;

    public EventTransformer()
    {
        testInjector = ClassInjector.create("com.forgeessentials.core.preloader.injections.MixinTest", isObfuscated);
        attackEntityFromInjector = ClassInjector.create("com.forgeessentials.core.preloader.injections.MixinEntity", isObfuscated);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null)
            return null;
        ClassNode classNode = ASMUtil.loadClassNode(bytes);
        boolean transformed = false;

        // Apply transformers
        transformed |= testInjector == null ? false : testInjector.inject(classNode);
        transformed |= attackEntityFromInjector.inject(classNode);

        if (!transformed)
            return bytes;
        ClassWriter writer = new ASMClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}