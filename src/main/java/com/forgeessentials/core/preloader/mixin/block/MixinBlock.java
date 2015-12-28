package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.core.preloader.api.ServerBlock;

@Mixin(Block.class)
public class MixinBlock implements ServerBlock
{

    @Shadow
    protected String textureName;

    @Shadow
    private String unlocalizedName;

    @Override
    public String getTextureNameServer()
    {
        return this.textureName == null ? "MISSING_ICON_BLOCK_" + Block.getIdFromBlock((Block) (Object) this) + "_" + this.unlocalizedName : this.textureName;
    }

}
