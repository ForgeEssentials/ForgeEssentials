package com.forgeessentials.commands.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.gen.ChunkProviderServer;

import com.forgeessentials.commands.world.CommandFiller;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TaskRegistry.ITickTask;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.FEChunkLoader;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.worldborder.ModuleWorldBorder;
import com.forgeessentials.worldborder.WorldBorder;

/**
 * Does the actual filling, with limited chuncks per tick.
 */

public class TickTaskFill implements ITickTask
{

    private static final int WORLD_BORDER = 6;

    public ICommandSender sender;

    public int speed = 1;

    private String dimID;

    private long ticks = 0L;

    private long todo = 0L;

    private WorldBorder border;

    private boolean isComplete = false;

    private WorldServer world;

    private int X;
    private int Z;

    private int minX;
    private int minZ;

    private int maxX;
    private int maxZ;

    private int centerX;
    private int centerZ;

    private int rad;

    private MinecraftServer server = MinecraftServer.getServer();

    private boolean stopped = false;

    private Method writeChunkToNBT = null;

    public TickTaskFill(WorldServer worldToFill, ICommandSender sender, boolean restart)
    {
        dimID = worldToFill.provider.dimensionId + "";
        FEChunkLoader.instance().forceLoadWorld(worldToFill);

        if (CommandFiller.map.containsKey(worldToFill.provider.dimensionId))
        {
            OutputHandler.chatError(server, "Already running a filler for dim " + dimID + "!");
            return;
        }

        this.sender = sender;
        world = worldToFill;
        border = ModuleWorldBorder.getInstance().getBorder(world);
        if (border == null || !border.isEnabled())
        {
            OutputHandler.chatError(sender, "You need to set the worldborder first!");
            return;
        }

        centerX = border.getCenter().getX() / 16;
        centerZ = border.getCenter().getZ() / 16;
        X = minX = centerX - border.getSize().getX() / 16 - WORLD_BORDER;
        Z = minZ = centerZ - border.getSize().getZ() / 16 - WORLD_BORDER;
        maxX = centerX + border.getSize().getX() / 16 + WORLD_BORDER;
        maxZ = centerZ + border.getSize().getZ() / 16 + WORLD_BORDER;
        rad = Math.max(border.getSize().getX(), border.getSize().getZ()) / 16 + WORLD_BORDER;

        switch (border.getShape())
        {
        case BOX:
        default:
            todo = (long) Math.pow(rad * 2, 2);
            break;
        case CYLINDER:
        case ELLIPSOID:
            todo = (long) (Math.pow(rad, 2) * Math.PI);
            break;
        }

        OutputHandler.debug("Filler for :" + world.provider.dimensionId);
        OutputHandler.debug("MinX=" + minX + " MaxX=" + maxX);
        OutputHandler.debug("MinZ=" + minZ + " MaxZ=" + maxZ);

        if (restart)
        {
            TickTaskFill saved = DataManager.getInstance().load(TickTaskFill.class, Integer.toString(worldToFill.provider.dimensionId));
            if (saved != null)
            {
                OutputHandler.chatWarning(sender, "Found a stopped filler. Will resume that one.");
                X = saved.X;
                Z = saved.Z;
                speed = saved.speed;
                ticks = saved.ticks;
                todo = saved.todo;
            }
        }

        setupWriteChunkToNBT();
        TaskRegistry.getInstance().schedule(this);

        OutputHandler.chatWarning(sender, String.format("This filler will take about %s at current speed", getETA()));
    }

