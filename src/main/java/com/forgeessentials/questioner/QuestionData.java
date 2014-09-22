package com.forgeessentials.questioner;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.questioner.AnswerEnum;
import com.forgeessentials.api.questioner.RunnableAnswer;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.selections.WarpPoint;

public class QuestionData {
    private WarpPoint point;
    private EntityPlayer asker;
    private EntityPlayer target;
    private int waitTime;
    private int interval;
    private int intervalCounter;
    private AnswerEnum affirmative;
    private AnswerEnum negative;

    private RunnableAnswer processAnswer;

    private String question;

    public QuestionData(WarpPoint point, EntityPlayer asker, EntityPlayer target, String question, RunnableAnswer runnable, AnswerEnum affirmative,
            AnswerEnum negative)
    {
        this.point = point;
        this.asker = asker;
        this.target = target;
        this.question = question;
        this.affirmative = affirmative;
        this.negative = negative;
        processAnswer = runnable;
        waitTime = QuestionCenter.defaultTime;
        interval = intervalCounter = QuestionCenter.defaultInterval;
    }

    public void setWaitTime(int seconds)
    {
        waitTime = seconds;
    }

    public void setInterval(int seconds)
    {
        interval = intervalCounter = seconds;
    }

    public void count()
    {
        intervalCounter--;
        if (intervalCounter == 0)
        {
            doQuestion();
            intervalCounter = interval;
        }

        waitTime--;
        if (waitTime == 0)
        {
            QuestionCenter.abort(this);
        }
    }

    public void doAnswer(boolean affirmative)
    {
        processAnswer.setAnswer(affirmative);
        processAnswer.run();
        QuestionCenter.questionDone(this);
    }

    public void doQuestion()
    {
        ChatUtils.sendMessage(target, question);
    }

    public EntityPlayer getAsker()
    {
        return asker;
    }

    public EntityPlayer getTarget()
    {
        return target;
    }

    public AnswerEnum getAffirmative()
    {
        return affirmative;
    }

    public AnswerEnum getNegative()
    {
        return negative;
    }
}
