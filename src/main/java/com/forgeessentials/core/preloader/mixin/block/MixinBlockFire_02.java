package com.forgeessentials.core.preloader.mixin.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.FireEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockFire.class)
public abstract class MixinBlockFire_02 extends Block
{
    // dummy constructor, leave me alone
    public MixinBlockFire_02(Material material)
    {
        super(material);
    }

    @Shadow(remap = false) // Forge method
    public abstract boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face);

    @Shadow
    abstract boolean func_176537_d(World p_149847_1_, BlockPos pos);

    @Shadow
    abstract boolean func_176533_e(World p_149847_1_, BlockPos pos);

    @Shadow(remap = false) // Forge method
    abstract void tryCatchFire(World p_149841_1_, BlockPos pos, int p_149841_5_, Random p_149841_6_, int p_149841_7_, EnumFacing face);

    @Shadow
    abstract int func_176538_m(World worldIn, BlockPos p_176538_2_);

    @Override
    @Overwrite
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.getGameRules().getGameRuleBooleanValue("doFireTick"))
        {
            if (!this.canPlaceBlockAt(worldIn, pos))
            {
                worldIn.setBlockToAir(pos);
            }

            Block block = worldIn.getBlockState(pos.offsetDown()).getBlock();
            boolean flag = block.isFireSource(worldIn, pos.offsetDown(), EnumFacing.UP);

            if (!flag && worldIn.isRaining() && this.func_176537_d(worldIn, pos))
            {
                worldIn.setBlockToAir(pos);
            }
            else
            {
                int i = ((Integer)state.getValue(BlockFire.field_176543_a)).intValue();

                if (i < 15)
                {
                    state = state.withProperty(BlockFire.field_176543_a, Integer.valueOf(i + rand.nextInt(3) / 2));
                    worldIn.setBlockState(pos, state, 4);
                }

                worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(10));

                if (!flag)
                {
                    if (!this.func_176533_e(worldIn, pos))
                    {
                        if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.offsetDown()) || i > 3)
                        {
                            worldIn.setBlockToAir(pos);
                        }

                        return;
                    }

                    if (!this.canCatchFire(worldIn, pos.offsetDown(), EnumFacing.UP) && i == 15 && rand.nextInt(4) == 0)
                    {
                        worldIn.setBlockToAir(pos);
                        return;
                    }
                }

                boolean flag1 = worldIn.func_180502_D(pos);
                byte b0 = 0;

                if (flag1)
                {
                    b0 = -50;
                }

                this.tryCatchFire(worldIn, pos.offsetEast(), 300 + b0, rand, i, EnumFacing.WEST);
                this.tryCatchFire(worldIn, pos.offsetWest(), 300 + b0, rand, i, EnumFacing.EAST);
                this.tryCatchFire(worldIn, pos.offsetDown(), 250 + b0, rand, i, EnumFacing.UP);
                this.tryCatchFire(worldIn, pos.offsetUp(), 250 + b0, rand, i, EnumFacing.DOWN);
                this.tryCatchFire(worldIn, pos.offsetNorth(), 300 + b0, rand, i, EnumFacing.SOUTH);
                this.tryCatchFire(worldIn, pos.offsetSouth(), 300 + b0, rand, i, EnumFacing.NORTH);

                for (int j = -1; j <= 1; ++j)
                {
                    for (int k = -1; k <= 1; ++k)
                    {
                        for (int l = -1; l <= 4; ++l)
                        {
                            if (j != 0 || l != 0 || k != 0)
                            {
                                int i1 = 100;

                                if (l > 1)
                                {
                                    i1 += (l - 1) * 100;
                                }

                                BlockPos blockpos1 = pos.add(j, l, k);
                                int j1 = this.func_176538_m(worldIn, blockpos1);

                                if (j1 > 0)
                                {
                                    int k1 = (j1 + 40 + worldIn.getDifficulty().getDifficultyId() * 7) / (i + 30);

                                    if (flag1)
                                    {
                                        k1 /= 2;
                                    }

                                    if (k1 > 0 && rand.nextInt(i1) <= k1 && (!worldIn.isRaining() || !this.func_176537_d(worldIn, blockpos1)))
                                    {
                                        int l1 = i + rand.nextInt(5) / 4;

                                        if (l1 > 15)
                                        {
                                            l1 = 15;
                                        }

                                        if (!MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(worldIn, pos, state.withProperty(BlockFire.field_176543_a, Integer.valueOf(l1)), blockpos1)));
                                        worldIn.setBlockState(blockpos1, state.withProperty(BlockFire.field_176543_a, Integer.valueOf(l1)), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
}
