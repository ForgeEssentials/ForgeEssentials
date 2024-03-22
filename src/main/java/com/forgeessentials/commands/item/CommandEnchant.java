package com.forgeessentials.commands.item;

import java.util.Map;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandEnchant extends ForgeEssentialsCommandBuilder
{
    public CommandEnchant(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "enchant";
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("name", ItemEnchantmentArgument.enchantment()).then(
                        Commands.literal("maxlevel").executes(CommandContext -> execute(CommandContext, "maxlevel"))))
                .then(Commands.argument("level", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                        .then(Commands.literal("custom").executes(CommandContext -> execute(CommandContext, "level"))));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ItemStack stack = getServerPlayer(ctx.getSource()).getMainHandItem();
        if (stack == ItemStack.EMPTY)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You are not holding a valid item");
            return Command.SINGLE_SUCCESS;
        }

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        Enchantment enchantment = ItemEnchantmentArgument.getEnchantment(ctx, "name");
        if (enchantment == null | !enchantment.canApplyAtEnchantingTable(stack))
        {
            ChatOutputHandler.chatError(ctx.getSource(), Translator.format("Invalid enchantment %s!", enchantment));
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("maxlevel"))
        {
            enchantments.put(enchantment, enchantment.getMaxLevel());
        }
        else
        {
            enchantments.put(enchantment,
                    Math.min(enchantment.getMaxLevel(), IntegerArgumentType.getInteger(ctx, "level")));
        }
        EnchantmentHelper.setEnchantments(enchantments, stack);
        return Command.SINGLE_SUCCESS;
    }
}
