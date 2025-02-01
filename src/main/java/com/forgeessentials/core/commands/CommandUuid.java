package com.forgeessentials.core.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;

public class CommandUuid extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "uuid";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/uuid [player]: Display a player's UUID";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.commands.uuid";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (!arguments.hasPlayer())
                throw new TranslatedCommandException("Player argument needed!");
            arguments.confirm("UUID = " + arguments.senderPlayer.getPersistentID().toString());
        }
        else
        {
            UserIdent player = arguments.parsePlayer(false, false);
            arguments.confirm("UUID = " + player.getOrGenerateUuid());
        }
    }

}
