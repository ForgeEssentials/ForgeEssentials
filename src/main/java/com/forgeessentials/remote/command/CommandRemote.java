package com.forgeessentials.remote.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet7Remote;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.remote.ModuleRemote;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandRemote extends ForgeEssentialsCommandBuilder
{

    public CommandRemote(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "remote";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("regen")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "regen")
                                        )
                                )
                        )
                .then(Commands.literal("setkey")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("key", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext, "setkey")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("block")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "block")
                                        )
                                )
                        )
                .then(Commands.literal("kick")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "kick")
                                        )
                                )
                        )
                .then(Commands.literal("start")
                        .executes(CommandContext -> execute(CommandContext, "start")
                                )
                        )
                .then(Commands.literal("stop")
                        .executes(CommandContext -> execute(CommandContext, "stop")
                                )
                        )
                .then(Commands.literal("qr")
                        .executes(CommandContext -> execute(CommandContext, "qr")
                                )
                        )
                ;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        switch (params.toString())
        {
        case "help":
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/remote start: Start remote server (= enable)");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/remote stop: Stop remote server (= disable)");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/remote regen [player]: Generate new passkey");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/remote setkey <player> <key>: Set your own passkey");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/remote block <player>: Block player from remote, until he generates a new passkey");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/remote kick <player>: Kick player accessing remote right now");
            return Command.SINGLE_SUCCESS;
        }
        case "regen":
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player"); 
            if(!hasPermission(ctx.getSource(), ModuleRemote.PERM_CONTROL)) {return Command.SINGLE_SUCCESS;}
            ModuleRemote.getInstance().setPasskey(getIdent(player), ModuleRemote.getInstance().generatePasskey());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Generated new passkey");
            showPasskey(ctx.getSource(), getIdent(player), false);
            return Command.SINGLE_SUCCESS;
        }
        case "setkey":
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            String key = StringArgumentType.getString(ctx, "key");
            if(!hasPermission(ctx.getSource(), ModuleRemote.PERM_CONTROL)) {return Command.SINGLE_SUCCESS;}
            ModuleRemote.getInstance().setPasskey(getIdent(player), key);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Passkey of %s changed to %s", getIdent(player).getUsernameOrUuid(), key));
            showPasskey(ctx.getSource(), getIdent(player), true);
            return Command.SINGLE_SUCCESS;
        }
        case "block":
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player"); 
            if (!getIdent(player).hasUuid())
                throw new TranslatedCommandException("Player %s not found", getIdent(player).getUsernameOrUuid());
            if(!hasPermission(ctx.getSource(), ModuleRemote.PERM_CONTROL)) {return Command.SINGLE_SUCCESS;}
            ModuleRemote.getInstance().setPasskey(getIdent(player), null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("User %s has been blocked from remote until he generates a new passkey", getIdent(player).getUsernameOrUuid()));
            return Command.SINGLE_SUCCESS;
        }
        case "kick":
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player"); 
            if (!getIdent(player).hasUuid())
                throw new TranslatedCommandException("Player %s not found", getIdent(player).getUsernameOrUuid());
            if(!hasPermission(ctx.getSource(), ModuleRemote.PERM_CONTROL)) {return Command.SINGLE_SUCCESS;}
            RemoteSession session = ModuleRemote.getInstance().getServer().getSession(getIdent(player));
            if (session == null)
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("User %s is not logged in on remote", getIdent(player).getUsernameOrUuid()));
                return Command.SINGLE_SUCCESS;
            }
            session.close("kick", 0);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("User %s has been kicked from remote", getIdent(player).getUsernameOrUuid()));
            return Command.SINGLE_SUCCESS;
        }
        case "start":
        {
            if(!hasPermission(ctx.getSource(), ModuleRemote.PERM_CONTROL)) {return Command.SINGLE_SUCCESS;}
            if (ModuleRemote.getInstance().getServer() != null)
                throw new TranslatedCommandException("Server already running on port " + ModuleRemote.getInstance().getPort());
            ModuleRemote.getInstance().startServer();
            if (ModuleRemote.getInstance().getServer() == null)
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Error starting remote server");
            else
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Server started");
            return Command.SINGLE_SUCCESS;
        }
        case "stop":
        {
            if(!hasPermission(ctx.getSource(), ModuleRemote.PERM_CONTROL)) {return Command.SINGLE_SUCCESS;}
            if (ModuleRemote.getInstance().getServer() == null)
                throw new TranslatedCommandException("Server not running");
            ModuleRemote.getInstance().stopServer();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Server stopped");
            return Command.SINGLE_SUCCESS;
        }
        case "qr":
        {
            if (!PlayerInfo.get(getIdent(ctx.getSource()).getPlayerMP()).getHasFEClient())
            {
                showPasskey(ctx.getSource(), getIdent(ctx.getSource()), false);
            }
            else
            {
                String connectString = ModuleRemote.getInstance().getConnectString(getIdent(ctx.getSource()));
                String url = ("https://chart.googleapis.com/chart?cht=qr&chld=M|4&chs=547x547&chl=" + connectString).replaceAll("\\|", "%7C");
                NetworkUtils.sendTo(new Packet7Remote(url), getIdent(ctx.getSource()).getPlayerMP());
            }
            return Command.SINGLE_SUCCESS;
        }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * @param sender
     * @param args
     * @param ident
     */
    public void showPasskey(CommandSource source, UserIdent ident, boolean hideKey)
    {
        String passkey = ModuleRemote.getInstance().getPasskey(ident);
        if (hideKey && !ident.hasPlayer())
            passkey = passkey.replaceAll(".", "*");
        String connectString = ModuleRemote.getInstance().getConnectString(ident);
        String url = ("https://chart.googleapis.com/chart?cht=qr&chld=M|4&chs=547x547&chl=" + connectString).replaceAll("\\|", "%7C");

        TextComponent qrLink = new StringTextComponent("[QR code]");
        if (ident.hasUuid() && PlayerInfo.get(ident.getUuid()).getHasFEClient())
            qrLink.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/remote qr"));
        else
            qrLink.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        qrLink.withStyle(TextFormatting.RED);
        qrLink.withStyle(TextFormatting.UNDERLINE);
        TextComponent msg = new StringTextComponent("Remote passkey = " + passkey + " ");
        msg.append(qrLink);

        ChatOutputHandler.sendMessage(source, msg);
        ChatOutputHandler.sendMessage(source, new StringTextComponent("Port = " + ModuleRemote.getInstance().getPort()));
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
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }
}
