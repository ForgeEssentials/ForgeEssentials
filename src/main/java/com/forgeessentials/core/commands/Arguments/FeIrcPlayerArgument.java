package com.forgeessentials.core.commands.Arguments;

import java.util.Arrays;
import java.util.Collection;

import com.forgeessentials.chat.irc.IrcHandler;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

public class FeIrcPlayerArgument implements ArgumentType<String>
{
    public static final SimpleCommandExceptionType ERROR_INVALID_PLAYER = new SimpleCommandExceptionType(new TranslationTextComponent("argument.player.invalid"));
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");

    public static String getPlayer(CommandContext<CommandSource> p_239195_0_, String p_239195_1_) {
       return p_239195_0_.getArgument(p_239195_1_, String.class);
    }

    public static FeIrcPlayerArgument player() {
       return new FeIrcPlayerArgument();
    }

    public String parse(StringReader p_parse_1_) throws CommandSyntaxException {
        String s = p_parse_1_.readUnquotedString();
        if (IrcHandler.getInstance().getIrcUserNames().contains(s)) {
            try {
                return s;
            } catch (IllegalArgumentException illegalargumentexception) {
            }
         }

       throw ERROR_INVALID_PLAYER.create();
    }

    public Collection<String> getExamples() {
       return EXAMPLES;
    }
}
