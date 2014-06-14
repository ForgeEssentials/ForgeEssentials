package com.forgeessentials.util.tasks;

//Depreciated

public interface ITickTask {
    abstract void tick();

    abstract void onComplete();

    abstract boolean isComplete();

    abstract boolean editsBlocks();
}
