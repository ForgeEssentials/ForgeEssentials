package com.forgeessentials.remote.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet7Remote;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.remote.ModuleRemote;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandRemote extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "remote";
    }

    private static final String[] parseMainArgs = { "regen", "setkey", "kick", "start", "stop", "block", "qr" };

    @Override
    public void processCommand(ICommandSender sender, String[] vargs) throws CommandException
    {
        CommandParserArgs args = new CommandParserArgs(this, vargs, sender);
        parse(args);
    }

    /**
     * @param args
     */
    public void parse(CommandParserArgs args) throws CommandException
    {
        if (args.isTabCompletion && args.size() == 1)
        {
            args.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseMainArgs);
            return;
        }
        if (args.isEmpty())
        {
            if (!args.hasPlayer())
                throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            showPasskey(args, args.ident, false);
        }
        else
        {
            String arg = args.remove();
            switch (arg)
            {
            case "help":
            {
                args.confirm("/remote start: Start remote server (= enable)");
                args.confirm("/remote stop: Stop remote server (= disable)");
                args.confirm("/remote regen [player]: Generate new passkey");
                args.confirm("/remote setkey <player> <key>: Set your own passkey");
                args.confirm("/remote block <player>: Block player from remote, until he generates a new passkey");
                args.confirm("/remote kick <player>: Kick player accessing remote right now");
                return;
            }
            case "regen":
            {
                UserIdent ident = args.parsePlayer(false, false);
                if (!ident.equals(args.ident))
                    args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                ModuleRemote.getInstance().setPasskey(ident, ModuleRemote.getInstance().generatePasskey());
                args.confirm("Generated new passkey");
                showPasskey(args, ident, false);
                return;
            }
            case "setkey":
            {
                UserIdent ident = args.parsePlayer(false, false);
                if (!ident.equals(args.ident))
                    args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isEmpty())
                    throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
                String key = args.remove();
                if (args.isTabCompletion)
                    return;
                ModuleRemote.getInstance().setPasskey(ident, key);
                args.confirm(Translator.format("Passkey of %s changed to %s", ident.getUsernameOrUuid(), key));
                showPasskey(args, ident, true);
                return;
            }
            case "block":
            {
                UserIdent ident = args.parsePlayer(true, false);
                if (!ident.hasUuid())
                    throw new TranslatedCommandException("Player %s not found", ident.getUsernameOrUuid());
                args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                ModuleRemote.getInstance().setPasskey(ident, null);
                args.confirm("User %s has been blocked from remote until he generates a new passkey", ident.getUsernameOrUuid());
                return;
            }
            case "kick":
            {
                UserIdent ident = args.parsePlayer(true, false);
                if (!ident.hasUuid())
                    throw new TranslatedCommandException("Player %s not found", ident.getUsernameOrUuid());
                args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                RemoteSession session = ModuleRemote.getInstance().getServer().getSession(ident);
                if (session == null)
                {
                    args.confirm("User %s is not logged in on remote", ident.getUsernameOrUuid());
                    return;
                }
                session.close("kick", 0);
                args.confirm("User %s has been kicked from remote", ident.getUsernameOrUuid());
                return;
            }
            case "start":
            {
                args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                if (ModuleRemote.getInstance().getServer() != null)
                    throw new TranslatedCommandException("Server already running on port " + ModuleRemote.getInstance().getPort());
                ModuleRemote.getInstance().startServer();
                if (ModuleRemote.getInstance().getServer() == null)
                    args.confirm("Error starting remote server");
                else
                    args.confirm("Server started");
                return;
            }
            case "stop":
            {
                args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                if (ModuleRemote.getInstance().getServer() == null)
                    throw new TranslatedCommandException("Server not running");
                ModuleRemote.getInstance().stopServer();
                args.confirm("Server stopped");
                return;
            }
            case "qr":
            {
                UserIdent ident = args.parsePlayer(true, true);
                if (!PlayerInfo.get(ident.getPlayerMP()).getHasFEClient())
                {
                    showPasskey(args, args.ident, false);
                }
                else
                {
                    String connectString = ModuleRemote.getInstance().getConnectString(ident);
                    String url = ("https://chart.googleapis.com/chart?cht=qr&chld=M|4&chs=547x547&chl=" + connectString).replaceAll("\\|", "%7C");
                    NetworkUtils.netHandler.sendTo(new Packet7Remote(url), ident.getPlayerMP());
                }
                return;
            }
            default:
                throw new TranslatedCommandException("Unknown subcommand " + arg);
            }
        }
    }

    /**
     * @param sender
     * @param args
     * @param ident
     */
    public void showPasskey(CommandParserArgs args, UserIdent ident, boolean hideKey)
    {
        String passkey = ModuleRemote.getInstance().getPasskey(ident);
        if (hideKey && !ident.hasPlayer())
            passkey = passkey.replaceAll(".", "*");
        String connectString = ModuleRemote.getInstance().getConnectString(ident);
        String url = ("https://chart.googleapis.com/chart?cht=qr&chld=M|4&chs=547x547&chl=" + connectString).replaceAll("\\|", "%7C");
        ChatComponentTranslation msg = new ChatComponentTranslation("Remote passkey = " + passkey + " ");

        IChatComponent qrLink = new ChatComponentText("[QR code]");
        if (ident.hasUuid() && PlayerInfo.get(ident.getUuid()).getHasFEClient())
            qrLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/remote qr"));
        else
            qrLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        qrLink.getChatStyle().setColor(EnumChatFormatting.RED);
        qrLink.getChatStyle().setUnderlined(true);
        msg.appendSibling(qrLink);

        ChatOutputHandler.sendMessage(args.sender, msg);
        ChatOutputHandler.sendMessage(args.sender, new ChatComponentText("Port = " + ModuleRemote.getInstance().getPort()));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] vargs, BlockPos pos)
    {
        try
        {
            CommandParserArgs args = new CommandParserArgs(this, vargs, sender, true);
            parse(args);
            return args.tabCompletion;
        }
        catch (CommandException e)
        {
            return null;
        }
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleRemote.PERM;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/remoteqr: Prints a link remote access QR code";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

}
