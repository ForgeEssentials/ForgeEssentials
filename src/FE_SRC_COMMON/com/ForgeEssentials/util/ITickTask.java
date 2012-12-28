package com.ForgeEssentials.util;

//Depreciated
import java.io.Serializable;

public interface ITickTask extends Serializable
{
	abstract void tick();
	
	abstract void onComplete();
	
	abstract boolean isComplete();
	
	abstract boolean editsBlocks();
}
