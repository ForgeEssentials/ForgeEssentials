package com.forgeessentials.util.questioner;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.questioner.QuestionCenter.IReplyHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class QuestionData {
    private ICommandSender target;
    private int waitTime;
    private long startTime;

    private IReplyHandler processAnswer;

    public QuestionData(ICommandSender target, String question, IReplyHandler runnable, int timeout)
    {
        this.target = target;
        startTime = System.currentTimeMillis();
        processAnswer = runnable;
        waitTime = timeout;

        OutputHandler.sendMessage(target, question);
    }

    public int getTimeout()
    {
        return waitTime;
    }

    public void count()
    {
        if ((System.currentTimeMillis() - startTime) / 1000L > waitTime)
        {
            QuestionCenter.abort(this);
        }
    }

    public void doAnswer(boolean affirmative)
    {
        processAnswer.replyReceived(affirmative);
        QuestionCenter.questionDone(this);
    }

    public ICommandSender getTarget()
    {
        return target;
    }
}
