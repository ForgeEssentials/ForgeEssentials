package com.forgeessentials.commands.player;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet2Reach;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;

public class CommandReach extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "fereach";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "reach" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/reach <distance>: Set block reach distance";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".reach";
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/reach <distance>: Set block reach distance. Set to 0 to reset.");
            return;
        }

        if (!PlayerInfo.get(arguments.senderPlayer).getHasFEClient())
        {
            arguments.error("You need the FE client addon to use this command");
            return;
        }

        float distance = (float) arguments.parseDouble();
        if (distance < 1)
            distance = 5;

        NetworkUtils.netHandler.sendTo(new Packet2Reach(distance), arguments.senderPlayer);
        arguments.senderPlayer.theItemInWorldManager.setBlockReachDistance(distance);
        arguments.confirm(Translator.format("Set reach distance to %d", (int) distance));
    }

}
