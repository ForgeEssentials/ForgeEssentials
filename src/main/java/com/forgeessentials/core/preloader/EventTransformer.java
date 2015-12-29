package com.forgeessentials.core.preloader;

import net.minecraft.entity.Entity;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityAttackedEvent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class EventTransformer implements IClassTransformer
{

    public static final String attackEntityFrom = TransformerUtil.isObfuscated ? "func_70097_a" : "attackEntityFrom";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null)
            return null;
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
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
        MethodNode attackEntityFromMethod = TransformerUtil.findMethod(classNode, attackEntityFrom, "(Lnet/minecraft/util/DamageSource;F)Z");
        if (attackEntityFromMethod == null)
            return false;
        // System.out.println(String.format("Patching attackEntityFrom event into %s", classNode.name));
        TransformerUtil.insertCodeFromMethod(attackEntityFromMethod, attackEntityFromMethod.instructions.getFirst(), getClass().getName(), "attackEntityFrom_event");
        return true;
    }

    protected void attackEntityFrom_event(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> ci)
    {
        EntityAttackedEvent event = new EntityAttackedEvent((Entity) (Object) this, damageSource, damage);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            ci.setReturnValue(event.result);
        }
        damage = event.damage;
    }

}