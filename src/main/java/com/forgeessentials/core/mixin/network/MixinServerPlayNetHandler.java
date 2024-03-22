package com.forgeessentials.core.mixin.network;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.forgeessentials.util.events.world.SignEditEvent;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerPlayNetHandler
{

    @Shadow
    public ServerPlayer player;
    @Final
    @Shadow
    private static Logger LOGGER;

    /**
     * Post {@link SignEditEvent} to the event bus.
     *
     * @param p_244542_1_ the update sign packet
     * @param p_244542_2_ the list of string we don't want to use
     * @author maximuslotro
     * @reason Need to inject custom colors into sign text
     */
    @Inject(method = "updateSignText", 
    		at = @At("HEAD"),
    		cancellable = true)
    public void updateSignText(ServerboundSignUpdatePacket p_244542_1_, List<TextFilter.FilteredText> p_244542_2_, CallbackInfo ci)
    {
        this.player.resetLastActionTime();
        ServerLevel serverworld = this.player.getLevel();
        BlockPos blockpos = p_244542_1_.getPos();
        if (serverworld.hasChunkAt(blockpos)) {
            BlockState blockstate = serverworld.getBlockState(blockpos);
            BlockEntity tileentity = serverworld.getBlockEntity(blockpos);
            if (!(tileentity instanceof SignBlockEntity)) {
                return;
            }
            SignBlockEntity signtileentity = (SignBlockEntity)tileentity;
            if (signtileentity.getPlayerWhoMayEdit() != this.player.getUUID())
            {
                LOGGER.warn("Player {} just tried to change non-editable sign",
                        (Object) this.player.getDisplayName().getString());
                return;
            }
            ci.cancel();
            SignEditEvent event = new SignEditEvent(p_244542_1_.getPos(), p_244542_1_.getLines(), this.player);
            if (MinecraftForge.EVENT_BUS.post(event)) {
            	for(int i = 0; i < p_244542_1_.getLines().length; ++i) {
                    if (event.formatted[i] == null) {
                        signtileentity.setMessage(i, new TextComponent(p_244542_1_.getLines()[i]));
                    }
                    else {
                        signtileentity.setMessage(i, event.formatted[i]);
                    }
                }
            }
            else {
            	for(int i = 0; i < p_244542_1_.getLines().length; ++i) {
                    signtileentity.setMessage(i, new TextComponent(p_244542_1_.getLines()[i]));
                 }
            }

            signtileentity.setChanged();
            serverworld.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
        }
    }

}