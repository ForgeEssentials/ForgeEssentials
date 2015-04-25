package com.forgeessentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commons.UserIdent;
import com.forgeessentials.core.misc.TranslatedCommandException;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRepair extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "repair";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
        {
            ItemStack item = sender.getHeldItem();
            if (item == null)
                throw new TranslatedCommandException("You are not holding a reparable item.");
            item.setItemDamage(0);
        }
        else if (args.length == 1 && PermissionsManager.checkPermission(sender, getPermissionNode() + ".others"))
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player == null)
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);

            ItemStack item = player.getHeldItem();
            if (item != null)
                item.setItemDamage(0);
        }
        else
        {
        	throw new TranslatedCommandException(getCommandUsage(sender));
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            // PlayerSelector.matchPlayers(sender, args[0])
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {

                ItemStack item = player.getHeldItem();

                if (item != null)
                {
                    item.setItemDamage(0);
                }

            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else
        	throw new TranslatedCommandException(getCommandUsage(sender));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", RegisteredPermValue.OP);
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
    		return "/repair [player] Repairs the item you or another player is holding.";
        }
        else
        {
        	return "/repair <player> Repairs the item the player is holding.";
        }
        
    }

}
