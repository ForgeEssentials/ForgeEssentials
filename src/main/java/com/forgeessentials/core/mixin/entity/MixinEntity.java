package com.forgeessentials.core.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.ModuleCommandsEventHandler;
import com.forgeessentials.util.events.entity.PressurePlateEvent;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;

@Mixin(Entity.class)
public abstract class MixinEntity
{
    /**
     * Send pressure-plate event on plate depress
     * 
     * @author Maximuslotro
     * @reason we want to add perms so players can't activate redstone in protected areas without permission
     */
    @Overwrite
    public boolean isIgnoringBlockTriggers()
    {
        return MinecraftForge.EVENT_BUS.post(new PressurePlateEvent((Entity) (Object) this));
    }

    /**
     * Custom fix afk players being pushed by entities
     * 
     * @author Maximuslotro
     * @reason fix afk players being pushed by entities
     */
    @Inject(at = @At("RETURN"),
            method = "canBeCollidedWith()Z",
            cancellable = true)
    public void isAfkPlayer(CallbackInfoReturnable<Boolean> callback)
    {
        if (((Entity) (Object) this) instanceof PlayerEntity)
        {
            if (ModuleCommandsEventHandler.isAfk(UserIdent.get((PlayerEntity) (Object) this)))
            {
                if (!callback.getReturnValue())
                {
                    callback.setReturnValue(true);
                    if (callback.getReturnValue())
                    {
                        LoggingHandler.felog.debug("Prevented afk player from being moved");
                    }
                }
            }
        }
    }

}