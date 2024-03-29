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

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

@Mixin(ServerPlayNetHandler.class)
public class MixinServerPlayNetHandler
{

    @Shadow
    public ServerPlayerEntity player;
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
    public void updateSignText(CUpdateSignPacket p_244542_1_, List<String> p_244542_2_, CallbackInfo ci)
    {
        this.player.resetLastActionTime();
        ServerWorld serverworld = this.player.getLevel();
        BlockPos blockpos = p_244542_1_.getPos();
        if (serverworld.hasChunkAt(blockpos)) {
            BlockState blockstate = serverworld.getBlockState(blockpos);
            TileEntity tileentity = serverworld.getBlockEntity(blockpos);
            if (!(tileentity instanceof SignTileEntity)) {
                return;
            }
            SignTileEntity signtileentity = (SignTileEntity)tileentity;
            if (signtileentity.getPlayerWhoMayEdit() != this.player)
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
                        signtileentity.setMessage(i, new StringTextComponent(p_244542_1_.getLines()[i]));
                    }
                    else {
                        signtileentity.setMessage(i, event.formatted[i]);
                    }
                }
            }
            else {
            	for(int i = 0; i < p_244542_1_.getLines().length; ++i) {
                    signtileentity.setMessage(i, new StringTextComponent(p_244542_1_.getLines()[i]));
                 }
            }

            signtileentity.setChanged();
            serverworld.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
        }
    }

}