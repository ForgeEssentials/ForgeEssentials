package com.forgeessentials.tickets;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTicket extends ForgeEssentialsCommandBuilder
{
    public CommandTicket(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "ticket";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "tickets" };
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("list")
                        .then(Commands
                                .argument("page",
                                        IntegerArgumentType.integer(1,
                                                (int) Math.ceil((double) ModuleTickets.ticketList.size() / 7)))
                                .executes(CommandContext -> execute(CommandContext, "list")))
                        .executes(CommandContext -> execute(CommandContext, "list-one")))
                .then(Commands.literal("new")
                        .then(Commands.argument("category", StringArgumentType.word()).suggests(SUGGEST_category)
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "new")))))
                .then(Commands.literal("view")
                        .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                .executes(CommandContext -> execute(CommandContext, "view"))))
                .then(Commands.literal("tp")
                        .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                .executes(CommandContext -> execute(CommandContext, "tp"))))
                .then(Commands.literal("del")
                        .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                .executes(CommandContext -> execute(CommandContext, "del"))))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_category = (ctx, builder) -> SharedSuggestionProvider.suggest(ModuleTickets.categories, builder);

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        String c = ChatFormatting.DARK_AQUA.toString();
        if (params.equals("blank"))
        {
            String usage = "list|new|view|tp <id>|del <id>";
            ChatOutputHandler.chatError(ctx.getSource(), "Usage: /ticket <" + usage + ">");
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("view"))
        {
            int id = IntegerArgumentType.getInteger(ctx, "id");
            Ticket t = ModuleTickets.getID(id);
            if (t == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.format("No such ticket with ID %d!", id));
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatNotification(ctx.getSource(),
                    c + "#" + t.id + " : " + t.creator + " - " + t.category + " - " + t.message);
            return Command.SINGLE_SUCCESS;
        }

        if ((params.equals("list") || params.equals("list-one")))
        {
            int page = 1;
            int pages = (int) Math.ceil((double) ModuleTickets.ticketList.size() / 7);
            if (params.equals("list"))
            {
                page = IntegerArgumentType.getInteger(ctx, "page");
            }

            if (ModuleTickets.ticketList.size() == 0)
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), c + "There are no tickets!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatNotification(ctx.getSource(), c + "--- Ticket List ---");
            for (int i = (page * 7) - 7; i < (page + 1) * 7; i++)
            {
                try
                {
                    Ticket t = ModuleTickets.ticketList.get(i);
                    ChatOutputHandler.chatNotification(ctx.getSource(),
                            "#" + t.id + ": " + t.creator + " - " + t.category + " - " + t.message);
                }
                catch (Exception e)
                {
                    break;
                }
            }
            ChatOutputHandler.chatNotification(ctx.getSource(),
                    c + Translator.format("--- Page %1$d of %2$d ---", page, pages));
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("new"))
        {
            String catagory = StringArgumentType.getString(ctx, "category");
            if (!ModuleTickets.categories.contains(catagory))
            {
                ChatOutputHandler.chatError(ctx.getSource(), "message.error.illegalCategory", catagory);
                return Command.SINGLE_SUCCESS;
            }

            String msg = StringArgumentType.getString(ctx, "message");
            Ticket t = new Ticket(ctx.getSource(), catagory, msg);
            ModuleTickets.ticketList.add(t);
            DataManager.getInstance().save(t, Integer.toString(t.id));
            ModuleTickets.FEcurrentID.set(ModuleTickets.currentID);
            ChatOutputHandler.chatNotification(ctx.getSource(),
                    c + Translator.format("Your ticket with ID %d has been posted.", t.id));

            // notify any ticket-admins that are online
            BaseComponent messageComponent = ChatOutputHandler.notification(
                    Translator.format("Player %s has filed a ticket.", ctx.getSource().getDisplayName().getString()));
            if (!ctx.getSource().getServer().isStopped())
                for (ServerPlayer player : ServerUtil.getPlayerList())
                    if (UserIdent.get(player).checkPermission(ModuleTickets.PERMBASE + ".admin"))
                        ChatOutputHandler.sendMessage(player.createCommandSourceStack(), messageComponent);
            ChatOutputHandler.sendMessage(ctx.getSource().getServer().createCommandSourceStack(), messageComponent);
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("tp"))
        {

            int id = IntegerArgumentType.getInteger(ctx, "id");
            Ticket t = ModuleTickets.getID(id);
            if (t == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.format("No such ticket with ID %d!", id));
                return Command.SINGLE_SUCCESS;
            }
            TeleportHelper.teleport((ServerPlayer) ctx.getSource().getEntity(), t.point);
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("del"))
        {
            int id = IntegerArgumentType.getInteger(ctx, "id");
            Ticket toRemove = ModuleTickets.getID(id);
            if (toRemove == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.format("No such ticket with ID %d!", id));
                return Command.SINGLE_SUCCESS;
            }
            ModuleTickets.ticketList.remove(toRemove);
            DataManager.getInstance().delete(Ticket.class, String.valueOf(id));
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    c + Translator.format("Your ticket with ID %d has been removed.", id));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    public List<String> getTicketList()
    {
        List<String> list = new ArrayList<>();
        for (Ticket t : ModuleTickets.ticketList)
        {
            list.add("" + t.id);
        }
        return list;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {

        return DefaultPermissionLevel.ALL;
    }
}
