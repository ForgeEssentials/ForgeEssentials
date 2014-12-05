package com.forgeessentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandSpawnMob extends FEcmdModuleCommands {

    private HashMap<String, String> mobNames = new HashMap<String, String>();

    public CommandSpawnMob()
    {
    	// Some type of nested table might be nice here...
        mobNames.put("creeper", "Creeper");
        mobNames.put("skeleton", "Skeleton");
        mobNames.put("skele", "Skeleton");
        mobNames.put("witherskeleton", "Skeleton");
        mobNames.put("wskeleton", "Skeleton");
        mobNames.put("wskele", "Skeleton");
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
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length >= 1)
        {
            MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(sender, 500);
            if (mop == null)
            {
                OutputHandler.chatError(sender, "You must first look at the ground!");
                return;
            }
            int amount = 1;
            double x = mop.blockX + 0.5D;
            double y = mop.blockY + 1;
            double z = mop.blockZ + 0.5D;
            if (args.length >= 2 && !args[1].equalsIgnoreCase("name"))
            {
                amount = parseIntWithMin(sender, args[1], 1);

                if (args.length >= 5)
                {
                    x = 0.5 + parseDouble(sender, args[2], sender.posX);
                    y = 0.5 + parseDouble(sender, args[3], sender.posY);
                    z = 0.5 + parseDouble(sender, args[4], sender.posZ);
                }
            }
            for (int i = 0; i < amount; i++)
            {
                EntityCreature mob = (EntityCreature) EntityList.createEntityByName(mobNames.get(args[0].toLowerCase()), sender.worldObj);
                if (mob == null)
                {
                    OutputHandler.chatError(sender, String.format("%s was not recognized as a mob.", args[0]));
                    return;
                }
                if (args[0].toLowerCase().equals("witherskeleton") || args[0].toLowerCase().equals("wskeleton") || args[0].toLowerCase().equals("wskele"))
                {
                	// Better safe than sorry...
                	if (mob instanceof EntitySkeleton) {
                		((EntitySkeleton)mob).setSkeletonType(1);
                	}
                }
                if (mob instanceof EntityLiving) {
                	((EntityLiving)mob).onSpawnWithEgg((IEntityLivingData)null);
                }
                mob.setPosition(x, y, z);
                sender.worldObj.spawnEntityInWorld(mob);
                if (args.length >= 3 && args[1].equalsIgnoreCase("name")) {
                	StringBuilder sb = new StringBuilder();
                	for(int index = 2; index < args.length; index++)
                	{
                		sb.append(" " + args[index]);
                	}
                	mob.setCustomNameTag(sb.toString());
                }
                mob.spawnExplosionParticle();
            }
        }
        else
        {
        	throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length >= 6)
        {
            int amount, x, y, z;
            int dimension = 0;
            
            amount = parseInt(sender, args[1]);
            x = parseInt(sender, args[2]);
            y = parseInt(sender, args[3]);
            z = parseInt(sender, args[4]);
            dimension = parseInt(sender, args[5]);
            
            for (int i = 0; i < amount; i++)
            {
                World world = DimensionManager.getWorld(dimension);
                EntityCreature mob = (EntityCreature) EntityList.createEntityByName(mobNames.get(args[0].toLowerCase()), world);
                if (mob == null)
                {
                    OutputHandler.chatError(sender, String.format("%s was not recognized as a mob.", args[0]));
                    return;
                }
                if (args.length >= 6) {
                	StringBuilder sb = new StringBuilder();
                	for(int index = 6; index < args.length; index++)
                	{
                		sb.append(" " + args[index]);
                	}
                	mob.setCustomNameTag(sb.toString());
                }
                if (mob instanceof EntityLiving) {
                	((EntityLiving)mob).onSpawnWithEgg((IEntityLivingData)null);
                }
                mob.setPosition(x, y, z);
                world.spawnEntityInWorld(mob);
                mob.spawnExplosionParticle();
            }
        }
        else
        {
        	throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
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

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
    	if (sender instanceof EntityPlayer)
        {
    		return "/spawnmob <mob type> [<amount> [<x> <y> <z>] [dimension]] Spawns a mob at the point you are looking at or the specified location.";
        }
        else
        {
        	return "/spawnmob <mob type> <amount> <x> <y> <z> <dimension> Spawns a mob at a specified location.";
        }
    }

}
