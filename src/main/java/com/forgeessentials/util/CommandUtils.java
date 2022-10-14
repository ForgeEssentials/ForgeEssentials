package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CommandUtils {
	public static ICommandSource GetSource(CommandSource source) {
		ICommandSource isource = ObfuscationReflectionHelper.getPrivateValue(CommandSource.class, source, "source");
		return isource;
	}
	
	public static double parseDouble(String input) throws NumberFormatException
    {
        try
        {
            double d0 = Double.parseDouble(input);

            if (!Doubles.isFinite(d0))
            {
                throw new NumberFormatException();
            }
            else
            {
                return d0;
            }
        }
        catch (NumberFormatException var3)
        {
            throw new NumberFormatException();
        }
    }

    public static double parseDouble(String input, double min) throws NumberFormatException
    {
        return parseDouble(input, min, Double.MAX_VALUE);
    }

    public static double parseDouble(String input, double min, double max) throws NumberFormatException
    {
        double d0 = parseDouble(input);

        if (d0 < min)
        {
            throw new NumberFormatException();
        }
        else if (d0 > max)
        {
            throw new NumberFormatException();
        }
        else
        {
            return d0;
        }
    }

    public static int parseInt(String input) throws NumberFormatException
    {
        try
        {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException var2)
        {
            throw new NumberFormatException();
        }
    }

    public static int parseInt(String input, int min) throws NumberFormatException
    {
        return parseInt(input, min, Integer.MAX_VALUE);
    }

    public static int parseInt(String input, int min, int max) throws NumberFormatException
    {
        int i = parseInt(input);

        if (i < min)
        {
            throw new NumberFormatException();
        }
        else if (i > max)
        {
            throw new NumberFormatException();
        }
        else
        {
            return i;
        }
    }

    /**
     * Parse int with support for relative int.
     *
     * @param string
     * @param relativeStart
     * @return
     * @throws NumberInvalidException
     */
    public static int parseRelativeInt(String string, int relativeStart) throws NumberFormatException
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(string);
        }
        else
        {
            return parseInt(string);
        }
    }

    /**
     * Parse double with support for relative values.
     *
     * @param string
     * @param relativeStart
     * @return
     */
    public static double parseRelativeDouble(String string, double relativeStart) throws NumberFormatException
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(string);
        }
        else
        {
            return parseInt(string);
        }
    }

    // ------------------------------------------------------------
    // Utilities

    public static List<String> getListOfStringsMatchingLastWord(String arg, Collection<String> possibleMatches)
    {
        List<String> arraylist = new ArrayList<>();
        for (String s2 : possibleMatches)
        {
            if (doesStringStartWith(arg, s2))
            {
                arraylist.add(s2);
            }
        }
        return arraylist;
    }

    public static List<String> getListOfStringsMatchingLastWord(String arg, String... possibleMatches)
    {
        List<String> arraylist = new ArrayList<>();
        int i = possibleMatches.length;
        for (int j = 0; j < i; ++j)
        {
            String s2 = possibleMatches[j];
            if (doesStringStartWith(arg, s2))
            {
                arraylist.add(s2);
            }
        }
        return arraylist;
    }

    public static List<String> completePlayername(String arg)
    {
        List<String> arraylist = new ArrayList<>();
        for (UserIdent s2 : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            if (doesStringStartWith(arg, s2.getUsernameOrUuid()))
            {
                arraylist.add(s2.getUsernameOrUuid());
            }
        }
        return arraylist;
    }

    public static List<String> getListOfStringsMatchingLastWord(String[] inputArgs, Collection<?> possibleCompletions)
    {
        String s = inputArgs[inputArgs.length - 1];
        List<String> list = Lists.<String>newArrayList();

        if (!possibleCompletions.isEmpty())
        {
            for (String s1 : Iterables.transform(possibleCompletions, Functions.toStringFunction()))
            {
                if (doesStringStartWith(s, s1))
                {
                    list.add(s1);
                }
            }

            if (list.isEmpty())
            {
                for (Object object : possibleCompletions)
                {
                    if (object instanceof ResourceLocation && doesStringStartWith(s, ((ResourceLocation)object).getNamespace()))
                    {
                        list.add(String.valueOf(object));
                    }
                }
            }
        }

        return list;
    }

    public List<String> matchToPlayers(String[] args)
    {
        return getListOfStringsMatchingLastWord(args, ServerLifecycleHooks.getCurrentServer().getPlayerNames());
    }

    public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibilities)
    {
        return getListOfStringsMatchingLastWord(args, Arrays.asList(possibilities));
    }

    public static boolean doesStringStartWith(String original, String region)
    {
        return region.regionMatches(true, 0, original, 0, original.length());
    }
    public static ServerPlayerEntity getServerPlayer(CommandSource source) {
        return (source.getEntity() instanceof ServerPlayerEntity) ? (ServerPlayerEntity) source.getEntity() : null;
    }
    public static UserIdent getUserIdent(CommandSource source, ServerPlayerEntity player) {
        return (player == null) ? (CommandUtils.GetSource(source) instanceof DoAsCommandSender ? ((DoAsCommandSender) CommandUtils.GetSource(source)).getUserIdent() : null) : UserIdent.get(player);
    }

    public static ITextComponent getChatComponentFromNthArg(CommandSource sender, String[] args, int index) throws CommandException, 
    {
        return getChatComponentFromNthArg(sender, args, index, false);
    }

    public static ITextComponent getChatComponentFromNthArg(CommandSource sender, String[] args, int index, boolean p_147176_3_) throws CommandException
    {
        ITextComponent itextcomponent = new StringTextComponent("");

        for (int i = index; i < args.length; ++i)
        {
            if (i > index)
            {
                itextcomponent.appendText(" ");
            }

            ITextComponent itextcomponent1 = net.minecraftforge.common.ForgeHooks.newChatWithLinks(args[i]); // Forge: links for messages

            if (p_147176_3_)
            {
                ITextComponent itextcomponent2 = EntitySelector.matchEntitiesToTextComponent(sender, args[i]);

                if (itextcomponent2 == null)
                {
                    if (EntitySelector.isSelector(args[i]))
                    {
                        throw new PlayerNotFoundException("commands.generic.selector.notFound", new Object[] {args[i]});
                    }
                }
                else
                {
                    itextcomponent1 = itextcomponent2;
                }
            }

            itextcomponent.appendSibling(itextcomponent1);
        }

        return itextcomponent;
    }
}
