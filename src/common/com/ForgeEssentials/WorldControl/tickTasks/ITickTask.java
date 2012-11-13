package com.ForgeEssentials.WorldControl.tickTasks;

import java.io.Serializable;

public interface ITickTask extends Serializable
{
	abstract void tick();
	
	abstract boolean isComplete();
	
	abstract boolean editsBlocks();
	
	abstract boolean equals(Object obj);
}
