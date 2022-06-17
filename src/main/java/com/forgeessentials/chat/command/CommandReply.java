package com.forgeessentials.chat.command;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.Translator;

public class CommandReply extends ForgeEssentialsCommandBase
{

    public static Map<CommandSource, WeakReference<CommandSource>> replyMap = new WeakHashMap<>();

    public static void messageSent(CommandSource argFrom, CommandSource argTo)
    {
        replyMap.put(argTo, new WeakReference<CommandSource>(argFrom));
    }

    public static CommandSource getReplyTarget(CommandSource sender)
    {
        WeakReference<CommandSource> replyTarget = replyMap.get(sender);
        if (replyTarget == null)
            return null;
        return replyTarget.get();
    }

    /* ------------------------------------------------------------ */

    @Override
    public String getPrimaryAlias()
    {
        return "reply";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "r" };
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/r <message>: Reply to last player that sent you a message";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleChat.PERM + ".reply";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
            throw new CommandException("commands.message.usage", new Object[0]);

        CommandSource target = getReplyTarget(sender);
        if (target == null)
            throw new CommandException(Translator.translateITC("No reply target found"));

        if (target == sender)
            throw new CommandException("commands.message.sameTarget", new Object[0]);

        ModuleChat.tell(sender, getChatComponentFromNthArg(sender, args, 0, !(sender instanceof PlayerEntity)), target);
    }

}
