package com.forgeessentials.protection;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.EnumGameType;

public class ProtectCommand extends ForgeEssentialsCommandBase{
    @Override public void processCommand(ICommandSender var1, String[] var2)
    {
        switch(var2.length)
        {
        case 0:
            ChatUtils.sendMessage(var1, "List of settings: gamemode");
            break;
        case 3:
        if (var2[0].equalsIgnoreCase("gamemode"))
        {
            if (!APIRegistry.zones.doesZoneExist(var2[1]))
            {
                ChatUtils.sendMessage(var1, "No zone by the name of " + var2[1] + " exists!");
            }
            AdditionalZoneData data = (AdditionalZoneData)ModuleProtection.itemsList.get(var2[1]);
            data.setGameMode(Integer.parseInt(var2[2]));
            ModuleProtection.itemsList.put(var2[1], data);
            ChatUtils.sendMessage(var1, "Gamemode in zone " + var2[1] + " set to " + EnumGameType.getByID(Integer.parseInt(var2[2])).getName());
        }
            break;

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

    @Override public String getCommandPerm()
    {
        return "fe.protection.protect";
    }

    @Override public RegGroup getReggroup()
    {
        return RegGroup.ZONE_ADMINS;
    }
}
