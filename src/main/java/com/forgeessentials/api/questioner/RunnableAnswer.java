package com.forgeessentials.api.questioner;

public class RunnableAnswer implements Runnable {
    private boolean affirmative;

    @Override
    public void run()
    {

    }

    public void setAnswer(boolean affirmative)
    {
        this.affirmative = affirmative;
    }
}
