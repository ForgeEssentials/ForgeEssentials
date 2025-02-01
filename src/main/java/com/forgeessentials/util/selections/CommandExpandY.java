package com.forgeessentials.util.selections;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;

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
    public String getCommandName()
    {
        return "/expandY";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args) throws CommandException
    {
        Selection sel = SelectionHandler.getSelection(player);
        if (sel == null)
            throw new TranslatedCommandException("Invalid selection.");
        SelectionHandler.setStart(player, sel.getStart().setY(0));
        SelectionHandler.setEnd(player, sel.getEnd().setY(MinecraftServer.getServer().getBuildLimit()));
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
    public String getCommandUsage(ICommandSender sender)
    {
        return "//expandY: Expands the currently selected area from the top to the bottom of the world.";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

}
