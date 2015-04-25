package com.forgeessentials.economy.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.economy.ModuleEconomy;

public class CommandSell extends ForgeEssentialsCommandBase
{

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".sell";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandName()
    {
        return "sell";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return null;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        throw new TranslatedCommandException("Not yet implemented!");
        // String itemName = args[0];
        // int meta = Integer.parseInt(args[1]);
        // int amount = Integer.parseInt(args[2]);
        //
        // int price = APIRegistry.economy.getItemTables().get(itemName);
        //
        // ItemStack stack = new ItemStack(CommandBase.getItemByText(player, itemName), amount, meta);
        // int transact = price * amount;
        //
        // String targetName = stack.getUnlocalizedName();
        //
        // // Loop though inventory and find a stack big enough to support the sell command
        // for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
        // {
        // ItemStack is = player.inventory.mainInventory[slot];
        // if (is != null)
        // {
        // if (is.getUnlocalizedName().equalsIgnoreCase(targetName))
        // {
        // if (meta == -1 || meta == is.getItemDamage())
        // {
        // if (is.stackSize >= amount)
        // {
        // player.inventory.decrStackSize(slot, amount);
        // APIRegistry.economy.addToWallet(transact, new UserIdent(player).getUuid());
        // OutputHandler.chatConfirmation(player, "You have sold " + amount + " of " + stack.getDisplayName() + ":" +
        // meta
        // + " to the server. " + ModuleEconomy.formatCurrency(transact) + " has been added to your account.");
        // break;
        // }
        // }
        // }
        // }
        // }
    }
}
