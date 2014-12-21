package com.forgeessentials.remote.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.remote.ModuleRemote;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

public class CommandRemotePasskey extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "remotekey";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        String passkey = ModuleRemote.getInstance().getPasskey(new UserIdent(sender));
        OutputHandler.chatConfirmation(sender, "Remote key = " + passkey);
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleRemote.PERM;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/remotekey: Shows passkey for remote access";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

}
