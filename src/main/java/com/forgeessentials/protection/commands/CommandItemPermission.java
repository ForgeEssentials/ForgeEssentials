package com.forgeessentials.protection.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.OutputHandler;

public class CommandItemPermission extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "itemperm";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        ItemStack stack = sender.getCurrentEquippedItem();
        if (stack == null)
        {
            OutputHandler.chatError(sender, "No item equipped!");
            return;
        }
        String permission = ModuleProtection.PERM_ITEM_USE + "." + stack.getUnlocalizedName() + "." + stack.getItemDamage();
        OutputHandler.chatNotification(sender, permission);
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.protection.itemperm";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/itemperm: Shows full item permission node";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

}
