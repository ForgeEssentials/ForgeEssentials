package com.ForgeEssentials.util.tasks;

//Depreciated
import java.io.Serializable;

public interface ITickTask
{
	abstract void tick();

	abstract void onComplete();

	abstract boolean isComplete();

	abstract boolean editsBlocks();
}
