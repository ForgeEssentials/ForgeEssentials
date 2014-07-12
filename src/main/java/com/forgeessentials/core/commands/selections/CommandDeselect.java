package com.forgeessentials.core.commands.selections;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Arrays;
import java.util.List;

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
    public void processCommandPlayer(EntityPlayer sender, String[] args)
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
    public String getCommandPerm()
    {
        return "fe.core.pos.deselect";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "//fedesel Deselects the selection";
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.MEMBERS;
    }
}
