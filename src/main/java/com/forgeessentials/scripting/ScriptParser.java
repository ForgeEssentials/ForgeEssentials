package com.forgeessentials.scripting;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;

public class ScriptParser
{

    public static interface ScriptMethod
    {

        public boolean process(CommandSourceStack sender, String[] args) throws CommandRuntimeException;

        public String getHelp();

    }

    public static interface ScriptArgument
    {

        public String process(CommandSourceStack sender) throws ScriptException;

        public String getHelp();

    }

    public static class ScriptException extends RuntimeException
    {

        public ScriptException()
        {
            super();
        }

        public ScriptException(String message)
        {
            super(message);
        }

        public ScriptException(String message, Object... args)
        {
            super(String.format(message, args));
        }

    }

    public static class SyntaxException extends ScriptException
    {

        public SyntaxException(String message, Object... args)
        {
            super(message, args);
        }

    }

    public static class ScriptErrorException extends ScriptException
    {

        public ScriptErrorException()
        {
            super();
        }

        public ScriptErrorException(String message)
        {
            super(message);
        }

        public ScriptErrorException(String message, Object... args)
        {
            super(message, args);
        }

    }

    public static class MissingPlayerException extends ScriptErrorException
    {

        public MissingPlayerException()
        {
            super("Missing player for @player argument");
        }

    }

    public static class MissingPermissionException extends ScriptErrorException
    {

        public final String permission;

        public MissingPermissionException(String permission)
        {
            super();
            this.permission = permission;
        }

        public MissingPermissionException(String permission, String message)
        {
            super(message);
            this.permission = permission;
        }

        public MissingPermissionException(String permission, String message, Object... args)
        {
            super(message, args);
            this.permission = permission;
        }

    }

}
