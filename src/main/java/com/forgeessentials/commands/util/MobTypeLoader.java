package com.forgeessentials.commands.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.objectweb.asm.Type;

import com.forgeessentials.api.EnumMobType;
import com.forgeessentials.api.EnumMobType.FEMob;
import com.forgeessentials.api.EnumMobType.FEMob.IsTamed;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;

public class MobTypeLoader
{
    private static final Type MOD = Type.getType(FEMob.class);

    public static void init()
    {
        LoggingHandler.felog.info("Discovering and loading FEMob data...");
        // started ASM handling for the module loading.
        // Set<ASMData> data = event.getAsmData().getAll(FEMob.class.getName());

        final List<ModFileScanData.AnnotationData> data = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations).flatMap(Collection::stream)
                .filter(a -> MOD.equals(a.annotationType())).collect(Collectors.toList());

        LoggingHandler.felog.info("Found {} FEMob annotations", data.size());

        String className;
        EnumMobType type;
        for (ModFileScanData.AnnotationData asm : data)
        {
            Class<?> c = null;
            className = asm.memberName();

            try
            {
                c = Class.forName(className);
            }
            catch (Exception e)
            {
                LoggingHandler.felog.info("Error trying to load " + asm.memberName() + " as a FEMob!");
                e.printStackTrace();
                return;
            }

            FEMob annot = c.getAnnotation(FEMob.class);
            if (annot == null)
            {
                throw new IllegalArgumentException(c.getName() + " doesn't have the @FEMob annotation!");
            }

            type = annot.type();
            if (type != EnumMobType.TAMEABLE)
            {
                // for all the others.. return...
                MobTypeRegistry.addMob(type, className);
                continue;
            }

            // continue cuz its a tameable...

            if (TamableAnimal.class.isAssignableFrom(c))
            {
                // do NOT add to the map.. its unnecessary...
                continue;
            }

            String isTameableName = null;

            // check teh fields
            for (Field f : c.getDeclaredFields())
            {
                if (f.isAnnotationPresent(IsTamed.class))
                {
                    if (isTameableName != null)
                    {
                        throw new RuntimeException("Two elements in " + className + " cannot be marked @IsTamed!");
                    }
                    else if (!f.getType().equals(boolean.class))
                    {
                        throw new RuntimeException(f.getName() + " in " + className + " must be of type boolean!");
                    }
                    else if (Modifier.isStatic(f.getModifiers()))
                    {
                        throw new RuntimeException(f.getName() + " in " + className + " cannot be static!");
                    }

                    isTameableName = f.getName();
                }
            }

            // check the methods...
            for (Method m : c.getDeclaredMethods())
            {
                if (m.isAnnotationPresent(IsTamed.class))
                {
                    if (isTameableName != null)
                    {
                        throw new RuntimeException("Two elements in " + className + " cannot be marked @IsTamed!");
                    }
                    else if (!m.getReturnType().equals(boolean.class))
                    {
                        throw new RuntimeException(m.getName() + " in " + className + " must return a boolean!");
                    }
                    else if (m.getParameterTypes().length > 0)
                    {
                        throw new RuntimeException(
                                m.getName() + " in " + className + " must take no parameters or arguments!");
                    }
                    else if (Modifier.isStatic(m.getModifiers()))
                    {
                        throw new RuntimeException(m.getName() + " in " + className + " cannot be static!");
                    }

                    isTameableName = m.getName() + "()";
                }
            }

            if (isTameableName == null)
            {
                throw new RuntimeException(
                        className + " MUST have an elemnt marked @isTamed! Override an inhertied method even!");
            }

            // add to list and return...
            MobTypeRegistry.addMob(type, className, isTameableName);
        }
    }
}
