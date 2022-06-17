package com.forgeessentials.core.misc;

import net.minecraft.command.CommandException;

import com.forgeessentials.api.UserIdent;

public class TranslatedCommandException extends CommandException
{

    public TranslatedCommandException(String message)
    {
        super(Translator.translateITC(message));
    }

    public TranslatedCommandException(String message, Object... args)
    {
        super(Translator.translateITC(message));//, args);
    }

    public static class PlayerNotFoundException extends TranslatedCommandException
    {

        public PlayerNotFoundException(String playerName)
        {
            super("Player %s not found", playerName);
        }

        public PlayerNotFoundException(UserIdent ident)
        {
            super("Player %s not found", ident.getUsernameOrUuid());
        }

    }

    public static class InvalidSyntaxException extends TranslatedCommandException
    {

        public InvalidSyntaxException()
        {
            super("Invalid Syntax");
        }

        public InvalidSyntaxException(String correctSyntax)
        {
            super("Invalid Syntax. Instead use %s", correctSyntax);
        }

    }
    public static class WrongUsageException extends TranslatedCommandException
    {

        public WrongUsageException()
        {
            super("Invalid Syntax");
        }

        public WrongUsageException(String correctSyntax)
        {
            super("Invalid Syntax. Instead use %s", correctSyntax);
        }

    }

}