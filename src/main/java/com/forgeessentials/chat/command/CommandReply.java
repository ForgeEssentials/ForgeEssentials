package com.forgeessentials.chat.command;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

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
    public List<String> getCommandAliases()
    {
        return Arrays.asList("r");
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/r <message>: Reply to last player that sent you a message";
    }

    @Override
    public String getPermissionNode()
    {
        return null;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
            throw new WrongUsageException("commands.message.usage", new Object[0]);

        ICommandSender target = getReplyTarget(sender);
        if (target == null)
            throw new PlayerNotFoundException();

        if (target == sender)
            throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);

        IChatComponent message = func_147176_a(sender, args, 0, !(sender instanceof EntityPlayer));
        ChatComponentTranslation sentMsg = new ChatComponentTranslation("commands.message.display.incoming", new Object[] { sender.func_145748_c_(),
                message.createCopy() });
        ChatComponentTranslation senderMsg = new ChatComponentTranslation("commands.message.display.outgoing",
                new Object[] { target.func_145748_c_(), message });
        sentMsg.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true));
        senderMsg.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true));
        target.addChatMessage(sentMsg);
        sender.addChatMessage(senderMsg);
        CommandReply.messageSent(sender, target);
    }

}
