package com.forgeessentials.core.misc;

import net.minecraft.command.CommandException;

public class TranslatedCommandException extends CommandException {
    private static final long serialVersionUID = 488225657837546510L;

    public TranslatedCommandException(String message, Object... args)
    {
        super(Translator.translate(message), args);
    }

}