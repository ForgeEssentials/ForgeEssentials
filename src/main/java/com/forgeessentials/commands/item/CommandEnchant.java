package com.forgeessentials.commands.item;

import java.util.Map;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandEnchant extends ForgeEssentialsCommandBuilder
{
    public CommandEnchant(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    private static final String PERM = ModuleCommands.PERM + ".enchant";

    @Override
    public String getPrimaryAlias()
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
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("name", EnchantmentArgument.enchantment())
                        .then(Commands.literal("maxlevel")
                                .executes(CommandContext -> execute(CommandContext, "maxlevel")
                                        )
                                )
                        )
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                .then(Commands.literal("custom")
                                        .executes(CommandContext -> execute(CommandContext, "level")
                                                )
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ItemStack stack = getServerPlayer(ctx.getSource()).getMainHandItem();
        if (stack == ItemStack.EMPTY)
            throw new TranslatedCommandException("You are not holding a valid item");

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        Enchantment enchantment = EnchantmentArgument.getEnchantment(ctx, "name");
        if (enchantment == null | !enchantment.canApplyAtEnchantingTable(stack))
            throw new TranslatedCommandException("Invalid enchantment %s!", enchantment);

        if (params.toString() == "maxlevel")
        {
            enchantments.put(enchantment, enchantment.getMaxLevel());
        }else 
        {
        enchantments.put(enchantment, Math.min(enchantment.getMaxLevel(), IntegerArgumentType.getInteger(ctx, "level")));
        }
        EnchantmentHelper.setEnchantments(enchantments, stack);
        return Command.SINGLE_SUCCESS;
    }
}
