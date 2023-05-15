package com.forgeessentials.util.questioner;

public class QuestionerException extends Exception
{
    public static class QuestionerStillActiveException extends Exception
    {
        public QuestionerStillActiveException() {}
    }
}