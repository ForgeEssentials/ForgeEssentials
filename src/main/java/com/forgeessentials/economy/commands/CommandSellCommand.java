package com.forgeessentials.economy.commands;

import java.util.Arrays;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.misc.TranslatedCommandException.InvalidSyntaxException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandSellCommand extends ForgeEssentialsCommandBuilder
{

    public CommandSellCommand(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

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
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    /*
     * Expected structure: "/sellcommand <player> <item> <amount> <meta> <command...>"
     */

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        UserIdent ident = UserIdent.get(args[0], sender);
        ServerPlayerEntity player = ident.getPlayerMP();

        String itemName = args[1];
        int amount = parseInt(args[2]);
        int meta = parseInt(args[3]);

        Item item = CommandBase.getItemByText(ident.getPlayerMP(), itemName);
        ItemStack itemStack = new ItemStack(item, amount, meta);

        int foundStacks = 0;
        for (int slot = 0; slot < player.inventory.items.size(); slot++)
        {
            ItemStack stack = player.inventory.items.get(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemStack.getItem()
                    && (itemStack.getDamageValue() == -1 || stack.getDamageValue() == itemStack.getDamageValue()))
                foundStacks += stack.getCount();
        }

        if (foundStacks < amount)
        {
            ChatOutputHandler.chatError(player, Translator.format("You do not have enough %s to afford this", itemStack.getDisplayName()));
            return Command.SINGLE_SUCCESS;
        }

        ChatOutputHandler.chatConfirmation(player, Translator.format("You paid %d x %s", //
                amount, itemStack.getDisplayName(), APIRegistry.economy.getWallet(UserIdent.get(player)).toString()));

        args = Arrays.copyOfRange(args, 4, args.length);
        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(new DoAsCommandSender(ModuleEconomy.ECONOMY_IDENT, player), StringUtils.join(args, " "));

        for (int slot = 0; slot < player.inventory.items.size(); slot++)
        {
            ItemStack stack = player.inventory.items.get(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemStack.getItem()
                    && (itemStack.getDamageValue() == -1 || stack.getDamageValue() == itemStack.getDamageValue()))
            {
                int removeCount = Math.min(stack.getCount(), amount);
                player.inventory.removeItem(slot, removeCount);
                foundStacks -= removeCount;
                amount -= removeCount;
                if (amount <= 0)
                    break;
            }
        }
    }
}
