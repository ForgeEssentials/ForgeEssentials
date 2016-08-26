package com.forgeessentials.util.questioner;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

public class QuestionData
{

    private ICommandSender target;

    private ICommandSender source;

    private String question;

    private int timeout;

    private long startTime;

    private QuestionerCallback callback;

    public QuestionData(ICommandSender target, String question, QuestionerCallback callback, int timeout, ICommandSender source)
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
        ChatOutputHandler.sendMessage(target, question);
        sendYesNoMessage();
    }

    public void sendYesNoMessage()
    {
        ITextComponent yesMessage = new TextComponentString("/yes");
        yesMessage.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/yes"));
        yesMessage.getStyle().setColor(TextFormatting.RED);
        yesMessage.getStyle().setUnderlined(true);

        ITextComponent noMessage = new TextComponentString("/no");
        noMessage.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/no"));
        noMessage.getStyle().setColor(TextFormatting.RED);
        noMessage.getStyle().setUnderlined(true);

        ITextComponent yesNoMessage = new TextComponentString("Type ");
        yesNoMessage.appendSibling(yesMessage);
        yesNoMessage.appendSibling(new TextComponentString(" or "));
        yesNoMessage.appendSibling(noMessage);
        yesNoMessage.appendSibling(new TextComponentString(" " + Translator.format("(timeout: %d)", timeout)));

        ChatOutputHandler.sendMessage(target, yesNoMessage);
    }

    protected void doAnswer(Boolean answer) throws CommandException
    {
        callback.respond(answer);
    }

    public void confirm() throws CommandException
    {
        Questioner.confirm(target);
        // TODO: Maybe send a message, because it was not confirmed through user interaction?
    }

    public void deny() throws CommandException
    {
        Questioner.deny(target);
        // TODO: Maybe send a message, because it was not denied through user interaction?
    }

    public void cancel() throws CommandException
    {
        Questioner.cancel(target);
        // TODO: Maybe send a message, because it was not canceled through user interaction?
    }

    /* ------------------------------------------------------------ */

    public ICommandSender getTarget()
    {
        return target;
    }

    public ICommandSender getSource()
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
