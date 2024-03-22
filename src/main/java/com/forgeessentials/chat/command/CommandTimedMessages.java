package com.forgeessentials.chat.command;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.BaseComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTimedMessages extends ForgeEssentialsCommandBuilder
{

    public CommandTimedMessages(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "timedmessage";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "tm" };
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
                .then(Commands.literal("add")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(CommandContext -> execute(CommandContext, "add"))))
                .then(Commands.literal("list").executes(CommandContext -> execute(CommandContext, "list")))
                .then(Commands.literal("delete")
                        .then(Commands.argument("number", IntegerArgumentType.integer(0))
                                .executes(CommandContext -> execute(CommandContext, "delete"))))
                .then(Commands.literal("send")
                        .then(Commands.argument("number", IntegerArgumentType.integer(0))
                                .executes(CommandContext -> execute(CommandContext, "send"))))
                .then(Commands.literal("interval")
                        .then(Commands.argument("number", IntegerArgumentType.integer())
                                .executes(CommandContext -> execute(CommandContext, "interval"))))
                .then(Commands.literal("shuffle")
                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                .executes(CommandContext -> execute(CommandContext, "shuffle"))))
                .executes(CommandContext -> execute(CommandContext, "help"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm add <message>: Add a message");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm list: List all messages");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm delete <id>: Delete a timed message");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm send <id>: Send a message");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tm interval <sec>: Set message interval");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/tm shuffle <true|false>: Enable/disable shuffling of messages");
            return Command.SINGLE_SUCCESS;
        }

        switch (params)
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
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND);
        }
        return Command.SINGLE_SUCCESS;
    }

    public void parseAdd(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        String message = StringArgumentType.getString(ctx, "message");
        if (message.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/timedmessage add <message...>: Add a timed message");
            return;
        }
        ModuleChat.timedMessages.addMessage(message);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Added new message:");
        ChatOutputHandler.sendMessage(ctx.getSource(), ModuleChat.timedMessages.formatMessage(message));
        ModuleChat.timedMessages.save(false);
    }

    public void parseList(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "List of messages:");
        for (int i = 0; i < ModuleChat.timedMessages.getMessages().size(); i++)
        {
            BaseComponent message = new TextComponent(String.format("%d: ", i));
            message.append(ModuleChat.timedMessages.formatMessage(ModuleChat.timedMessages.getMessages().get(i)));
            ChatOutputHandler.sendMessage(ctx.getSource(), message);
        }
    }

    public void parseDelete(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        int num = IntegerArgumentType.getInteger(ctx, "number");
        if (num > ModuleChat.timedMessages.getMessages().size() - 1)
        {
            ChatOutputHandler.chatError(ctx.getSource(), Translator.format("No such message with Index %d!", num));
            return;
        }
        ModuleChat.timedMessages.getMessages().remove(num);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Removed message");
        ModuleChat.timedMessages.save(false);
    }

    public void parseSend(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        int index = IntegerArgumentType.getInteger(ctx, "number");
        if (index > ModuleChat.timedMessages.getMessages().size() - 1)
        {
            ChatOutputHandler.chatError(ctx.getSource(), Translator.format("No such message with Index %d!", index));
            return;
        }
        ModuleChat.timedMessages.broadcastMessage(index);
    }

    public void parseInterval(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        int index = IntegerArgumentType.getInteger(ctx, "number");
        ModuleChat.timedMessages.setInterval(index);
        ModuleChat.timedMessages.save(false);
    }

    public void parseShuffle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
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
