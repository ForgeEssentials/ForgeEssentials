package com.forgeessentials.util.questioner;

import net.minecraft.command.CommandException;

public interface QuestionerCallback {

	public void respond(Boolean response) throws CommandException;

}