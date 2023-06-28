package com.forgeessentials.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.client.ForgeEssentialsClient;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity
{

    /**
     * Solve for noClip functionality
     * @author Maximuslotro
     * @reason stuff
     */
	@Redirect(method = "tick",
            at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z")
    )
    public boolean onUpdate_NoClip(PlayerEntity _this) {
        //Minecraft instance = Minecraft.getInstance();
        if(ForgeEssentialsClient.noClip){
        	if(!ForgeEssentialsClient.noClipChanged) {
                //instance.gui.getChat().addMessage(new StringTextComponent("Noclip true"));
                ForgeEssentialsClient.noClipChanged=true;
        	}
            return true;

        }
        else {
        	if(ForgeEssentialsClient.noClipChanged) {
                //instance.gui.getChat().addMessage(new StringTextComponent("Noclip false"));
                ForgeEssentialsClient.noClipChanged=false;
        	}
            return _this.isSpectator();
        }
    }
}
