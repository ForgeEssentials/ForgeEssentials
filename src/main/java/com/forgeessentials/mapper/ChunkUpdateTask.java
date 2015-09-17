package com.forgeessentials.mapper;

public class ChunkUpdateTask implements Runnable
{

    private final boolean isFinished;

    private final int chunkX;

    private final int chunkZ;

    public ChunkUpdateTask(int chunkX, int chunkZ)
    {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.isFinished = false;
    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub

    }

    public boolean isFinished()
    {
        return isFinished;
    }

    public int getChunkX()
    {
        return chunkX;
    }

    public int getChunkZ()
    {
        return chunkZ;
    }

}
