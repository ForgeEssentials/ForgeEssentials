package com.forgeessentials.protection.effect;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.scripting.ScriptParser;

public class CommandEffect extends ZoneEffect
{

    protected String command;

    public CommandEffect(EntityPlayerMP player, int interval, String command)
    {
        super(player, interval, false);
        this.command = command;
    }

    @Override
    public void execute()
    {
        ScriptParser.run(command, player, null);
    }

}
