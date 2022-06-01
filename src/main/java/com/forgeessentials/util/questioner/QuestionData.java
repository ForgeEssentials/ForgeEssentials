package com.forgeessentials.util.questioner;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

public class QuestionData
{

    private CommandSource target;

    private CommandSource source;

    private String question;

    private int timeout;

    private long startTime;

    private QuestionerCallback callback;

    public QuestionData(CommandSource target, String question, QuestionerCallback callback, int timeout, CommandSource source)
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
        TextComponent yesMessage = new StringTextComponent("/yes");
        yesMessage.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/yes"));
        yesMessage.getStyle().withColor(TextFormatting.RED);
        yesMessage.getStyle().setUnderlined(true);

        TextComponent noMessage = new StringTextComponent("/no");
        noMessage.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/no"));
        noMessage.getStyle().withColor(TextFormatting.RED);
        noMessage.getStyle().setUnderlined(true);

        TextComponent yesNoMessage = new StringTextComponent("Type ");
        yesNoMessage.append(yesMessage);
        yesNoMessage.append(new StringTextComponent(" or "));
        yesNoMessage.append(noMessage);
        yesNoMessage.append(new StringTextComponent(" " + Translator.format("(timeout: %d)", timeout)));

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

    public CommandSource getTarget()
    {
        return target;
    }

    public CommandSource getSource()
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
