package com.forgeessentials.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.client.ForgeEssentialsClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends ClientPlayerEntity
{

    public MixinPlayerEntity(Minecraft p_i232461_1_, ClientWorld p_i232461_2_, ClientPlayNetHandler p_i232461_3_, StatisticsManager p_i232461_4_,
            ClientRecipeBook p_i232461_5_, boolean p_i232461_6_, boolean p_i232461_7_)
    {
        super(p_i232461_1_, p_i232461_2_, p_i232461_3_, p_i232461_4_, p_i232461_5_, p_i232461_6_, p_i232461_7_);
    }

    @Redirect(method = "tick",
            at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z")
    )
    public boolean onUpdate_NoClip(PlayerEntity _this) {
        Minecraft instance = Minecraft.getInstance();
        if(ForgeEssentialsClient.noClip){
            instance.gui.getChat().addMessage(new StringTextComponent("Noclip true"));
            return true;

        }
        else {
            instance.gui.getChat().addMessage(new StringTextComponent("Noclip false"));
            NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(_this.getGameProfile().getId());
            return networkplayerinfo != null && networkplayerinfo.getGameMode() == GameType.SPECTATOR;
        }
    }
}
