package net.minecraftforge.fe.event.world;



import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Forge PR 1459
 **/

@Cancelable
public class SignEditEvent extends Event
{
    public final BlockPos pos;
    public final IChatComponent[] text;
    public final EntityPlayerMP editor;

    public SignEditEvent(BlockPos pos, IChatComponent[] iChatComponents, EntityPlayerMP editor)
    {
        super();
        this.pos = pos;
        this.text = iChatComponents;
        this.editor = editor;
    }
}