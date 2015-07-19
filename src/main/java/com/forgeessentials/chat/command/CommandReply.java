package com.forgeessentials.chat.command;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandReply extends ForgeEssentialsCommandBase
{

    public static Map<ICommandSender, WeakReference<ICommandSender>> replyMap = new WeakHashMap<>();

    public static void messageSent(ICommandSender argFrom, ICommandSender argTo)
    {
        replyMap.put(argTo, new WeakReference<ICommandSender>(argFrom));
    }

    public static ICommandSender getReplyTarget(ICommandSender sender)
    {
        WeakReference<ICommandSender> replyTarget = replyMap.get(sender);
        if (replyTarget == null)
            return null;
        return replyTarget.get();
    }

    /* ------------------------------------------------------------ */

    @Override
    public String getCommandName()
    {
        return "reply";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "r" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/r <message>: Reply to last player that sent you a message";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleChat.PERM + ".reply";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
            throw new WrongUsageException("commands.message.usage", new Object[0]);

        ICommandSender target = getReplyTarget(sender);
        if (target == null)
            throw new PlayerNotFoundException("No reply target found");

        if (target == sender)
            throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);

        ModuleChat.tell(sender, getChatComponentFromNthArg(sender, args, 0, !(sender instanceof EntityPlayer)), target);
    }

}
