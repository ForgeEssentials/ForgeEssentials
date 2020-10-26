package com.forgeessentials.commands.player;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandDoAs extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "doas";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {

        return "/doas <player> <command> Run a command as another player.";
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
        return ModuleCommands.PERM + ".doas";
    }

    @Override
    public void registerExtraPermissions()
    {
        PermissionAPI.registerNode("fe.commands.doas.console", DefaultPermissionLevel.OP, "Use /doas as the console");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            ChatOutputHandler.chatError(sender, getUsage(sender));
            return;
        }
        if ((sender instanceof EntityPlayerMP) && args[0].equalsIgnoreCase("[CONSOLE]"))
        {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            if (!PermissionAPI.hasPermission(player, "fe.commands.doas.console"))
                throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

            if (args.length < 2)
                throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);

            args = Arrays.copyOfRange(args, 1, args.length);
            String cmd = StringUtils.join(args, " ");
            server.getCommandManager().executeCommand(new DoAsCommandSender(APIRegistry.IDENT_SERVER, player), cmd);
        }

        StringBuilder cmd = new StringBuilder(args.toString().length());
        for (int i = 1; i < args.length; i++)
        {
            cmd.append(args[i]);
            cmd.append(" ");
        }
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player != null)
        {
            ChatOutputHandler.chatWarning(player, Translator.format("Player %s is attempting to issue a command as you.", sender.getName()));
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(player, cmd.toString());
            ChatOutputHandler.chatConfirmation(sender, Translator.format("Successfully issued command as %s", args[0]));
        }
        else
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return matchToPlayers(args);
        }
        else
        {
            return null;
        }
    }

}
