package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.output.ChatOutputHandler;



public class CommandBubble extends ForgeEssentialsCommandBase
{

    public static String BUBBLE_GROUP = "command_bubble";

    public CommandBubble()
    {
        APIRegistry.getFEEventBus().register(this);
    }

    @Override
    public String getCommandName()
    {
        return "febubble";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "bubble" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/bubble [on|off]";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".bubble";
    }

    @SubscribeEvent
    public void permissionInitializeEvent(PermissionEvent.Initialize e)
    {
        e.serverZone.setGroupPermissionProperty(BUBBLE_GROUP, FEPermissions.GROUP_PRIORITY, "45");
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_USE + Zone.ALL_PERMS, false);
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, false);
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, false);
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, false);
        e.serverZone.setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_INTERACT_ENTITY + Zone.ALL_PERMS, false);
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
        boolean toggleOn = !APIRegistry.perms.getServerZone().getIncludedGroups(Zone.GROUP_DEFAULT).contains(BUBBLE_GROUP);
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("on"))
                toggleOn = true;
            if (args[0].equalsIgnoreCase("off"))
                toggleOn = false;
        }
        if (toggleOn)
        {
            APIRegistry.perms.getServerZone().groupIncludeAdd(Zone.GROUP_DEFAULT, BUBBLE_GROUP);
            ChatOutputHandler.chatConfirmation(sender, "Activated bubble. Players are now unable to interact with the world.");
        }
        else
        {
            APIRegistry.perms.getServerZone().groupIncludeRemove(Zone.GROUP_DEFAULT, BUBBLE_GROUP);
            ChatOutputHandler.chatConfirmation(sender, "Deactivated bubble");
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "on", "off");
        }
        return null;
    }

}