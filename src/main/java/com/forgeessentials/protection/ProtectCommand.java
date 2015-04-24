package com.forgeessentials.protection;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class ProtectCommand extends ForgeEssentialsCommandBase{
    
    @Override public void processCommand(ICommandSender player, String[] var2)
    {
        switch(var2.length)
        {
        case 0:
            OutputHandler.chatNotification(player, "List of settings: gamemode");
            break;
        /*
        case 3:
        if (var2[0].equalsIgnoreCase("gamemode"))
        {
            *//*Zone zone = APIRegistry.perms.getZone(var2[1]);
            for (Group g : APIRegistry.perms.getGroupsInZone(zone.getName()))
            {
                APIRegistry.perms.setGroupPermissionProp(g.name, ModuleProtection.PERMPROP_ZONE_GAMEMODE, var2[2], zone.getName());
            }
            if (var1 instanceof EntityPlayer)
            {
                ((EntityPlayer)var1).setGameType(EnumGameType.getByID(Integer.parseInt(var2[2])));
            }
            OutputHandler.chatConfirmation(var1, String.format("Successfully set gamemode of zone %s1 to %s2", zone.getName(), EnumGameType.getByID(Integer.parseInt(var2[2])).getName()));*//*
        }
            break;
            */

        default:
            throw new TranslatedCommandException("Syntax error. Try %s", getCommandUsage(player));
        }
    }

    @Override public String getCommandName()
    {
        return "protect";
    }

    @Override public String getCommandUsage(ICommandSender sender)
    {
        return "/protect <setting> <zoneName> <value>";
    }

    @Override public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override public String getPermissionNode()
    {
        return "fe.protection.protect";
    }

    @Override public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }
}
