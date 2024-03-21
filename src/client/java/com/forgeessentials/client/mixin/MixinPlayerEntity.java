package com.forgeessentials.client.mixin;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.client.ForgeEssentialsClient;

@Mixin(Player.class)
public abstract class MixinPlayerEntity
{

    /**
     * Solve for noClip functionality
     * 
     * @author Maximuslotro
     * @reason stuff
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z"))
    public boolean onUpdate_NoClip(Player _this)
    {
        // Minecraft instance = Minecraft.getInstance();
        if (ForgeEssentialsClient.noClip)
        {
            if (!ForgeEssentialsClient.noClipChanged)
            {
                // instance.gui.getChat().addMessage(new StringTextComponent("Noclip true"));
                ForgeEssentialsClient.noClipChanged = true;
            }
            return true;

        }
        else
        {
            if (ForgeEssentialsClient.noClipChanged)
            {
                // instance.gui.getChat().addMessage(new StringTextComponent("Noclip false"));
                ForgeEssentialsClient.noClipChanged = false;
            }
            return _this.isSpectator();
        }
    }
}
