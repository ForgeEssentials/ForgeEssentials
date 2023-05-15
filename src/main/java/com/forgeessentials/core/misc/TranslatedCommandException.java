package com.forgeessentials.core.misc;

import net.minecraft.command.CommandException;

@Deprecated
public class TranslatedCommandException extends CommandException
{
	@Deprecated
    public TranslatedCommandException(String message)
    {
        super(Translator.translateITC(message));
    }
	@Deprecated
    public TranslatedCommandException(String message, Object... args)
    {
        super(Translator.translateITC(message, args));
    }

}