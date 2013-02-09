package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandSpawnMob extends ForgeEssentialsCommandBase
{

	private HashMap<String, String>	mobNames	= new HashMap<String, String>();

	public CommandSpawnMob()
	{
		mobNames.put("creeper", "Creeper");
		mobNames.put("skeleton", "Skeleton");
		mobNames.put("spider", "Spider");
		mobNames.put("giant", "Giant");
		mobNames.put("zombie", "Zombie");
		mobNames.put("slime", "Slime");
		mobNames.put("ghast", "Ghast");
		mobNames.put("pigzombie", "PigZombie");
		mobNames.put("zombiepigman", "PigZombie");
		mobNames.put("enderman", "Enderman");
		mobNames.put("cavespider", "CaveSpider");
		mobNames.put("silverfish", "Silverfish");
		mobNames.put("blaze", "Blaze");
		mobNames.put("magmaslime", "LavaSlime");
		mobNames.put("lavaslime", "LavaSlime");
		mobNames.put("magmacube", "LavaSlime");
		mobNames.put("lavacube", "LavaSlime");
		mobNames.put("enderdragon", "EnderDragon");
		mobNames.put("dragon", "EnderDragon");
		mobNames.put("wither", "WitherBoss");
		mobNames.put("witherboss", "WitherBoss");
		mobNames.put("bat", "Bat");
		mobNames.put("witch", "Witch");
		mobNames.put("pig", "Pig");
		mobNames.put("sheep", "Sheep");
		mobNames.put("cow", "Cow");
		mobNames.put("chicken", "Chicken");
		mobNames.put("squid", "Squid");
		mobNames.put("wolf", "Wolf");
		mobNames.put("dog", "Wolf");
		mobNames.put("mooshroom", "MushroomCow");
		mobNames.put("mushroomcow", "MushroomCow");
		mobNames.put("snowman", "SnowMan");
		mobNames.put("ocelot", "Ozelot");
		mobNames.put("golem", "VillagerGolem");
		mobNames.put("villager", "Villager");
	}

	@Override
	public String getCommandName()
	{
		return "spawnmob";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1)
		{
			MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(sender, false);
			if (mop == null)
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_TARGET));
				return;
			}
			int amount = 1;
			double x = mop.blockX + 0.5D;
			double y = mop.blockY + 1;
			double z = mop.blockZ + 0.5D;
			if (args.length >= 2)
			{
				try
				{
					amount = new Integer(args[1]);
				}
				catch (NumberFormatException e)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
					return;
				}
				if (args.length >= 5)
				{
					try
					{
						x = new Integer(args[2]);
					}
					catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[2]));
						return;
					}
					try
					{
						y = new Integer(args[3]);
					}
					catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[3]));
						return;
					}
					try
					{
						z = new Integer(args[4]);
					}
					catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[4]));
						return;
					}
				}
			}
			for (int i = 0; i < amount; i++)
			{
				EntityCreature mob = (EntityCreature) EntityList.createEntityByName(mobNames.get(args[0].toLowerCase()), sender.worldObj);
				if (mob == null)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOMOB, args[0]));
					return;
				}
				mob.setPosition(x, y, z);
				sender.worldObj.spawnEntityInWorld(mob);
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 6)
		{
			int amount;
			int x;
			int y;
			int z;
			int dimension = 0;
			try
			{
				amount = new Integer(args[1]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[1]));
				return;
			}
			try
			{
				x = new Integer(args[2]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[2]));
				return;
			}
			try
			{
				y = new Integer(args[3]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[3]));
				return;
			}
			try
			{
				z = new Integer(args[4]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[4]));
				return;
			}
			try
			{
				dimension = new Integer(args[5]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[5]));
			}
			for (int i = 0; i < amount; i++)
			{
				World world = FunctionHelper.getDimension(dimension);
				EntityCreature mob = (EntityCreature) EntityList.createEntityByName(mobNames.get(args[0].toLowerCase()), world);
				if (mob == null)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOMOB, args[0]));
					return;
				}
				mob.setPosition(x, y, z);
				world.spawnEntityInWorld(mob);
			}
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX));
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, mobNames.keySet());
		}
		else
		{
			return null;
		}
	}

}
