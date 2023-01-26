package com.forgeessentials.core.commands.Arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.forgeessentials.commands.item.CommandKit;
import com.forgeessentials.commands.util.Kit;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

public class FeKitArgument implements ArgumentType<List<String>>
{
    public static final SimpleCommandExceptionType ERROR_INVALID_KIT = new SimpleCommandExceptionType(new TranslationTextComponent("argument.kit.invalid"));
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");

    public static String getKit(CommandContext<CommandSource> p_239195_0_, String p_239195_1_) {
       return p_239195_0_.getArgument(p_239195_1_, String.class);
    }

    public static FeKitArgument kit() {
       return new FeKitArgument();
    }

    public List<String> parse(StringReader p_parse_1_) throws CommandSyntaxException {
        String s = p_parse_1_.readUnquotedString();
        List<String> availableKits = new ArrayList<>();
        try {
            for (Kit kit : CommandKit.kits.values())
                if (kit.getName().contains(s))//com.forgeessentials.util.CommandUtils.hasPermission(null,CommandKit.PERM + "." + kit.getName()))
                    availableKits.add(kit.getName());
            return availableKits;
        } catch (IllegalArgumentException illegalargumentexception) {
        }
       throw ERROR_INVALID_KIT.create();
    }

    public Collection<String> getExamples() {
       return EXAMPLES;
    }
}