package com.forgeessentials.scripting.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.scripting.pattern.Pattern.PatternMatchException;

public class PatternParser<T extends Pattern>
{

    public static class ParseResult<R>
    {

        public final R pattern;

        public final List<String> arguments;

        public ParseResult(R pattern, List<String> arguments)
        {
            this.pattern = pattern;
            this.arguments = arguments;
        }

    }

    protected List<T> patterns;

    public void add(T pattern)
    {
        patterns.add(pattern);
    }

    public ParseResult<T> parse(String input, ICommandSender sender) throws PatternMatchException
    {
        String lastError = Translator.translate("Invalid syntax");
        patternLoop: for (T pattern : patterns)
        {
            Matcher matcher = pattern.patternRegex.matcher(input);
            if (!matcher.matches())
                continue;

            List<String> arguments = new ArrayList<>();
            for (int i = 0; i < matcher.groupCount(); i++)
            {
                String arg = matcher.group(i + 1);
                arguments.add(arg);
                switch (pattern.argumentTypes.get(i))
                {
                case PLAYER:
                    if (UserIdent.getPlayerByMatchOrUsername(sender, arg) == null)
                    {
                        lastError = Translator.format("Could not find player %s", arg);
                        continue patternLoop;
                    }
                    break;
                case GROUP:
                    if (!APIRegistry.perms.groupExists(arg))
                    {
                        lastError = Translator.format("Could not find player %s", arg);
                        continue patternLoop;
                    }
                    break;
                case ZONE:
                    if (APIRegistry.perms.getZoneById(arg) == null)
                    {
                        lastError = Translator.format("Could not find zone %s", arg);
                        continue patternLoop;
                    }
                    break;
                case DECIMAL:
                case FLOAT:
                case NONE:
                    break;
                }
            }
            pattern.onMatch(arguments);
            return new ParseResult<>(pattern, arguments);
        }
        throw new PatternMatchException(lastError);
    }

}
