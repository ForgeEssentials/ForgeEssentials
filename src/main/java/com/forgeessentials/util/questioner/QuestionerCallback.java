package com.forgeessentials.util.questioner;

import net.minecraft.commands.CommandRuntimeException;

import CommandRuntimeException;

public interface QuestionerCallback
{

    public void respond(Boolean response) throws CommandRuntimeException;

}