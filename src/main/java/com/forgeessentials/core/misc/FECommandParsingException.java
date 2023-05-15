package com.forgeessentials.core.misc;

public class FECommandParsingException extends Exception
{
	public String error;
    public FECommandParsingException(String message)
    {
        error=Translator.translate(message);
    }
    public FECommandParsingException(String message, Object... args)
    {
    	error=Translator.format(message, args);
    }
}