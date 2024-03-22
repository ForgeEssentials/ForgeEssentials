package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandSellCommand extends ForgeEssentialsCommandBuilder
{

    public CommandSellCommand(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "sellcommand";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "sc", "scmd" };
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
     * Expected structure: "/sellcommand <player> <item> <amount> <command...>"
     */

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("item", ItemArgument.item())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .then(Commands.argument("command", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "blank"))))));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        UserIdent ident = UserIdent.get(EntityArgument.getPlayer(ctx, "player"));
        ServerPlayer player = ident.getPlayerMP();

        int amount = IntegerArgumentType.getInteger(ctx, "amount");

        Item item = ItemArgument.getItem(ctx, "item").getItem();
        ItemStack itemStack = new ItemStack(item, amount);

        int foundStacks = 0;
        for (int slot = 0; slot < player.getInventory().items.size(); slot++)
        {
            ItemStack stack = player.getInventory().items.get(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemStack.getItem()
                    && (itemStack.getDamageValue() == -1 || stack.getDamageValue() == itemStack.getDamageValue()))
                foundStacks += stack.getCount();
        }

        if (foundStacks < amount)
        {
            ChatOutputHandler.chatError(player, Translator.format("You do not have enough %s to afford this",
                    itemStack.getDisplayName().getString()));
            return Command.SINGLE_SUCCESS;
        }

        ChatOutputHandler.chatConfirmation(player, Translator.format("You paid %d x %s", //
                amount, itemStack.getDisplayName().getString(),
                APIRegistry.economy.getWallet(UserIdent.get(player)).toString()));

        ServerLifecycleHooks.getCurrentServer().getCommands()
                .performCommand(new DoAsCommandSender(ModuleEconomy.ECONOMY_IDENT, player.createCommandSourceStack())
                        .createCommandSourceStack(), StringArgumentType.getString(ctx, "command"));

        for (int slot = 0; slot < player.getInventory().items.size(); slot++)
        {
            ItemStack stack = player.getInventory().items.get(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemStack.getItem()
                    && (itemStack.getDamageValue() == -1 || stack.getDamageValue() == itemStack.getDamageValue()))
            {
                int removeCount = Math.min(stack.getCount(), amount);
                player.getInventory().removeItem(slot, removeCount);
                foundStacks -= removeCount;
                amount -= removeCount;
                if (amount <= 0)
                    break;
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
