package net.minecraftforge.fe.event.world;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
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
    public final ServerPlayerEntity editor;
    public ITextComponent[] formatted;

    public SignEditEvent(BlockPos pos, String[] text, ServerPlayerEntity editor)
    {
        super();
        this.pos = pos;
        this.text = text;
        this.editor = editor;
        this.formatted = new ITextComponent[]{null, null, null, null};
    }
}