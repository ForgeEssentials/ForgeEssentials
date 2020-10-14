package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandTempBan extends ParserCommandBase
{

    public static final String PERM_BAN_REASON = "tempban.reason";

    @Override
    public String getPrimaryAlias()
    {
        return "tempban";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/tempban <player> <duration>[s|m|h|d|w|months]: Tempban a player";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".tempban";
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
            player.getPlayerMP().connection.disconnect(new TextComponentTranslation(Translator.format("You have been banned for %s", durationString)));

        if (!arguments.isEmpty())
        {
            String reason = arguments.toString();
            ChatOutputHandler.sendMessage(arguments.server,
                    Translator.format("Player %s, has been temporarily banned for %s. Reason: %s", player.getUsername(), durationString, reason));
            APIRegistry.perms.setPlayerPermissionProperty(player, PERM_BAN_REASON, reason);
        }
        else
        {
            ChatOutputHandler.sendMessage(arguments.server,
                    Translator.format("Player %s, has been temporarily banned for %s", player.getUsername(), durationString));
        }
    }

}
