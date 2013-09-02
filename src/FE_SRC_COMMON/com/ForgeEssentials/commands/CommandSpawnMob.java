package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandSpawnMob extends FEcmdModuleCommands
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
				amount = parseIntWithMin(sender, args[1], 1);

				if (args.length >= 5)
				{
					x = 0.5 + parseInt(sender, args[2], sender.posX);
					y = 0.5 + parseInt(sender, args[3], sender.posY);
					z = 0.5 + parseInt(sender, args[4], sender.posZ);
				}
			}
			for (int i = 0; i < amount; i++)
			{
				EntityCreature mob = (EntityCreature) EntityList.createEntityByName(mobNames.get(args[0].toLowerCase()), sender.worldObj);
				if (mob == null)
				{
					OutputHandler.chatError(sender, Localization.format("command.spawnmob.noMobX", args[0]));
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
			amount = parseInt(sender, args[1]);
			x = parseInt(sender, args[2]);
			y = parseInt(sender, args[3]);
			z = parseInt(sender, args[4]);
			dimension = parseInt(sender, args[5]);
			for (int i = 0; i < amount; i++)
			{
				World world = FunctionHelper.getDimension(dimension);
				EntityCreature mob = (EntityCreature) EntityList.createEntityByName(mobNames.get(args[0].toLowerCase()), world);
				if (mob == null)
				{
					ChatUtils.sendMessage(sender, Localization.format(Localization.ERROR_NOMOB, args[0]));
					return;
				}
				mob.setPosition(x, y, z);
				world.spawnEntityInWorld(mob);
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX));
			OutputHandler.debug("test");
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(args, mobNames.keySet());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

}
