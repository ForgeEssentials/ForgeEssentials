package com.forgeessentials.util.questioner;

import com.forgeessentials.util.TranslatedCommandException;

public class QuestionerStillActiveException extends Exception
{

    public static class CommandException extends TranslatedCommandException
    {
        public CommandException()
        {
            super("Cannot run command because player is still answering a question. Please wait a moment");
        }
    }

}