package com.forgeessentials.worldborder;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FEChunkLoader;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.tasks.ITickTask;
import com.forgeessentials.util.tasks.TaskRegistry;

/**
 * Does the actual filling, with limited chuncks per tick.
 *
 * @author Dries007
 */

@SaveableObject
public class TickTaskFill implements ITickTask {
    final ClassContainer con = new ClassContainer(TickTaskFill.class);
    @SaveableField
    public int speed = 1;
    public WorldBorder border;
    @UniqueLoadingKey
    @SaveableField
    String dimID;
    boolean isComplete = false;
    WorldServer world;
    int minX;
    int minZ;
    int maxX;
    int maxZ;
    int centerX;
    int centerZ;
    int rad;
    @SaveableField
    long ticks = 0L;
    @SaveableField
    long todo = 0L;
    MinecraftServer server = MinecraftServer.getServer();
    ICommandSender source;
    boolean stopped = false;
    @SaveableField
    private int X;
    @SaveableField
    private int Z;

    private TickTaskFill(IReconstructData tag)
    {
        X = (Integer) tag.getFieldValue("X");
        Z = (Integer) tag.getFieldValue("Z");
        speed = (Integer) tag.getFieldValue("speed");
        ticks = (Long) tag.getFieldValue("ticks");
        todo = (Long) tag.getFieldValue("todo");
    }

    public TickTaskFill(WorldServer worldToFill, ICommandSender sender, boolean restart)
    {
        dimID = worldToFill.provider.dimensionId + "";
        FEChunkLoader.instance().forceLoadWorld(worldToFill);

        if (CommandFiller.map.containsKey(worldToFill.provider.dimensionId))
        {
            OutputHandler.chatError(server, "Already running a filler for dim " + dimID + "!");
            return;
        }

        source = sender;
        world = worldToFill;
        border = ModuleWorldBorder.borderMap.get(APIRegistry.perms.getWorldZone(world).getName());

        if (border.shapeByte == 0 || border.rad == 0)
        {
            OutputHandler.chatError(sender, "You need to set the worldborder first!");
            return;
        }

        X = minX = (border.center.getX() - border.rad - ModuleWorldBorder.overGenerate) / 16;
        Z = minZ = (border.center.getZ() - border.rad - ModuleWorldBorder.overGenerate) / 16;
        maxX = (border.center.getX() + border.rad + ModuleWorldBorder.overGenerate) / 16;
        maxZ = (border.center.getZ() + border.rad + ModuleWorldBorder.overGenerate) / 16;
        centerX = border.center.getX() / 16;
        centerZ = border.center.getZ() / 16;
        rad = (border.rad + ModuleWorldBorder.overGenerate) / 16;

        todo = border.getETA();

        OutputHandler.debug("Filler for :" + world.provider.dimensionId);
        OutputHandler.debug("MinX=" + minX + " MaxX=" + maxX);
        OutputHandler.debug("MinZ=" + minZ + " MaxZ=" + maxZ);

        if (restart)
        {
            TickTaskFill saved = (TickTaskFill) DataStorageManager.getReccomendedDriver().loadObject(con, worldToFill.provider.dimensionId + "");
            if (saved != null)
            {
                OutputHandler.chatWarning(source, "Found a stopped filler. Will resume that one.");
                X = saved.X;
                Z = saved.Z;
                speed = saved.speed;
                ticks = saved.ticks;
                todo = saved.todo;
            }
        }

        TaskRegistry.registerTask(this);

        OutputHandler.chatWarning(source, "This filler will take about " + getETA() + " at current speed.");
    }

    @Reconstructor
    private static TickTaskFill reconstruct(IReconstructData tag)
    {
        return new TickTaskFill(tag);
    }

    private String getETA()
    {
        try
        {
            return FunctionHelper.parseTime((int) (todo / speed / FunctionHelper.getTPS()));
        }
        catch (Exception e)
        {
            return "";
        }
    }

    @Override
    public void tick()
    {
        ticks++;

        if (ticks % (20 * 25) == 0)
        {
            ChatUtils.sendMessage(source, "Filler for " + dimID + ": " + getStatus());
        }

        for (int i = 0; i < speed; i++)
        {
            try
            {
                Chunk chunk = world.theChunkProviderServer.loadChunk(X, Z);
                chunk.setChunkModified();
                world.theChunkProviderServer.safeSaveChunk(chunk);
                world.theChunkProviderServer.unloadQueuedChunks();
                world.theChunkProviderServer.unloadChunksIfNotNearSpawn(X, Z);

                todo--;

                next();
                if (isComplete())
                {
                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void next()
    {
        // 1 = square
        if (border.shapeByte == 1)
        {
            if (X <= maxX)
            {
                X++;
            }
            else
            {
                if (Z <= maxZ)
                {
                    X = minX;
                    Z++;
                }
                else
                {
                    isComplete = true;
                }
            }
        }
        // 2 = round
        else if (border.shapeByte == 2)
        {
            while (true)
            {
                if (X <= maxX)
                {
                    X++;
                }
                else
                {
                    if (Z <= maxZ)
                    {
                        X = minX;
                        Z++;
                    }
                    else
                    {
                        isComplete = true;
                    }
                }

                if (rad >= ModuleWorldBorder.getDistanceRound(centerX, centerZ, X, Z))
                {
                    break;
                }
            }
        }
        else
        {
            isComplete = true;
            throw new RuntimeException("WTF?" + border.shapeByte);
        }
    }

    @Override
    public void onComplete()
    {
        try
        {
            boolean var6 = world.levelSaving;
            world.levelSaving = false;
            world.saveAllChunks(true, (IProgressUpdate) null);
            world.levelSaving = var6;
        }
        catch (MinecraftException var7)
        {
        }
        if (!stopped)
        {
            OutputHandler.chatWarning(source, "Filler finished after " + ticks + " ticks.");
            System.out.print("Removed filler? :" + DataStorageManager.getReccomendedDriver().deleteObject(con, dimID));
        }
        CommandFiller.map.remove(Integer.parseInt(dimID));
        FEChunkLoader.instance().unforceLoadWorld(world);
        System.gc();
    }

    @Override
    public boolean isComplete()
    {
        return isComplete;
    }

    @Override
    public boolean editsBlocks()
    {
        return true;
    }

    public void stop()
    {
        stopped = true;
        isComplete = true;
        DataStorageManager.getReccomendedDriver().saveObject(con, this);
        OutputHandler.chatWarning(source, "Filler stopped after " + ticks + " ticks. Still to do: " + todo + " chuncks.");
        System.gc();
    }

    public String getStatus()
    {
        return "Todo: " + getETA() + " at " + speed + " chuncks per ticks.";
    }

    public void speed(int speed)
    {
        this.speed = speed;
        OutputHandler.chatWarning(source, "Changed speed of filler " + dimID + " to " + speed);
    }
}
