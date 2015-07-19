package net.minecraftforge.fe.event.world;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Forge PR 1459
 **/

@Cancelable
public class SignEditEvent extends Event
{
    public final int x, y, z;
    public final String[] text;
    public final EntityPlayerMP editor;

    public SignEditEvent(int x, int y, int z, String[] text, EntityPlayerMP editor)
    {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
        this.text = text;
        this.editor = editor;
    }
}