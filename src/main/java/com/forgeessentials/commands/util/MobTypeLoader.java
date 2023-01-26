package com.forgeessentials.commands.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.Type;

import net.minecraft.entity.passive.TameableEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forgespi.language.ModFileScanData;

import com.forgeessentials.api.EnumMobType;
import com.forgeessentials.api.EnumMobType.FEMob;
import com.forgeessentials.api.EnumMobType.FEMob.IsTamed;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.collect.Maps;

public class MobTypeLoader
{
    private static final Type MOD = Type.getType(FEMob.class);
    
    public static void preLoad(FMLCommonSetupEvent event)
    {
        LoggingHandler.felog.info("Discovering and loading FEMob data...");
        // started ASM handling for the module loading.
        //Set<ASMData> data = event.getAsmData().getAll(FEMob.class.getName());

        final List<ModFileScanData.AnnotationData> data = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> MOD.equals(a.getAnnotationType()))
                .collect(Collectors.toList());

        Map<Type, String> classModIds = Maps.newHashMap();

        // Gather all @FEModule classes
        data.stream().filter(a -> MOD.equals(a.getAnnotationType())).forEach(info -> classModIds.put(info.getClassType(), (String)info.getAnnotationData().get("value")));
        LoggingHandler.felog.info("Found {} FEMob annotations", data.size());
        
        String className;
        EnumMobType type;
        for (ModFileScanData.AnnotationData asm : data)
        {
            Class<?> c = null;
            className = asm.getClass().getName();

            try
            {
                c = Class.forName(className);
            }
            catch (Exception e)
            {
                LoggingHandler.felog.info("Error trying to load " + asm.getClass() + " as a FEMob!");
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

            if (TameableEntity.class.isAssignableFrom(c))
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
                        throw new RuntimeException(m.getName() + " in " + className + " must take no parameters or arguments!");
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
                throw new RuntimeException(className + " MUST have an elemnt marked @isTamed! Override an inhertied method even!");
            }

            // add to list and return...
            MobTypeRegistry.addMob(type, className, isTameableName);
        }
    }
}
