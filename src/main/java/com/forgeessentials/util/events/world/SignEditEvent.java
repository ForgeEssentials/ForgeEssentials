package com.forgeessentials.util.events.world;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Forge PR 1459
 **/

@Cancelable
public class SignEditEvent extends Event
{
    public final BlockPos pos;
    public String[] text;
    public final ServerPlayer editor;
    public Component[] formatted;

    public SignEditEvent(BlockPos pos, String[] text, ServerPlayer editor)
    {
        super();
        this.pos = pos;
        this.text = text;
        this.editor = editor;
        this.formatted = new Component[] { null, null, null, null };
    }
}