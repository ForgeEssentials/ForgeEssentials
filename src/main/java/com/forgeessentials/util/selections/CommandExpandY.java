package com.forgeessentials.util.selections;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandExpandY extends ForgeEssentialsCommandBase
{

    public CommandExpandY()
    {
        return;
    }

    @Override
    public String getPrimaryAlias()
    {
        return "/expandY";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP player, String[] args) throws CommandException
    {
        Selection sel = SelectionHandler.getSelection(player);
        if (sel == null)
            throw new TranslatedCommandException("Invalid selection.");
        SelectionHandler.setStart(player, sel.getStart().setY(0));
        SelectionHandler.setEnd(player, sel.getEnd().setY(server.getBuildLimit()));
        ChatOutputHandler.chatConfirmation(player, "Selection expanded from bottom to top.");
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.core.pos.expandy";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "//expandY: Expands the currently selected area from the top to the bottom of the world.";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

}
