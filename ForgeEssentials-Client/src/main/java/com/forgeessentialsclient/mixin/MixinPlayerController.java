package com.forgeessentialsclient.mixin;

import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.world.GameType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentialsclient.handler.ReachDistanceHandler;

@Mixin(PlayerController.class)
public abstract class MixinPlayerController
{
    
    @Shadow
    private GameType currentGameType;
    @Overwrite 
    public float getPickRange()
    {
        if (ReachDistanceHandler.getReachDistance() > 0)
            return ReachDistanceHandler.getReachDistance();
        return this.currentGameType.isCreative() ? 5.0F : 4.5F;
    }
    
}
