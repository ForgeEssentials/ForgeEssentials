package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandButcher extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "butcher";
	}

	public List getCommandAliases()
	{
		return Arrays.asList(new String[] { "butch", "killall" });
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		try
		{
			boolean all = false;
			int radius = 5;
			if (var2.length == 1)
			{
				radius = Integer.parseInt(var2[0]);
			}
			else if (var2.length == 0)
			{
				all = true;
			}
			else
			{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Butcher Command Failed(Try /butcher (<radius>)");
				return;
			}

			butcherCommand(radius, all, this.getCommandSenderAsPlayer(var1));
		}
		catch (Exception e)
		{
			this.getCommandSenderAsPlayer(var1).addChatMessage("Butcher Command Failed!(Unknown Reason)");
		}
	}

	public static void butcherCommand(int radius, boolean all, EntityPlayer sender)
	{
		int killed = 0;
		for (int i = 0; i < sender.worldObj.loadedEntityList.size(); i++)
		{
			Entity ent = (Entity) sender.worldObj.loadedEntityList.get(i);
			if (ent instanceof EntityItem || ent instanceof EntityMob || ent instanceof EntityAnimal)
			{
				if (all)
				{
					ent.setDead();
					killed++;
				}
				else
				{
					if (ent.getDistanceToEntity(sender) < radius)
					{
						ent.setDead();
						killed++;
					}
				}
			}
		}
		sender.addChatMessage("Butchered " + killed + " entities");
	}

}
