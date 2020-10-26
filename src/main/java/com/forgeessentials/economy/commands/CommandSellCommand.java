package com.forgeessentials.economy.commands;

import java.util.Arrays;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException.InvalidSyntaxException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandSellCommand extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "sellcommand";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "sc", "scmd" };
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".sellcommand";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/sellcommand <player> <item> <amount> <meta> <command...>";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    /*
     * Expected structure: "/sellcommand <player> <item> <amount> <meta> <command...>"
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 5)
            throw new InvalidSyntaxException(getUsage(sender));

        UserIdent ident = UserIdent.get(args[0], sender);
        EntityPlayerMP player = ident.getPlayerMP();
        if (player == null)
            throw new PlayerNotFoundException("commands.generic.player.notFound");

        String itemName = args[1];
        int amount = parseInt(args[2]);
        int meta = parseInt(args[3]);

        Item item = CommandBase.getItemByText(ident.getPlayerMP(), itemName);
        ItemStack itemStack = new ItemStack(item, amount, meta);

        int foundStacks = 0;
        for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++)
        {
            ItemStack stack = player.inventory.mainInventory.get(slot);
            if (stack != null && stack.getItem() == itemStack.getItem()
                    && (itemStack.getItemDamage() == -1 || stack.getItemDamage() == itemStack.getItemDamage()))
                foundStacks += stack.getCount();
        }

        if (foundStacks < amount)
        {
            ChatOutputHandler.chatError(player, Translator.format("You do not have enough %s to afford this", itemStack.getDisplayName()));
            return;
        }

        ChatOutputHandler.chatConfirmation(player, Translator.format("You paid %d x %s", //
                amount, itemStack.getDisplayName(), APIRegistry.economy.getWallet(UserIdent.get(player)).toString()));

        args = Arrays.copyOfRange(args, 4, args.length);
        server.getCommandManager().executeCommand(new DoAsCommandSender(ModuleEconomy.ECONOMY_IDENT, player), StringUtils.join(args, " "));

        for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++)
        {
            ItemStack stack = player.inventory.mainInventory.get(slot);
            if (stack != null && stack.getItem() == itemStack.getItem()
                    && (itemStack.getItemDamage() == -1 || stack.getItemDamage() == itemStack.getItemDamage()))
            {
                int removeCount = Math.min(stack.getCount(), amount);
                player.inventory.decrStackSize(slot, removeCount);
                foundStacks -= removeCount;
                amount -= removeCount;
                if (amount <= 0)
                    break;
            }
        }
    }
    
}
