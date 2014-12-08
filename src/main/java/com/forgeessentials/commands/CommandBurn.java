package com.forgeessentials.commands;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FEOptionParser;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBurn extends FEcmdModuleCommands {
	
    @Override
    public String getCommandName()
    {
        return "burn";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
    	FEOptionParser parser = new FEOptionParser("burn");
    	parser.accepts("p").withRequiredArg().defaultsTo("me").ofType(String.class).describedAs("Target Player.");
    	parser.accepts("t").withRequiredArg().defaultsTo("15").ofType(Integer.class).describedAs("Time to burn.");
    	
    	OptionSet options = parser.parse(sender, args);
    	
    	if (options == null)
    		return;
    	
    	EntityPlayerMP target = sender;
    	int time = (int)options.valueOf("t");
    		time = time < 0 ? 0 : time;
    		
    	if (options.has("p") && !options.valueOf("p").toString().equalsIgnoreCase("me")) {
    		if (PermissionsManager.checkPermission(sender, getPermissionNode() + ".others")) {
    			target = UserIdent.getPlayerByUsername(options.valueOf("p").toString());
    			if (target == null) {
    				throw new CommandException(String.format("Player %s does not exist, or is not online.", options.valueOf("p").toString()));
    			}
    		}
    		else {
    			throw new CommandException("You lack the permission to burn other players.");
    		}
    	}
    	
    	OutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
    	target.setFire(time);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    	FEOptionParser parser = new FEOptionParser("burn");
    	parser.accepts("p").withRequiredArg().defaultsTo("Olee").ofType(String.class).describedAs("Target Player");
    	parser.accepts("t").withRequiredArg().defaultsTo("15").ofType(Integer.class).describedAs("Burn Time");
    	parser.accepts("?");
    	
    	OptionSet options = parser.parse(sender, args);

    	if (options == null)
    		return;
    	
    	EntityPlayerMP target;
    	int time = (int)options.valueOf("t");
			time = time < 0 ? 0 : time;
    	
    	if (options.has("?")) {
    		parser.printHelpOn(sender);
    		return;
    	}
    	
    	if (options.has("p")) {
    		target = UserIdent.getPlayerByUsername(options.valueOf("p").toString());
    		if (target == null) {
    			throw new CommandException(String.format("Player %s does not exist, or is not online.", options.valueOf("p").toString()));
    		}
    	}
    	else {
    		throw new WrongUsageException(getCommandUsage(sender));
    	}
    	
    	OutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
    	target.setFire(time);
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", RegisteredPermValue.OP);
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
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
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
    		return "/burn [-p <me|player>] [-t <time>] Set a player on fire.";
        }
        else
        {
        	return "/burn <-p player> [-t <time>] Set a player on fire.";
        }
    }
}
