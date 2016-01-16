package com.forgeessentials.core.preloader.injections;

import java.util.Random;

import net.minecraft.block.BlockFire;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fe.event.world.FireEvent;

import com.forgeessentials.core.preloader.asminjector.CallbackInfo;
import com.forgeessentials.core.preloader.asminjector.annotation.At;
import com.forgeessentials.core.preloader.asminjector.annotation.At.Shift;
import com.forgeessentials.core.preloader.asminjector.annotation.Inject;
import com.forgeessentials.core.preloader.asminjector.annotation.Local;
import com.forgeessentials.core.preloader.asminjector.annotation.Mixin;

@Mixin(BlockFire.class)
public abstract class MixinBlockFire extends BlockFire
{

    @Inject(target = "tryCatchFire(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V",
            aliases = { "setBlock=func_147465_d" },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z", shift = Shift.LAST_LABEL) )
    public void handleTryCatchFireA(World world, int x, int y, int z, int chance, Random rnd, int argValue1, ForgeDirection face, CallbackInfo ci)
    {
        //System.out.println("Injector: Fire destroyed block and spread to below block");
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(x, y, z, world, this, argValue1)))
        {
            ci.doReturn();
        }
    }

    @Inject(target = "tryCatchFire(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V",
            aliases = { "setBlockToAir=func_147468_f" },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z", shift = Shift.LAST_LABEL) )
    public void handleTryCatchFireB(World world, int x, int y, int z, int chance, Random rnd, int argValue1, ForgeDirection face, CallbackInfo ci)
    {
        //System.out.println("Injector: Fire destroyed block");
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(x, y, z, world, this, argValue1)))
        {
            ci.doReturn();
        }
    }

    @Inject(target = "updateTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
            aliases = { "updateTick=func_149674_a", "setBlock=func_147465_d" },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z", shift = Shift.LAST_LABEL) )
    public void updateTick(World world, int sourceX, int sourceY, int sourceZ, Random rnd, CallbackInfo ci, @Local("p_149674_2_") int x,
            @Local("p_149674_4_") int y, @Local("p_149674_3_") int z)
    {
        //System.out.println(String.format("Injector: Fire spreading to other block from [%d,%d,%d] to [%d,%d,%d]", sourceX, sourceY, sourceZ, x, y, z));
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(x, y, z, world, this, 3, sourceX, sourceY, sourceZ)))
        {
            ci.doReturn();
        }
    }

}
