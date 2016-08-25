package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandTempBan extends ForgeEssentialsCommandBase
{

    public static final String PERM_BAN_REASON = "tempban.reason";

    @Override
    public String getCommandName()
    {
        return "fetempban";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "tempban" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/tempban <player> <duration>[s|m|h|d|w|months]: Tempban a player";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".tempban";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender);
        parse(arguments);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender, true);
        try
        {
            parse(arguments);
        }
        catch (CommandException e)
        {
            return arguments.tabCompletion;
        }
        return arguments.tabCompletion;
    }

    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        UserIdent player = arguments.parsePlayer(true, false);

        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        long duration = arguments.parseTimeReadable();

        PlayerInfo pi = PlayerInfo.get(player.getUuid());
        pi.startTimeout("tempban", duration);

        String durationString = ChatOutputHandler.formatTimeDurationReadable(duration / 1000, true);
        if (player.hasPlayer())
            player.getPlayerMP().playerNetServerHandler.kickPlayerFromServer(Translator.format("You have been banned for %s", durationString));

        if (!arguments.isEmpty())
        {
            String reason = arguments.toString();
            ChatOutputHandler.sendMessage(MinecraftServer.getServer(),
                    Translator.format("Player %s, has been temporarily banned for %s. Reason: %s", player.getUsername(), durationString, reason));
            APIRegistry.perms.setPlayerPermissionProperty(player, PERM_BAN_REASON, reason);
        }
        else
        {
            ChatOutputHandler.sendMessage(MinecraftServer.getServer(),
                    Translator.format("Player %s, has been temporarily banned for %s", player.getUsername(), durationString));
        }
    }

}
