package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandDechant extends ForgeEssentialsCommandBuilder
{
    public CommandDechant(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "dechant";
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
        return baseBuilder.then(Commands.argument("name", ItemEnchantmentArgument.enchantment())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ItemStack stack = getServerPlayer(ctx.getSource()).getMainHandItem();
        if (stack == ItemStack.EMPTY)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You are not holding a valid item.");
            return Command.SINGLE_SUCCESS;
        }
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        List<Enchantment> validEnchantments = new ArrayList<>();
        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
            if (enchantment != null && enchantments.containsKey(enchantment)) {
                validEnchantments.add(enchantment);
            }
        }
        Enchantment enchantmentC = ItemEnchantmentArgument.getEnchantment(ctx, "name");
        if (enchantmentC == null | !validEnchantments.contains(enchantmentC))
        {
            ChatOutputHandler.chatError(ctx.getSource(), Translator.format("Invalid enchantment %s!", enchantmentC));
            return Command.SINGLE_SUCCESS;
        }
        enchantments.remove(enchantmentC);
        EnchantmentHelper.setEnchantments(enchantments, stack);
        return Command.SINGLE_SUCCESS;
    }
}
