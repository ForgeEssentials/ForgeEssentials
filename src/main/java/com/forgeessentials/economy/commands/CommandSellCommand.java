package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.TranslatedCommandException.InvalidSyntaxException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;

public class CommandSellCommand extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "sellcommand";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("sc", "scmd");
    }

    @Override
    public String getPermissionNode()
    {
        return null;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/sellcommand <player> <['amount'x]item[:'meta']> <command [args]>";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        return false;
    }

    /*
     * Expected structure: "/sellcommand <player> <item> <amount> <meta> <command [args]>"
     */
    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length < 5)
            throw new InvalidSyntaxException(getCommandUsage(sender));

        UserIdent ident = new UserIdent(args[0]);
        EntityPlayerMP player = ident.getPlayer();
        if (player == null)
            throw new PlayerNotFoundException();

        String itemName = args[1];
        int amount = parseInt(sender, args[2]);
        int meta = parseInt(sender, args[3]);

        Item item = CommandBase.getItemByText(ident.getPlayer(), itemName);
        ItemStack itemStack = new ItemStack(item, amount, meta);

        int foundStacks = 0;
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
        {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && stack.getItem() == itemStack.getItem()
                    && (itemStack.getItemDamage() == -1 || stack.getItemDamage() == itemStack.getItemDamage()))
                foundStacks += stack.stackSize;
        }

        if (foundStacks < amount)
            throw new TranslatedCommandException("You do not have enough %s to afford this", itemStack.getDisplayName());

        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
        {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && stack.getItem() == itemStack.getItem()
                    && (itemStack.getItemDamage() == -1 || stack.getItemDamage() == itemStack.getItemDamage()))
            {
                int removeCount = Math.min(stack.stackSize, foundStacks);
                player.inventory.decrStackSize(slot, removeCount);
                foundStacks -= removeCount;
            }
        }

        args = Arrays.copyOfRange(args, 4, args.length);
        MinecraftServer.getServer().getCommandManager().executeCommand(sender, StringUtils.join(args, " "));

        OutputHandler.chatConfirmation(player, Translator.format("You paid %d x %s", //
                amount, itemStack.getDisplayName(), APIRegistry.economy.getWallet(player).toString()));
    }
}
