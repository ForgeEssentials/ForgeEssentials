package com.forgeessentials.core.misc;

import net.minecraft.command.CommandException;

public class TranslatedCommandException extends CommandException
{

    public TranslatedCommandException(String message)
    {
        super(Translator.translateITC(message));
    }

    public TranslatedCommandException(String message, Object... args)
    {
        super(Translator.translateITC(message, args));
    }

}