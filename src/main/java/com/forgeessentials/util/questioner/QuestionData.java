package com.forgeessentials.util.questioner;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;

public class QuestionData
{

    private Player target;

    private Player source;

    private String question;

    private int timeout;

    private long startTime;

    private QuestionerCallback callback;

    public QuestionData(Player target, String question, QuestionerCallback callback, int timeout,
            Player source)
    {
        this.target = target;
        this.timeout = timeout;
        this.callback = callback;
        this.source = source;
        this.question = question;
        this.startTime = System.currentTimeMillis();
    }

    public void sendQuestion()
    {
        ChatOutputHandler.sendMessage(target.createCommandSourceStack(), question);
        sendYesNoMessage();
    }

    public void sendYesNoMessage()
    {
        BaseComponent yesMessage = new TextComponent("/feyes");
        ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/feyes");
        yesMessage.withStyle((style) -> style.withClickEvent(click));
        yesMessage.withStyle(ChatFormatting.RED);
        yesMessage.withStyle(ChatFormatting.UNDERLINE);

        BaseComponent noMessage = new TextComponent("/feno");
        ClickEvent click1 = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/feno");
        noMessage.withStyle((style) -> style.withClickEvent(click1));
        noMessage.withStyle(ChatFormatting.RED);
        noMessage.withStyle(ChatFormatting.UNDERLINE);

        BaseComponent yesNoMessage = new TextComponent("Type ");
        yesNoMessage.append(yesMessage);
        yesNoMessage.append(new TextComponent(" or "));
        yesNoMessage.append(noMessage);
        yesNoMessage.append(new TextComponent(" " + Translator.format("(timeout: %d)", timeout)));

        ChatOutputHandler.sendMessage(target.createCommandSourceStack(), yesNoMessage);
    }

    protected void doAnswer(Boolean answer) throws CommandRuntimeException
    {
        callback.respond(answer);
    }

    public void confirm() throws CommandRuntimeException
    {
        Questioner.confirm(target);
        // TODO: Maybe send a message, because it was not confirmed through user
        // interaction?
    }

    public void deny() throws CommandRuntimeException
    {
        Questioner.deny(target);
        // TODO: Maybe send a message, because it was not denied through user
        // interaction?
    }

    public void cancel() throws CommandRuntimeException
    {
        Questioner.cancel(target);
        // TODO: Maybe send a message, because it was not canceled through user
        // interaction?
    }

    /* ------------------------------------------------------------ */

    public Player getTarget()
    {
        return target;
    }

    public Player getSource()
    {
        return source;
    }

    public String getQuestion()
    {
        return question;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public QuestionerCallback getCallback()
    {
        return callback;
    }

    public boolean isTimeout()
    {
        return (System.currentTimeMillis() - startTime) / 1000L > timeout;
    }

}
