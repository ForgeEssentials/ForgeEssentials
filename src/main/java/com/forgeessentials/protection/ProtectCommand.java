package com.forgeessentials.protection;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldSettings;

public class ProtectCommand extends ForgeEssentialsCommandBase{
    @Override public void processCommand(ICommandSender var1, String[] var2)
    {
        switch(var2.length)
        {
        case 0:
            ChatUtils.sendMessage(var1, "List of settings: gamemode");
            break;
        /*
        case 3:
        if (var2[0].equalsIgnoreCase("gamemode"))
        {
            *//*Zone zone = APIRegistry.zones.getZone(var2[1]);
            for (Group g : APIRegistry.perms.getGroupsInZone(zone.getZoneName()))
            {
                APIRegistry.perms.setGroupPermissionProp(g.name, ModuleProtection.PERMPROP_ZONE_GAMEMODE, var2[2], zone.getZoneName());
            }
            if (var1 instanceof EntityPlayer)
            {
                ((EntityPlayer)var1).setGameType(EnumGameType.getByID(Integer.parseInt(var2[2])));
            }
            ChatUtils.sendMessage(var1, String.format("Successfully set gamemode of zone %s1 to %s2", zone.getZoneName(), EnumGameType.getByID(Integer.parseInt(var2[2])).getName()));*//*
        }
            break;
            */

        default:
            ChatUtils.sendMessage(var1, "Command syntax is wrong. Try " + getCommandUsage(var1));
            break;
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

    @Override public RegGroup getReggroup()
    {
        return RegGroup.ZONE_ADMINS;
    }
}
