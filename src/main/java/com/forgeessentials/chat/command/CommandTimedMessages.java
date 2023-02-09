package com.forgeessentials.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandTimedMessages extends ForgeEssentialsCommandBuilder
{

    public CommandTimedMessages(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "timedmessage";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "tm" };
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.timedmessage";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("add")
                        .then(Commands.argument("message", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "add")
                                        )
                                )
                        )
                .then(Commands.literal("list")
                        .executes(CommandContext -> execute(CommandContext, "list")
                                )
                        )
                .then(Commands.literal("delete")
                        .then(Commands.argument("number", IntegerArgumentType.integer(0, ModuleChat.timedMessages.getMessages().size()-1))
                                .executes(CommandContext -> execute(CommandContext, "delete")
                                        )
                                )
                        )
                .then(Commands.literal("send")
                        .then(Commands.argument("number", IntegerArgumentType.integer(0,ModuleChat.timedMessages.getMessages().size()-1))
                                .executes(CommandContext -> execute(CommandContext, "send")
                                        )
                                )
                        )
                .then(Commands.literal("interval")
                        .then(Commands.argument("number", IntegerArgumentType.integer())
                                .executes(CommandContext -> execute(CommandContext, "interval")
                                        )
                                )
                        )
                .then(Commands.literal("shuffle")
                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                .executes(CommandContext -> execute(CommandContext, "shuffle")
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "help")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "help")
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm add <message>: Add a message");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm list: List all messages");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm delete <id>: Delete a timed message");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm send <id>: Send a message");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm interval <sec>: Set message interval");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm shuffle <true|false>: Enable/disable shuffling of messages");
            return Command.SINGLE_SUCCESS;
        }

        String option = params.toString();
        switch (option)
        {
        case "add":
            parseAdd(ctx);
            break;
        case "list":
            parseList(ctx);
            break;
        case "delete":
            parseDelete(ctx);
            break;
        case "send":
            parseSend(ctx);
            break;
        case "interval":
            parseInterval(ctx);
            break;
        case "shuffle":
            parseShuffle(ctx);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, option);
        }
        return Command.SINGLE_SUCCESS;
    }

    public void parseAdd(CommandContext<CommandSource> ctx) throws CommandSyntaxException
    {
        String message = MessageArgument.getMessage(ctx, "message").getString();
        if (message.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/timedmessage add <message...>: Add a timed message");
            return;
        }
        ModuleChat.timedMessages.addMessage(message);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Added new message:");
        ChatOutputHandler.sendMessage(ctx.getSource(),ModuleChat.timedMessages.formatMessage(message));
        ModuleChat.timedMessages.save(false);
    }

    public void parseList(CommandContext<CommandSource> ctx) throws CommandSyntaxException
    {
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "List of messages:");
        for (int i = 0; i < ModuleChat.timedMessages.getMessages().size(); i++)
            ChatOutputHandler.sendMessage(ctx.getSource(),new TranslationTextComponent(String.format("%d: %s", i, ModuleChat.timedMessages.formatMessage(ModuleChat.timedMessages.getMessages().get(i)))));
    }

    public void parseDelete(CommandContext<CommandSource> ctx) throws CommandSyntaxException
    {
        int num = IntegerArgumentType.getInteger(ctx, "number");
        ModuleChat.timedMessages.getMessages().remove(num);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Removed message");
        ModuleChat.timedMessages.save(false);
    }

    public void parseSend(CommandContext<CommandSource> ctx) throws CommandSyntaxException
    {
        int index = IntegerArgumentType.getInteger(ctx, "number");
        ModuleChat.timedMessages.broadcastMessage(index);
    }

    public void parseInterval(CommandContext<CommandSource> ctx) throws CommandSyntaxException
    {
        int index = IntegerArgumentType.getInteger(ctx, "number");
        ModuleChat.timedMessages.setInterval(index);
        ModuleChat.timedMessages.save(false);
    }

    public void parseShuffle(CommandContext<CommandSource> ctx) throws CommandSyntaxException
    {
        boolean newShuffle = BoolArgumentType.getBool(ctx, "bool");
        if (newShuffle != ModuleChat.timedMessages.getShuffle())
        {
            ModuleChat.timedMessages.setShuffle(newShuffle);
            ModuleChat.timedMessages.initMessageOrder();
            ModuleChat.timedMessages.save(false);
        }
    }
}
