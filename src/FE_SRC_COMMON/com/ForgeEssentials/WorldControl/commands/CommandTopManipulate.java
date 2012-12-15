package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskHandler;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskTopManipulator;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandTopManipulate extends WorldControlCommandBase
{

	private String name;
	private TickTaskTopManipulator.Mode manipulateMode;
	
	public CommandTopManipulate(String cmdName, TickTaskTopManipulator.Mode mode)
	{
		super(false);
		this.name = cmdName;
		this.manipulateMode = mode;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length == 1 || args.length == 3)
		{
			int radius = -1;
			Point effectPosition = null;
			
			try
			{
				radius = Integer.parseInt(args[0]);
			}
			catch (Exception e)
			{
				error(player);
				radius = -1;
			}
			
			if (args.length == 1)
			{
				effectPosition = new Point((int)player.posX - 1, (int)player.posY, (int)player.posZ);
			}
			else
			{
				int x;
				int z;
				
				try
				{
					x = Integer.parseInt(args[1]);
					z = Integer.parseInt(args[2]);
					
					effectPosition = new Point(x, 0, z);
				}
				catch (Exception e)
				{
					error(player);
				}
			}
			
			if (radius != -1 && effectPosition != null)
			{
				World world = player.worldObj;
				BackupArea back = new BackupArea();
				// For some reason, player.posX is out.

				TickTaskHandler.addTask(new TickTaskTopManipulator(player, back, effectPosition, radius, this.manipulateMode));
			}
		}
		else
		{
			error(player);
		}
	}

}
