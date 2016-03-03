package com.forgeessentials.tickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandTicket extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "ticket";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "tickets" };
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        doStuff(sender, args);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        doStuff(sender, args);
    }

    public void doStuff(ICommandSender sender, String[] args) throws CommandException
    {
        String c = EnumChatFormatting.DARK_AQUA.toString();
        if (args.length == 0)
        {
            String usage = "list|new|view";
            if (permcheck(sender, "tp"))
            {
                usage += "|tp <id>";
            }
            if (permcheck(sender, "admin"))
            {
                usage += "|del <id>";
            }
            throw new TranslatedCommandException("Usage: /ticket <" + usage + ">");
        }

        if (args[0].equalsIgnoreCase("view") && permcheck(sender, "view"))
        {
            if (args.length != 2)
                throw new TranslatedCommandException("Usage: /ticket view <id>");
            int id = parseInt(args[1], 0, ModuleTickets.currentID + 1);
            Ticket t = ModuleTickets.getID(id);
            ChatOutputHandler.chatNotification(sender, c + "#" + t.id + " : " + t.creator + " - " + t.category + " - " + t.message);
        }

        if (args[0].equalsIgnoreCase("list") && permcheck(sender, "view"))
        {
            int page = 0;
            int pages = ModuleTickets.ticketList.size() / 7;
            if (args.length == 2)
            {
                page = parseInt(args[1], 0, pages);
            }

            if (ModuleTickets.ticketList.size() == 0)
            {
                ChatOutputHandler.chatNotification(sender, c + "There are no tickets!");
                return;
            }
            ChatOutputHandler.chatNotification(sender, c + "--- Ticket List ---");
            for (int i = page * 7; i < (page + 1) * 7; i++)
            {
                try
                {
                    Ticket t = ModuleTickets.ticketList.get(i);
                    ChatOutputHandler.chatNotification(sender, "#" + t.id + ": " + t.creator + " - " + t.category + " - " + t.message);
                }
                catch (Exception e)
                {
                    break;
                }
            }
            ChatOutputHandler.chatNotification(sender, c + Translator.format("--- Page %1$d of %2$d ---", page + 1, pages + 1));
            return;
        }

        if (args[0].equalsIgnoreCase("new") && permcheck(sender, "new"))
        {
            if (args.length < 3)
                throw new TranslatedCommandException("Usage: /ticket new <category> <message ...>");
            if (!ModuleTickets.categories.contains(args[1]))
                throw new TranslatedCommandException("message.error.illegalCategory", args[1]);

            String msg = "";
            for (String var : Arrays.copyOfRange(args, 2, args.length))
            {
                msg += " " + var;
            }
            msg = msg.substring(1);
            Ticket t = new Ticket(sender, args[1], msg);
            ModuleTickets.ticketList.add(t);
            ChatOutputHandler.chatNotification(sender, c + Translator.format("Your ticket with ID %d has been posted.", t.id));

            // notify any ticket-admins that are online
            IChatComponent messageComponent = ChatOutputHandler.notification(Translator.format("Player %s has filed a ticket.", sender.getName()));
            if (!MinecraftServer.getServer().isServerStopped())
                for (EntityPlayerMP player : ServerUtil.getPlayerList())
                    if (UserIdent.get(player).checkPermission(ModuleTickets.PERMBASE + ".admin"))
                        ChatOutputHandler.sendMessage(player, messageComponent);
            ChatOutputHandler.sendMessage(MinecraftServer.getServer(), messageComponent);
            return;
        }

        if (args[0].equalsIgnoreCase("tp") && permcheck(sender, "tp"))
        {
            if (args.length != 2)
                throw new TranslatedCommandException("Usage: /ticket tp <id>");
            int id = parseInt(args[1], 0, ModuleTickets.currentID + 1);
            TeleportHelper.teleport((EntityPlayerMP) sender, ModuleTickets.getID(id).point);
        }

        if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("close") && permcheck(sender, "admin"))
        {
            if (args.length != 2)
                throw new TranslatedCommandException("Usage: /ticket del <id>");
            int id = Integer.parseInt(args[1]);
            Ticket toRemove = ModuleTickets.getID(id);
            if (toRemove == null)
            {
                ChatOutputHandler.chatError(sender, Translator.format("No such ticket with ID %d!", id));
                return;
            }
            ModuleTickets.ticketList.remove(toRemove);
            ChatOutputHandler.chatConfirmation(sender, c + Translator.format("Your ticket with ID %d has been removed.", id));
        }
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

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "list", "new", "view", "tp", "del");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("new"))
        {
            return getListOfStringsMatchingLastWord(args, ModuleTickets.categories);
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("del")))
        {
            List<String> list = new ArrayList<String>();
            for (Ticket t : ModuleTickets.ticketList)
            {
                list.add("" + t.id);
            }
            return getListOfStringsMatchingLastWord(args, list);
        }
        return null;
    }

    public boolean permcheck(ICommandSender sender, String perm)
    {
        if (sender instanceof EntityPlayer)
        {
            return PermissionManager.checkPermission((EntityPlayer) sender, ModuleTickets.PERMBASE + "." + perm);
        }
        else
        {
            return true;
        }
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        String usage = "list|new|view";
        if (permcheck(sender, "tp"))
        {
            usage += "|tp <id>";
        }
        if (permcheck(sender, "admin"))
        {
            usage += "|del <id>";
        }
        return "Usage: /ticket <" + usage + ">";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {

        return PermissionLevel.TRUE;
    }
}
