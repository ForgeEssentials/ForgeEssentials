package com.forgeessentials.util.questioner;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class QuestionData {

	private PlayerEntity target;

	private PlayerEntity source;

	private String question;

	private int timeout;

	private long startTime;

	private QuestionerCallback callback;

	public QuestionData(PlayerEntity target, String question, QuestionerCallback callback, int timeout,
			PlayerEntity source) {
		this.target = target;
		this.timeout = timeout;
		this.callback = callback;
		this.source = source;
		this.question = question;
		this.startTime = System.currentTimeMillis();
	}

	public void sendQuestion() {
		ChatOutputHandler.sendMessage(target.createCommandSourceStack(), question);
		sendYesNoMessage();
	}

	public void sendYesNoMessage() {
		TextComponent yesMessage = new StringTextComponent("/feyes");
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/feyes");
		yesMessage.withStyle((style) -> {
			return style.withClickEvent(click);
		});
		yesMessage.withStyle(TextFormatting.RED);
		yesMessage.withStyle(TextFormatting.UNDERLINE);

		TextComponent noMessage = new StringTextComponent("/feno");
		ClickEvent click1 = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/feno");
		noMessage.withStyle((style) -> {
			return style.withClickEvent(click1);
		});
		noMessage.withStyle(TextFormatting.RED);
		noMessage.withStyle(TextFormatting.UNDERLINE);

		TextComponent yesNoMessage = new StringTextComponent("Type ");
		yesNoMessage.append(yesMessage);
		yesNoMessage.append(new StringTextComponent(" or "));
		yesNoMessage.append(noMessage);
		yesNoMessage.append(new StringTextComponent(" " + Translator.format("(timeout: %d)", timeout)));

		ChatOutputHandler.sendMessage(target.createCommandSourceStack(), yesNoMessage);
	}

	protected void doAnswer(Boolean answer) throws CommandException {
		callback.respond(answer);
	}

	public void confirm() throws CommandException {
		Questioner.confirm(target);
		// TODO: Maybe send a message, because it was not confirmed through user
		// interaction?
	}

	public void deny() throws CommandException {
		Questioner.deny(target);
		// TODO: Maybe send a message, because it was not denied through user
		// interaction?
	}

	public void cancel() throws CommandException {
		Questioner.cancel(target);
		// TODO: Maybe send a message, because it was not canceled through user
		// interaction?
	}

	/* ------------------------------------------------------------ */

	public PlayerEntity getTarget() {
		return target;
	}

	public PlayerEntity getSource() {
		return source;
	}

	public String getQuestion() {
		return question;
	}

	public int getTimeout() {
		return timeout;
	}

	public long getStartTime() {
		return startTime;
	}

	public QuestionerCallback getCallback() {
		return callback;
	}

	public boolean isTimeout() {
		return (System.currentTimeMillis() - startTime) / 1000L > timeout;
	}

}
