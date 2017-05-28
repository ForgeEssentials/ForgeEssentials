package net.minecraftforge.fe.event.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Forge PR 1459
 **/

@Cancelable
public class SignEditEvent extends Event
{
    public final BlockPos pos;
    public String[] text;
    public final EntityPlayerMP editor;
    public ITextComponent[] formatted;

    public SignEditEvent(BlockPos pos, String[] text, EntityPlayerMP editor)
    {
        super();
        this.pos = pos;
        this.text = text;
        this.editor = editor;
        this.formatted = new ITextComponent[]{null, null, null, null};
    }
}