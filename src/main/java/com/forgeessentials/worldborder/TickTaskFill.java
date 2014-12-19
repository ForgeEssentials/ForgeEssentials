package com.forgeessentials.worldborder;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.gen.ChunkProviderServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.commons.SaveableObject;
import com.forgeessentials.commons.SaveableObject.Reconstructor;
import com.forgeessentials.commons.SaveableObject.SaveableField;
import com.forgeessentials.commons.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.v2.DataManager;
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
            TickTaskFill saved = DataManager.getInstance().load(TickTaskFill.class, Integer.toString(worldToFill.provider.dimensionId));
            if (saved == null)
                saved = (TickTaskFill) DataStorageManager.getReccomendedDriver().loadObject(con, Integer.toString(worldToFill.provider.dimensionId));
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
            OutputHandler.chatNotification(source, "Filler for " + dimID + ": " + getStatus());
        }

        for (int i = 0; i < speed; i++)
        {
			/* skip over chunks that already exist (we use the region cache so
			 * as to not load them) */
			while(!RegionFileCache.createOrLoadRegionFile(
					world.getChunkSaveLocation(), X >> 5, Z >> 5)
					.chunkExists(X & 0x1F, Z & 0x1F))
			{
				--todo;
				if (next())
					return;
			}
			
			/* get and populate the chunk manually so that it does not get
			 * fully loaded in, this saves ram and increases performance */
			ChunkProviderServer provider =
					(ChunkProviderServer)world.getChunkProvider();
			
			Chunk chunk = provider.currentChunkProvider.loadChunk(X, Z);
			chunk.populateChunk(provider, provider, X, Z);
			provider.safeSaveChunk(chunk);
			
			--todo;
            if (next())
                return;
        }
    }

    private boolean next()
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
                    return true;
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
                        return true;
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
        
        return false;
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
            System.out.print("Removed filler? :" + DataManager.getInstance().delete(TickTaskFill.class, dimID));
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
        DataManager.getInstance().save(this, dimID);
        DataStorageManager.getReccomendedDriver().saveObject(con, this);
        OutputHandler.chatWarning(source, "Filler stopped after " + ticks + " ticks. Still to do: " + todo + " chuncks.");
        System.gc();
    }

    public String getStatus()
    {
        return "Todo: " + getETA() + " at " + speed + " chuncks per ticks.";
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
        OutputHandler.chatWarning(source, "Changed speed of filler " + dimID + " to " + speed);
    }
}
