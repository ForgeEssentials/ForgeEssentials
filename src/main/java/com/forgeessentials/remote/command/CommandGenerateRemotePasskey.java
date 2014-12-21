package com.forgeessentials.remote.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.remote.ModuleRemote;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

public class CommandGenerateRemotePasskey extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "remotekeygen";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        String passkey = ModuleRemote.getInstance().generatePasskey();
        ModuleRemote.getInstance().setPasskey(new UserIdent(sender), passkey);
        OutputHandler.chatConfirmation(sender, "New remote key = " + passkey);
    }

    @Override
    public String getPermissionNode()
    {
        return null;
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
