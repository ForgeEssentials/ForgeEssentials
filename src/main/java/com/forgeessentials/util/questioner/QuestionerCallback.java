package com.forgeessentials.util.questioner;

import net.minecraft.commands.CommandRuntimeException;

public interface QuestionerCallback
{

    public void respond(Boolean response) throws CommandRuntimeException;

}