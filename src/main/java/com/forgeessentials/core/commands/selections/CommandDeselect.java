package com.forgeessentials.core.commands.selections;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

public class CommandDeselect extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "/fedesel";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("/fedeselect", "/deselect", "/sel");
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        PlayerInfo info = PlayerInfo.getPlayerInfo(sender.getPersistentID());
        info.clearSelection();

        OutputHandler.chatConfirmation(sender, "Selection cleared.");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.core.pos.deselect";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "//fedesel Deselects the selection";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }
}
