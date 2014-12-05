package com.forgeessentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommandBubble extends FEcmdModuleCommands {
    
    public static String BUBBLE_GROUP = "command_bubble";
    
    public CommandBubble() {
        APIRegistry.getFEEventBus().register(this);
    }
    
    @SubscribeEvent
    public void permissionInitializeEvent(PermissionEvent.Initialize e) {
        e.serverZone.setGroupPermissionProperty(BUBBLE_GROUP, FEPermissions.GROUP_PRIORITY, "45");
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_USE + IPermissionsHelper.ALL_PERMS, false);
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_PLACE + IPermissionsHelper.ALL_PERMS, false);
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_BREAK + IPermissionsHelper.ALL_PERMS, false);
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_INTERACT + IPermissionsHelper.ALL_PERMS, false);
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_INTERACT_ENTITY + IPermissionsHelper.ALL_PERMS, false);
    }
    
    @Override
    public String getCommandName()
    {
        return "bubble";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        doCmd(sender, args);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        doCmd(sender, args);
    }

    public void doCmd(ICommandSender sender, String[] args)
    {
        boolean toggleOn = !APIRegistry.perms.getServerZone().getIncludedGroups(IPermissionsHelper.GROUP_DEFAULT).contains(BUBBLE_GROUP);
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("on"))
                toggleOn = true;
            if (args[0].equalsIgnoreCase("off"))
                toggleOn = false;
        }
        if (toggleOn)
        {
            APIRegistry.perms.getServerZone().groupIncludeAdd(IPermissionsHelper.GROUP_DEFAULT, BUBBLE_GROUP);
            OutputHandler.chatConfirmation(sender, "Activated bubble. Players are now unable to interact with the world.");
        }
        else
        {
            APIRegistry.perms.getServerZone().groupIncludeRemove(IPermissionsHelper.GROUP_DEFAULT, BUBBLE_GROUP);
            OutputHandler.chatConfirmation(sender, "Deactivated bubble");
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
            return getListOfStringsMatchingLastWord(args, "on", "off");
        }
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/bubble [on|off]";
    }

}