    private String getETA()
    {
        try
        {
            return OutputHandler.formatTimeDurationReadable((int) (todo / speed / ServerUtil.getTPS()), true);
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
            OutputHandler.chatNotification(sender, "Filler for " + dimID + ": " + getStatus());
        }

        for (int i = 0; i < speed; i++)
        {
            // TODO: Disabled until population works properly

            // // skip over chunks that already exist (we use the region cache so as to not load them)
            // while (RegionFileCache.createOrLoadRegionFile(world.getChunkSaveLocation(), X, Z).chunkExists(X & 0x1F, Z
            // & 0x1F))
            // {
            // --todo;
            // if (next())
            // return;
            // }

            // get and populate the chunk manually so that it does not get fully loaded in, this saves ram and increases
            // performance
            ChunkProviderServer provider = (ChunkProviderServer) world.getChunkProvider();

            Chunk chunk = provider.currentChunkProvider.loadChunk(X, Z);
            // TODO: Populating chunks that way only works, if the surrounding chunks also exist
            chunk.populateChunk(provider, provider, X, Z);
            saveChunk(provider, chunk);

            --todo;
            if (next())
                return;
        }
    }

    private boolean next()
    {
        // square
        if (border.getShape() == AreaShape.BOX)
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
        // round
        else
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

                double dx = centerX - X;
                double dz = centerZ - Z;
                if (Math.sqrt(dx * dx + dz * dz) < rad)
                    break;
            }
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
            /* ignopre errors */
        }
        if (!stopped)
        {
            OutputHandler.chatWarning(sender, "Filler finished after " + ticks + " ticks.");
            System.out.print("Removed filler? :" + DataManager.getInstance().delete(TickTaskFill.class, dimID));
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
        OutputHandler.chatWarning(sender, "Filler stopped after " + ticks + " ticks. Still to do: " + todo + " chuncks.");
        System.gc();
    }

    public String getStatus()
    {
        return "Todo: " + getETA() + " at " + speed + " chuncks per ticks.";
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
        OutputHandler.chatWarning(sender, String.format("Changed speed of filler %d to %d", dimID, speed));
    }

    /**
     * Save the provided chunk without involving the provider directly or generating events.
     * 
     * @param provider
     *            The WorldServer chunk provider
     * @param chunk
     *            The chunk to save
     */
    private void saveChunk(ChunkProviderServer provider, Chunk chunk)
    {
        AnvilChunkLoader loader = (AnvilChunkLoader) provider.currentChunkLoader;
        try
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound.setTag("Level", nbttagcompound1);
            this.writeChunkToNBT(loader, chunk, world, nbttagcompound1);
            this.writeChunkNBTTags(loader, chunk, nbttagcompound);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /*
     * obtain the method call for AnvilChunkLoader.writeChunkToNBT and make it accessible
     */
    private void setupWriteChunkToNBT()
    {
        @SuppressWarnings("rawtypes")
        Class[] cArg = new Class[] { Chunk.class, World.class, NBTTagCompound.class };
        try
        {
            writeChunkToNBT = AnvilChunkLoader.class.getDeclaredMethod("func_75820_a", cArg); // writeChunkToNBT
        }
        catch (NoSuchMethodException e)
        {
            try
            {
                writeChunkToNBT = AnvilChunkLoader.class.getDeclaredMethod("writeChunkToNBT", cArg);
            }
            catch (NoSuchMethodException e1)
            {
                throw new RuntimeException("TickTaskFill: Unable to obtain access to private method AnvilChunkLoader.writeChunkToNBT");
            }
        }

        writeChunkToNBT.setAccessible(true);
    }

    /* wrapper for AnvilChunkLoader.writeChunkToNBT */
    private void writeChunkToNBT(AnvilChunkLoader loader, Chunk chunk, World world, NBTTagCompound tag)
    {
        Object[] args = new Object[] { chunk, world, tag };
        try
        {
            writeChunkToNBT.invoke(loader, args);
        }
        catch (IllegalAccessException | IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.getCause().printStackTrace();
        }
    }

    private void writeChunkNBTTags(AnvilChunkLoader loader, Chunk chunk, NBTTagCompound tag) throws IOException
    {
        try (DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(world.getChunkSaveLocation(), chunk.xPosition, chunk.zPosition))
        {
            CompressedStreamTools.write(tag, dataoutputstream);
        }
    }
}
