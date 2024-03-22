package com.forgeessentials.multiworld.v2.provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class ProvidersReflection {
	public static ChunkGenerator getChunkProvider(String className, Class<?>[] classes, Object[] initargs){
		try {
			Class<?> clazz = Class.forName(className);
	    	Constructor<?> ctor = clazz.getConstructor(classes);
	    	Object object = ctor.newInstance(initargs);
	    	if(object instanceof ChunkGenerator) {
	    		return (ChunkGenerator) object;
	    	}
	    	LoggingHandler.felog.debug("[Multiworld] Null ChunkGenerator:");
	    	return null;
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
    }

    public static BiomeSource getBiomeProvider(String className, Class<?>[] classes, Object[] initargs){
		try {
			Class<?> clazz = Class.forName(className);
	    	Constructor<?> ctor = clazz.getConstructor(classes);
	    	Object object = ctor.newInstance(initargs);
	    	if(object instanceof BiomeSource) {
	    		return (BiomeSource) object;
	    	}
	    	LoggingHandler.felog.debug("[Multiworld] Null BiomeProvider:");
	    	return null;
	    	//throw new RuntimeException("RecievedProvider "+object.getClass().toString()+"Is not assignable from net.minecraft.world.biome.provider.BiomeProvider");
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
    }
}
