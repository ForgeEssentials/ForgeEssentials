package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.src.EntityArrow;
import net.minecraft.src.EntityBat;
import net.minecraft.src.EntityBlaze;
import net.minecraft.src.EntityBoat;
import net.minecraft.src.EntityCaveSpider;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.EntityCow;
import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityDragon;
import net.minecraft.src.EntityEnderCrystal;
import net.minecraft.src.EntityEnderEye;
import net.minecraft.src.EntityEnderPearl;
import net.minecraft.src.EntityEnderman;
import net.minecraft.src.EntityExpBottle;
import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.EntityGiantZombie;
import net.minecraft.src.EntityIronGolem;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityItemFrame;
import net.minecraft.src.EntityLargeFireball;
import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMagmaCube;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityMooshroom;
import net.minecraft.src.EntityOcelot;
import net.minecraft.src.EntityPainting;
import net.minecraft.src.EntityPig;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPotion;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.EntitySilverfish;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.EntitySmallFireball;
import net.minecraft.src.EntitySnowball;
import net.minecraft.src.EntitySnowman;
import net.minecraft.src.EntitySpider;
import net.minecraft.src.EntitySquid;
import net.minecraft.src.EntityTNTPrimed;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.EntityWitch;
import net.minecraft.src.EntityWither;
import net.minecraft.src.EntityWitherSkull;
import net.minecraft.src.EntityWolf;
import net.minecraft.src.EntityXPOrb;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.World;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSpawn extends ForgeEssentialsCommandBase
{

	private HashMap<String, String> mobNames = new HashMap<String, String>();

	public CommandSpawn()
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
		return "spawn";
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
				} catch (NumberFormatException e)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
					return;
				}
				if (args.length >= 5)
				{
					try
					{
						x = new Integer(args[2]);
					} catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[2]));
						return;
					}
					try
					{
						y = new Integer(args[3]);
					} catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[3]));
						return;
					}
					try
					{
						z = new Integer(args[4]);
					} catch (NumberFormatException e)
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
		} else
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX));
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
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[1]));
				return;
			}
			try
			{
				x = new Integer(args[2]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[2]));
				return;
			}
			try
			{
				y = new Integer(args[3]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[3]));
				return;
			}
			try
			{
				z = new Integer(args[4]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[4]));
				return;
			}
			try
			{
				dimension = new Integer(args[5]);
			} catch (NumberFormatException e)
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
		} else
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX));
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
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
		} else
		{
			return null;
		}
	}

}
