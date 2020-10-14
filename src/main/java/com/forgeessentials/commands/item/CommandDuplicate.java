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
import com.forgeessentials.util.PlayerUtil;

public class CommandDuplicate extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "duplicate";
    }

    @Override
    public String getUsage(ICommandSender par1ICommandSender)
    {
        return "/duplicate [amount]: Duplicates your current item";
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
        return ModuleCommands.PERM + "." + getName();
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP player, String[] args) throws CommandException
    {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack == null)
            throw new TranslatedCommandException("No item equipped");

        int stackSize = 0;
        if (args.length > 0)
            stackSize = parseInt(args[0]);

        ItemStack newStack = stack.copy();
        if (stackSize > 0)
            newStack.setCount(stackSize);

        PlayerUtil.give(player, newStack);
    }

}
