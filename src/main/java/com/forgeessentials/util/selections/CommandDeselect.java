package com.forgeessentials.util.selections;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandDeselect extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "/desel";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "/deselect", "/deselect", "/sel" };
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        PlayerInfo info = PlayerInfo.get(sender.getPersistentID());
        info.setSel1(null);
        info.setSel2(null);
        SelectionHandler.sendUpdate(sender);
        ChatOutputHandler.chatConfirmation(sender, "Selection cleared.");
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
    public String getUsage(ICommandSender sender)
    {
        return "//fedesel Deselects the selection";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }
}
