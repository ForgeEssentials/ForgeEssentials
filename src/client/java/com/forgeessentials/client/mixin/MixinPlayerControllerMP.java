package com.forgeessentials.client.mixin;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.world.GameType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.client.handler.ReachDistanceHandler;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP
{
    
    @Shadow
    private GameType currentGameType;

    @Overwrite
    public float getBlockReachDistance()
    {
        if (ReachDistanceHandler.getReachDistance() > 0)
            return ReachDistanceHandler.getReachDistance();
        return this.currentGameType.isCreative() ? 5.0F : 4.5F;
    }

}
