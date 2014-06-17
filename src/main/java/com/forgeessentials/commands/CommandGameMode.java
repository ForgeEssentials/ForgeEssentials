package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumGameType;

import java.util.ArrayList;
import java.util.List;

public class CommandGameMode extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "gamemode";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "gm" };
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
    	EnumGameType gm;
    	switch(args.length) {
    	case 0:
    		setGameMode(sender);
    		break;
    	case 1:
    		gm = getGameTypeFromString(args[0]);
    		if(gm != null){
    			setGameMode(sender, sender, gm);
    		}
    		else {
    			setGameMode(sender, args[0]);
    		}
    		break;
    	default:
    		gm = getGameTypeFromString(args[0]);
    		if(gm != null){
    			for(int i = 1; i < args.length; i++) {
    				setGameMode(sender, args[i], gm);
    			}
    		}
    		else {
    			throw new WrongUsageException("commands.gamemode.usage");
    		}
    	}
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    	EnumGameType gm;
    	switch(args.length) {
    	case 0:
    		throw new WrongUsageException("commands.gamemode.usage");
    	case 1:
    		gm = getGameTypeFromString(args[0]);
    		if(gm != null){
    			throw new WrongUsageException("commands.gamemode.usage");
    		}
    		else {
    			setGameMode(sender, args[0]);
    		}
    		break;
    	default:
    		gm = getGameTypeFromString(args[0]);
    		if(gm != null){
    			for(int i = 1; i < args.length; i++) {
    				setGameMode(sender, args[i], gm);
    			}
    		}
    		else {
    			throw new WrongUsageException("commands.gamemode.usage");
    		}
    		break;
    	}
    }
    
    public void setGameMode(EntityPlayer sender) {
    	setGameMode(sender, sender, sender.capabilities.isCreativeMode ? EnumGameType.SURVIVAL : EnumGameType.CREATIVE);
    }
    
    public void setGameMode(ICommandSender sender, String target) {
    	EntityPlayer player;
    	try{
    		player = getPlayer(sender, target);
    	}
    	catch(PlayerNotFoundException e) {
    		OutputHandler.chatError(sender, String.format("Unable to find player: %1$s.", target));
    		return;
    	}
    	if(player != null){
    		setGameMode(sender, target, player.capabilities.isCreativeMode ? EnumGameType.SURVIVAL : EnumGameType.CREATIVE);
    	}
    	else {
    		OutputHandler.chatError(sender, String.format("Unable to find player: %1$s.", target));
    	}
    }
    
    public void setGameMode(ICommandSender sender, String target, EnumGameType mode) {
    	EntityPlayer player;
    	try{
    		player = getPlayer(sender, target);
    	}
    	catch(PlayerNotFoundException e) {
    		OutputHandler.chatError(sender, String.format("Unable to find player: %1$s.", target));
    		return;
    	}
    	if(player != null){
    		setGameMode(sender, player, mode);
    	}
    	else {
    		OutputHandler.chatError(sender, String.format("Unable to find player: %1$s.", target));
    	}
    }
    
    public void setGameMode(ICommandSender sender, EntityPlayer target, EnumGameType mode) {
    	target.setGameType(mode);
        target.fallDistance = 0.0F;
        String modeName = StatCollector.translateToLocal("gameMode." + mode.getName());
        OutputHandler.chatConfirmation(sender, String.format("%1$s's gamemode was changed to %2$s.", target.username, modeName));
    }

    private EnumGameType getGameTypeFromString(String string)
    {
        if (string.equalsIgnoreCase(EnumGameType.SURVIVAL.getName()) || string.equalsIgnoreCase("s") || string.equals("0"))
        {
            return EnumGameType.SURVIVAL;
        }
        else if (string.equalsIgnoreCase(EnumGameType.CREATIVE.getName()) || string.equalsIgnoreCase("c") || string.equals("1"))
        {
            return EnumGameType.CREATIVE;
        }
        else if (string.equalsIgnoreCase(EnumGameType.ADVENTURE.getName()) || string.equalsIgnoreCase("a") || string.equals("2"))
        {
            return EnumGameType.ADVENTURE;
        }
        else
        {
            return null;
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
            return getListOfStringsMatchingLastWord(args, new String[] { "survival", "creative", "adventure" });
        }
        else
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }

    }

    @Override
    public void registerExtraPermissions(IPermRegisterEvent event)
    {
        event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        // TODO Auto-generated method stub
        return "/gamemode [gamemode] <player(s)> Change a player's gamemode.";
    }
}
