package com.forgeessentials.chat.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.commons.CommandParserArgs;
import com.forgeessentials.util.ParserCommandBase;
import com.forgeessentials.util.TranslatedCommandException;

public class CommandGroupMessage extends ParserCommandBase
{

    public static final String PERM = "fe.chat.groupmessage";

    @Override
    public String getCommandName()
    {
        return "gmsg";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/gmsg <group> <msg...>: Send message to a group";
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
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
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            throw new WrongUsageException(getCommandUsage(null));
        }

        arguments.tabComplete(APIRegistry.perms.getServerZone().getGroups());
        String group = arguments.remove().toLowerCase();

        if (arguments.isEmpty())
            throw new TranslatedCommandException("Missing chat message");

        IChatComponent msgComponent = func_147176_a(arguments.sender, arguments.toArray(), 0, !(arguments.sender instanceof EntityPlayer));
        ModuleChat.tellGroup(arguments.sender, msgComponent.getUnformattedText(), group);
    }

}
