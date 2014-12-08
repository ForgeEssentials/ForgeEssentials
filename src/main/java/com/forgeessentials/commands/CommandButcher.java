package com.forgeessentials.commands;

import com.forgeessentials.api.EnumMobType;
import com.forgeessentials.commands.util.CommandButcherTickTask;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FEOptionParser;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.tasks.TaskRegistry;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import joptsimple.OptionSet;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.CommandButcherTickTask;
import com.forgeessentials.commands.util.CommandButcherTickTask.ButcherMobType;
import com.forgeessentials.commands.util.FEcmdModuleCommands;

public class CommandButcher extends FEcmdModuleCommands {
    
    public static List<String> typeList = ButcherMobType.getNames();

    @Override
    public String getCommandName()
    {
        return "febutcher";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "butcher" };
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
    {      
        if (args.length >= 2) {
        	switch ( args[args.length - 2] ) {
        		case "-m" :
        			getListOfStringsMatchingLastWord(args, typeList);
        			break;
        	}
        }
        
        return null;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
    	FEOptionParser parser = new FEOptionParser("butcher");
    	parser.accepts("x")
    		.withRequiredArg()
    		.ofType(Integer.class)
    		.defaultsTo((int)sender.posX, (int)sender.posY, (int)sender.posZ)
    		.describedAs("Target Point")
    		.withValuesSeparatedBy(",");
    	parser.accepts("r")
    		.withRequiredArg()
    		.ofType(Integer.class)
    		.defaultsTo(0)
    		.describedAs("Radius: -1 for World.");
    	parser.accepts("w")
    		.withRequiredArg()
    		.ofType(Integer.class)
    		.defaultsTo(sender.worldObj.provider.dimensionId)
    		.describedAs("World");
    	parser.accepts("m")
    		.withRequiredArg()
    		.ofType(String.class)
    		.defaultsTo(ButcherMobType.HOSTILE.toString())
    		.describedAs("Mob Type: " + ButcherMobType.getNames());
    	
    	OptionSet options = parser.parse(sender, args);

    	if (options == null)
    		return;
    	
        int radius = (int)options.valueOf("r");
        int x = (int)options.valuesOf("x").get(0);
        int y = (int)options.valuesOf("x").get(1);
        int z = (int)options.valuesOf("x").get(2);
        String mobType = (String)options.valueOf("m");
        World world = DimensionManager.getWorld((int)options.valueOf("w"));
        
        if (world == null) {
            throw new CommandException("The specified dimension does not exist.");
        }
        
        AxisAlignedBB pool = AxisAlignedBB.getBoundingBox(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(sender, world, mobType, pool, radius);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    	FEOptionParser parser = new FEOptionParser("butcher");
    	parser.accepts("x")
    		.withRequiredArg()
    		.ofType(Integer.class)
    		.defaultsTo(0, 0, 0)
    		.describedAs("Target Point")
    		.withValuesSeparatedBy(",");
    	parser.accepts("r")
    		.withRequiredArg()
    		.ofType(Integer.class)
    		.defaultsTo(-1)
    		.describedAs("Radius: -1 for World.");
    	parser.accepts("w")
    		.withRequiredArg()
    		.ofType(Integer.class)
    		.defaultsTo(0)
    		.describedAs("World");
    	parser.accepts("m")
    		.withRequiredArg()
    		.ofType(String.class)
    		.defaultsTo(ButcherMobType.HOSTILE.toString())
    		.describedAs("Mob Type: " + ButcherMobType.getNames());
    	
    	OptionSet options = parser.parse(sender, args);
    	
    	if (options == null)
    		return;
    	
        int radius = (int)options.valueOf("r");
        int x = (int)options.valuesOf("x").get(0);
        int y = (int)options.valuesOf("x").get(1);
        int z = (int)options.valuesOf("x").get(2);
        String mobType = (String)options.valueOf("m");
        World world = DimensionManager.getWorld((int)options.valueOf("w"));
        
        if (world == null) {
            throw new CommandException("The specified dimension does not exist.");
        }
        
        AxisAlignedBB pool = AxisAlignedBB.getBoundingBox(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(sender, world, mobType, pool, radius);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/butcher [-x <x,y,z>][-r <radius>] [-m <mob type>] : Kills the type of mobs within the specified radius around the specified point in the specified world.";
    }

}
