package com.forgeessentials.core.preloader;

import net.minecraft.entity.Entity;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityAttackedEvent;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.forgeessentials.core.preloader.ASMInjector.CallbackInfo;

public class EventTransformer implements IClassTransformer
{

    public static final boolean isObfuscated = !((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));

    public static final String attackEntityFrom = isObfuscated ? "func_70097_a" : "attackEntityFrom";

    private ASMInjector attackEntityFromInjector;

    public EventTransformer()
    {
        attackEntityFromInjector = ASMInjector.create(getClass().getName(), "attackEntityFrom_event");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null)
            return null;
        ClassNode classNode = ASMInjector.loadClassNode(bytes);
        boolean transformed = false;

        // Apply transformers
        transformed |= transformAttackEntityFrom(classNode);

        if (!transformed)
            return bytes;
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public boolean transformAttackEntityFrom(ClassNode classNode)
    {
        MethodNode attackEntityFromMethod = ASMInjector.findMethod(classNode, attackEntityFrom, "(Lnet/minecraft/util/DamageSource;F)Z");
        if (attackEntityFromMethod == null)
            return false;
        // System.out.println(String.format("Patching attackEntityFrom event into %s", classNode.name));
        attackEntityFromInjector.inject(attackEntityFromMethod, attackEntityFromMethod.instructions.getFirst());
        return true;
    }

    protected void attackEntityFrom_event(DamageSource damageSource, float damage, CallbackInfo ci)
    {
        EntityAttackedEvent event = new EntityAttackedEvent((Entity) (Object) this, damageSource, damage);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            ci.doReturn(event.result);
        }
        damage = event.damage;
    }

}