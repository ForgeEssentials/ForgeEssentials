package com.forgeessentials.chat.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.ForgeEssentialsCommandBase;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.TranslatedCommandException;
import com.forgeessentials.util.Translator;
import com.forgeessentials.util.Utils;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandMute extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "mute";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/mute <player>: Mutes the specified player.";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.mute";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            EntityPlayerMP receiver = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (receiver == null)
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);

            PlayerUtil.getPersistedTag(receiver, true).setBoolean("mute", true);
            ChatUtil.chatError(sender, Translator.format("You muted %s.", args[0]));
            ChatUtil.chatError(receiver, Translator.format("You were muted by %s.", sender.getCommandSenderName()));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return Utils.getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

}
