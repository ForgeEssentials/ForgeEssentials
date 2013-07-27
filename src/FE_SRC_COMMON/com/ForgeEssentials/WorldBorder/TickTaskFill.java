package com.ForgeEssentials.WorldBorder;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.data.api.ClassContainer;
import com.ForgeEssentials.data.api.DataStorageManager;
import com.ForgeEssentials.data.api.IReconstructData;
import com.ForgeEssentials.data.api.SaveableObject;
import com.ForgeEssentials.data.api.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.api.SaveableObject.SaveableField;
import com.ForgeEssentials.data.api.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.util.FEChunkLoader;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.tasks.ITickTask;
import com.ForgeEssentials.util.tasks.TaskRegistry;

/**
 * Does the actual filling, with limited chuncks per tick.
 * @author Dries007
 */

@SaveableObject
public class TickTaskFill implements ITickTask
{
	@UniqueLoadingKey
	@SaveableField
	String					dimID;
	boolean					isComplete	= false;

	WorldServer				world;

	int						minX;
	int						minZ;

	int						maxX;
	int						maxZ;

	int						centerX;
	int						centerZ;
	int						rad;

	@SaveableField
	long					ticks		= 0L;
	@SaveableField
	long					todo		= 0L;

	MinecraftServer			server		= MinecraftServer.getServer();

	@SaveableField
	private int				X;
	@SaveableField
	private int				Z;

	@SaveableField
	public int				speed		= 1;

	public WorldBorder		border;

	ICommandSender			source;

	boolean					stopped		= false;
	final ClassContainer	con			= new ClassContainer(TickTaskFill.class);

	@Reconstructor
	private static TickTaskFill reconstruct(IReconstructData tag)
	{
		return new TickTaskFill(tag);
	}

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
		border = ModuleWorldBorder.borderMap.get(APIRegistry.zones.getWorldZone(world).getZoneName());

		if (border.shapeByte == 0 || border.rad == 0)
		{
			OutputHandler.chatError(sender, "You need to set the worldborder first!");
			return;
		}

		X = minX = (border.center.x - border.rad - ModuleWorldBorder.overGenerate) / 16;
		Z = minZ = (border.center.z - border.rad - ModuleWorldBorder.overGenerate) / 16;
		maxX = (border.center.x + border.rad + ModuleWorldBorder.overGenerate) / 16;
		maxZ = (border.center.z + border.rad + ModuleWorldBorder.overGenerate) / 16;
		centerX = border.center.x / 16;
		centerZ = border.center.z / 16;
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

		if (ticks % (20 * 25) == 0) {
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
			boolean var6 = world.canNotSave;
			world.canNotSave = false;
			world.saveAllChunks(true, (IProgressUpdate) null);
			world.canNotSave = var6;
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
