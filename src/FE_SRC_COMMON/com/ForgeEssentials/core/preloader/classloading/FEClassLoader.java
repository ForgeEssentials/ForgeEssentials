package com.ForgeEssentials.core.preloader.classloading;

import java.io.File;
import java.net.MalformedURLException;

import net.minecraft.launchwrapper.LaunchClassLoader;

public class FEClassLoader {
	public void runClassLoad(LaunchClassLoader classloader, File root){
		File lib = new File(root, "lib/");
		if (!lib.exists()){
			lib.mkdirs();
		}
		for (File f : lib.listFiles()){
			if (f != null){
			try {
				classloader.addURL(f.toURI().toURL());
				System.out.println("[ForgeEssentials] Loaded library file " + f.getAbsolutePath());
			} catch (MalformedURLException e) {
				throw new RuntimeException("Could not add library file " + f.getAbsolutePath() + ", there may be a classloading problem.");
			}
			}
		}

		File module = new File(root, "modules/");
		if (!module.exists()){
			module.mkdirs();
		}
		for (File f : module.listFiles()){
			if (f != null){
			try{
				classloader.addURL(f.toURI().toURL());
			} catch(MalformedURLException e){
				System.err.println("[ForgeEssentials] Could not add module file " + f.getAbsolutePath() + ", there may be a class loading problem.");
			}
			}
		}
		System.out.println("[ForgeEssentials] Loaded " + module.listFiles().length + " modules");
	}

}
