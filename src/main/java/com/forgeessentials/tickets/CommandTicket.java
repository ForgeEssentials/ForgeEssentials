package com.forgeessentials.tickets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandTicket extends BaseCommand
{
    public CommandTicket(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "ticket";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "tickets" };
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("list")
                        .then(Commands.argument("page", IntegerArgumentType.integer(0, ModuleTickets.ticketList.size() / 7))
                                .executes(CommandContext -> execute(CommandContext, "list")
                                        )
                                )
                        )
                .then(Commands.literal("new")
                        //.then(Commands.argument("catagory", ModuleTickets.categories)
                        .then(Commands.argument("message", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "new"))
                                        )
                               // )
                        )
                .then(Commands.literal("view")
                        .then(Commands.argument("id", IntegerArgumentType.integer(0, ModuleTickets.currentID + 1))
                                .executes(CommandContext -> execute(CommandContext, "view")
                                        )
                                )
                        )
                .then(Commands.literal("tp")
                        .then(Commands.argument("id", IntegerArgumentType.integer(0, ModuleTickets.currentID + 1))
                                .executes(CommandContext -> execute(CommandContext, "tp")
                                        )
                                )
                        )
                .then(Commands.literal("del")
                        .then(Commands.argument("id", IntegerArgumentType.integer(0, ModuleTickets.currentID ))
                                .executes(CommandContext -> execute(CommandContext, "del")
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        String c = TextFormatting.DARK_AQUA.toString();
        if (params.toString() == "blank")
        {
            String usage = "list|new|view";
            if (permcheck(ctx.getSource(), "tp"))
            {
                usage += "|tp <id>";
            }
            if (permcheck(ctx.getSource(), "admin"))
            {
                usage += "|del <id>";
            }
            throw new TranslatedCommandException("Usage: /ticket <" + usage + ">");
        }

        if (params.toString() == "view" && permcheck(ctx.getSource(), "view"))
        {
            int id = IntegerArgumentType.getInteger(ctx, "id");
            Ticket t = ModuleTickets.getID(id);
            ChatOutputHandler.chatNotification(ctx.getSource(), c + "#" + t.id + " : " + t.creator + " - " + t.category + " - " + t.message);
        }

        if (params.toString() == "list" && permcheck(ctx.getSource(), "view"))
        {
            int page = 0;
            int pages = IntegerArgumentType.getInteger(ctx, "page");

            if (ModuleTickets.ticketList.size() == 0)
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), c + "There are no tickets!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatNotification(ctx.getSource(), c + "--- Ticket List ---");
            for (int i = page * 7; i < (page + 1) * 7; i++)
            {
                try
                {
                    Ticket t = ModuleTickets.ticketList.get(i);
                    ChatOutputHandler.chatNotification(ctx.getSource(), "#" + t.id + ": " + t.creator + " - " + t.category + " - " + t.message);
                }
                catch (Exception e)
                {
                    break;
                }
            }
            ChatOutputHandler.chatNotification(ctx.getSource(), c + Translator.format("--- Page %1$d of %2$d ---", page + 1, pages + 1));
            return Command.SINGLE_SUCCESS;
        }

        if (params.toString() == "new" && permcheck(ctx.getSource(), "new"))
        {
            String catagory = null;
            if (!ModuleTickets.categories.contains(catagory))
                throw new TranslatedCommandException("message.error.illegalCategory", catagory);

            String msg = MessageArgument.getMessage(ctx, "message").getString();
            Ticket t = new Ticket(ctx.getSource(), catagory, msg);
            ModuleTickets.ticketList.add(t);
            ChatOutputHandler.chatNotification(ctx.getSource(), c + Translator.format("Your ticket with ID %d has been posted.", t.id));

            // notify any ticket-admins that are online
            ITextComponent messageComponent = ChatOutputHandler.notification(Translator.format("Player %s has filed a ticket.", ctx.getSource().getEntity().getName().getString()));
            if (!ctx.getSource().getServer().isStopped())
                for (ServerPlayerEntity player : ServerUtil.getPlayerList())
                    if (UserIdent.get(player).checkPermission(ModuleTickets.PERMBASE + ".admin"))
                        ChatOutputHandler.sendMessage(player.createCommandSourceStack(), messageComponent);
            ChatOutputHandler.sendMessage(ctx.getSource().getServer().createCommandSourceStack(), messageComponent);
            return Command.SINGLE_SUCCESS;
        }

        if (params.toString() == "tp" && permcheck(ctx.getSource(), "tp"))
        {

            int id = IntegerArgumentType.getInteger(ctx, "id");
            TeleportHelper.teleport((ServerPlayerEntity) ctx.getSource().getEntity(), ModuleTickets.getID(id).point);
        }

        if (params.toString() == "del" || params.toString() == "close" && permcheck(ctx.getSource(), "admin"))
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
            ChatOutputHandler.chatConfirmation(ctx.getSource(), c + Translator.format("Your ticket with ID %d has been removed.", id));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleTickets.PERMBASE + ".command";
    }

    public List<String> getTicketList()
    {
        List<String> list = new ArrayList<String>();
        for (Ticket t : ModuleTickets.ticketList)
        {
            list.add("" + t.id);
        }
        return list;
    }

    public boolean permcheck(CommandSource sender, String perm)
    {
        if (sender.getEntity() instanceof PlayerEntity)
        {
            return PermissionAPI.hasPermission((PlayerEntity) sender.getEntity(), ModuleTickets.PERMBASE + "." + perm);
        }
        else
        {
            return true;
        }
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {

        return DefaultPermissionLevel.ALL;
    }
}
