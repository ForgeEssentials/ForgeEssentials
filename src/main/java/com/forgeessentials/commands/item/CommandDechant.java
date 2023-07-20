package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandDechant extends ForgeEssentialsCommandBuilder
{
    public CommandDechant(boolean enabled)
    {
        super(enabled);
    }

    private static final String PERM = ModuleCommands.PERM + ".dechant";

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
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.then(Commands.argument("name", EnchantmentArgument.enchantment())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
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
        Enchantment enchantmentC = EnchantmentArgument.getEnchantment(ctx, "name");
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
