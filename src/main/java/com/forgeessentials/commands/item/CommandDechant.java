package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandDechant extends BaseCommand
{
    public CommandDechant(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    private static final String PERM = ModuleCommands.PERM + ".dechant";

    @Override
    public String getPrimaryAlias()
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ItemStack stack = arguments.senderPlayer.getMainHandItem();
        if (stack == ItemStack.EMPTY)
            throw new TranslatedCommandException("You are not holding a valid item");
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        List<String> validEnchantmentNames = new ArrayList<>();
        Map<String, Enchantment> validEnchantments = new HashMap<>();
        Iterator<Enchantment> itor = ForgeRegistries.ENCHANTMENTS.iterator();
        while (itor.hasNext())
        {
            Enchantment enchantment = itor.next();
            if (enchantment != null && enchantments.containsKey(enchantment))
            {
                String name = I18n.translateToLocal(enchantment.getName()).replaceAll(" ", "");
                validEnchantmentNames.add(name);
                validEnchantments.put(name.toLowerCase(), enchantment);
            }
        }
        while (!arguments.isEmpty())
        {
            arguments.tabComplete(validEnchantmentNames);
            String name = arguments.remove();
            Enchantment enchantment = validEnchantments.get(name.toLowerCase());
            if (enchantment == null)
                throw new TranslatedCommandException("Invalid enchantment name %s!", name);
            enchantments.remove(enchantment);
        }
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }
}
