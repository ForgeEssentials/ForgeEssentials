package com.forgeessentials.playerlogger.rollback;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.playerlogger.BlockChange;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.tasks.ITickTask;
import com.google.common.base.Charsets;

import cpw.mods.fml.common.registry.GameData;

public class TickTaskRollback implements ITickTask {
	private boolean isComplete = false;
	private ICommandSender sender;
	private int changed = 0;
	private boolean undo;
	private WorldServer world;
	private Iterator<BlockChange> i;

	/**
	 * @param sender
	 * @param undo
	 *            @throws SQLException
	 */
	public TickTaskRollback(ICommandSender sender, boolean undo, ArrayList<BlockChange> changes) throws SQLException
	{
		this.sender = sender;
		this.undo = undo;
		this.i = changes.iterator();
	}

	@Override
	public void tick()
	{
		int currentTickChanged = 0;
		boolean continueFlag = true;

		while (continueFlag)
		{
			try
			{
				if (i.hasNext())
				{
					BlockChange bc = i.next();
					world = DimensionManager.getWorld(bc.getDimension());

					if (bc.getType() == 0)
					{
						if (!undo)
						{
							place(bc);
						}
						else
						{
							remove(bc);
						}
					}
					else if (bc.getType() == 1)
					{
						if (undo)
						{
							place(bc);
						}
						else
						{
							remove(bc);
						}
					}
					currentTickChanged++;
					world.markBlockForUpdate(bc.getX(), bc.getY(), bc.getZ());
				}
				else
				{
					isComplete = true;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (isComplete || currentTickChanged >= 20)
			{
				// Stop running this tick.
				changed += currentTickChanged;
				continueFlag = false;
			}
		}
	}

	public void place(BlockChange bc)
	{
		String[] block = bc.getBlock().split(":");

		Block blockPlace = GameData.getBlockRegistry().getObject(block[0]); // legacy
		world.setBlock(bc.getX(), bc.getY(), bc.getZ(), blockPlace, Integer.parseInt(block[1]), 2);
		if (bc.getData() != null)
		{
			try
			{
				byte[] bdata = bc.getData().getBytes(1, (int) bc.getData().length());
				System.out.println(new String(bdata));

				// reconstruct TE
				NBTTagCompound compound = (NBTTagCompound) JsonToNBT.func_150315_a(new String(bdata, Charsets.UTF_8));
				TileEntity te = (TileEntity) Class.forName(compound.getString("TE_CLASS")).newInstance();
				te.readFromNBT(compound);

				world.setTileEntity(bc.getX(), bc.getY(), bc.getZ(), te);
			}
			catch (Exception e)
			{
				ChatUtils.sendMessage(sender, "Could not restore TE data.");
				e.printStackTrace();
			}
		}
	}

	public void remove(BlockChange bc) throws SQLException
	{
		world.removeTileEntity(bc.getX(), bc.getY(), bc.getZ());
		world.setBlock(bc.getX(), bc.getY(), bc.getZ(), Blocks.air);
	}

	@Override
	public void onComplete()
	{
		ChatUtils.sendMessage(sender, "Rollback done! Changed " + changed + " blocks.");
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

}
