package com.forgeessentials.remote.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.remote.ModuleRemote;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.UserIdent;

public class CommandRemote extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "remote";
    }

    private static final String[] parseMainArgs = { "regen", "kick", "start", "stop", "block" };

    @Override
    public void processCommand(ICommandSender sender, String[] vargs)
    {
        CommandParserArgs args = new CommandParserArgs(this, vargs, sender);
        parse(args);
    }

    /**
     * @param args
     */
    public void parse(CommandParserArgs args)
    {
        if (args.isTabCompletion && args.size() == 1)
        {
            args.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseMainArgs);
            return;
        }
        if (args.isEmpty())
        {
            if (!args.hasPlayer())
                throw new CommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            showPasskey(args, args.userIdent);
        }
        else
        {
            String arg = args.remove();
            switch (arg)
            {
            case "regen":
            {
                UserIdent ident = args.parsePlayer();
                if (ident == null)
                    return;
                if (!ident.hasUUID())
                    throw new CommandException(String.format("Player %s not found", ident.getUsernameOrUUID()));
                if (!ident.equals(args.userIdent))
                    args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                ModuleRemote.getInstance().setPasskey(ident, ModuleRemote.getInstance().generatePasskey());
                args.info("Generated new passkey");
                showPasskey(args, ident);
                return;
            }
            case "block":
            {
                UserIdent ident = args.parsePlayer();
                if (ident == null)
                    return;
                if (!ident.hasUUID())
                    throw new CommandException(String.format("Player %s not found", ident.getUsernameOrUUID()));
                args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                ModuleRemote.getInstance().setPasskey(ident, null);
                args.info(String.format("User %s has been blocked from remote until he generates a new passkey", ident.getUsernameOrUUID()));
                return;
            }
            case "kick":
            {
                UserIdent ident = args.parsePlayer();
                if (ident == null)
                    return;
                if (!ident.hasUUID())
                    throw new CommandException(String.format("Player %s not found", ident.getUsernameOrUUID()));
                args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                RemoteSession session = ModuleRemote.getInstance().getServer().getSession(ident);
                if (session == null)
                {
                    args.info(String.format("User %s is not logged in on remote", ident.getUsernameOrUUID()));
                    return;
                }
                session.close("kick", 0);
                args.info(String.format("User %s has been kicked from remote", ident.getUsernameOrUUID()));
                return;
            }
            case "start":
            {
                args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                if (ModuleRemote.getInstance().getServer() != null)
                    throw new CommandException("Server already running on port " + ModuleRemote.getInstance().getPort());
                ModuleRemote.getInstance().startServer();
                if (ModuleRemote.getInstance().getServer() == null)
                    args.info("Error starting remote server");
                else
                    args.info("Server started");
                return;
            }
            case "stop":
            {
                args.checkPermission(ModuleRemote.PERM_CONTROL);
                if (args.isTabCompletion)
                    return;
                if (ModuleRemote.getInstance().getServer() == null)
                    throw new CommandException("Server not running");
                ModuleRemote.getInstance().stopServer();
                args.info("Server stopped");
                return;
            }
            default:
                throw new CommandException("Unknown subcommand " + arg);
            }
        }
    }

    /**
     * @param sender
     * @param args
     * @param ident
     */
    public void showPasskey(CommandParserArgs args, UserIdent ident)
    {
        String connectString = ModuleRemote.getInstance().getConnectString(ident);
        String url = ("https://chart.googleapis.com/chart?cht=qr&chld=M|4&chs=547x547&chl=" + connectString).replaceAll("\\|", "%7C");
        
        ChatComponentTranslation msg = new ChatComponentTranslation("Remote passkey = " + ModuleRemote.getInstance().getPasskey(ident) + " ");
        
        IChatComponent qrLink = new ChatComponentText("[QR code]");
        qrLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        qrLink.getChatStyle().setColor(EnumChatFormatting.RED);
        qrLink.getChatStyle().setUnderlined(true);
        msg.appendSibling(qrLink);
        
        args.sender.addChatMessage(msg);
        args.sender.addChatMessage(new ChatComponentText("Port = " + ModuleRemote.getInstance().getPort()));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] vargs)
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

}
