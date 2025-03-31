package com.forgeessentials.commands.item;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandRename extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "rename";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/rename <new name> Renames the item you are currently holding.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".rename";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 0)
            throw new TranslatedCommandException(getUsage(sender));

        ItemStack is = sender.inventory.getCurrentItem();
        if (is == ItemStack.EMPTY)
            throw new TranslatedCommandException("You are not holding a valid item.");

        StringBuilder sb = new StringBuilder();
        for (String arg : args)
        {
            sb.append(arg + " ");
        }
        is.setStackDisplayName(ChatOutputHandler.formatColors(sb.toString().trim()));
    }

}