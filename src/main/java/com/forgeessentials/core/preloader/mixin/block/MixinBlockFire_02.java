package com.forgeessentials.core.preloader.mixin.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fe.event.world.FireSpreadEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

@Mixin(BlockFire.class)
public abstract class MixinBlockFire_02 extends Block
{
    // dummy constructor, leave me alone
    public MixinBlockFire_02(Material material)
    {
        super(material);
    }

    @Shadow(remap = false) // Forge method
    public abstract boolean canCatchFire(IBlockAccess world, int x, int y, int z, ForgeDirection face);

    @Shadow
    abstract boolean canNeighborBurn(World p_149847_1_, int p_149847_2_, int p_149847_3_, int p_149847_4_);

    @Shadow
    abstract int getChanceOfNeighborsEncouragingFire(World p_149845_1_, int p_149845_2_, int p_149845_3_, int p_149845_4_);

    @Shadow(remap = false) // Forge method
    abstract void tryCatchFire(World p_149841_1_, int p_149841_2_, int p_149841_3_, int p_149841_4_, int p_149841_5_, Random p_149841_6_, int p_149841_7_, ForgeDirection face);

    @Overwrite
    public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        if (p_149674_1_.getGameRules().getGameRuleBooleanValue("doFireTick"))
        {
            boolean flag = p_149674_1_.getBlock(p_149674_2_, p_149674_3_ - 1, p_149674_4_)
                    .isFireSource(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_, UP);

            if (!this.canPlaceBlockAt(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_))
            {
                p_149674_1_.setBlockToAir(p_149674_2_, p_149674_3_, p_149674_4_);
            }

            if (!flag && p_149674_1_.isRaining() && (p_149674_1_.canLightningStrikeAt(p_149674_2_, p_149674_3_, p_149674_4_) || p_149674_1_
                    .canLightningStrikeAt(p_149674_2_ - 1, p_149674_3_, p_149674_4_) || p_149674_1_
                    .canLightningStrikeAt(p_149674_2_ + 1, p_149674_3_, p_149674_4_) || p_149674_1_
                    .canLightningStrikeAt(p_149674_2_, p_149674_3_, p_149674_4_ - 1) || p_149674_1_
                    .canLightningStrikeAt(p_149674_2_, p_149674_3_, p_149674_4_ + 1)))
            {
                p_149674_1_.setBlockToAir(p_149674_2_, p_149674_3_, p_149674_4_);
            }
            else
            {
                int l = p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_);

                if (l < 15)
                {
                    p_149674_1_.setBlockMetadataWithNotify(p_149674_2_, p_149674_3_, p_149674_4_, l + p_149674_5_.nextInt(3) / 2, 4);
                }

                p_149674_1_.scheduleBlockUpdate(p_149674_2_, p_149674_3_, p_149674_4_, this, this.tickRate(p_149674_1_) + p_149674_5_.nextInt(10));

                if (!flag && !this.canNeighborBurn(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_))
                {
                    if (!World.doesBlockHaveSolidTopSurface(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_) || l > 3)
                    {
                        p_149674_1_.setBlockToAir(p_149674_2_, p_149674_3_, p_149674_4_);
                    }
                }
                else if (!flag && !this.canCatchFire(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_, UP) && l == 15 && p_149674_5_.nextInt(4) == 0)
                {
                    p_149674_1_.setBlockToAir(p_149674_2_, p_149674_3_, p_149674_4_);
                }
                else
                {
                    boolean flag1 = p_149674_1_.isBlockHighHumidity(p_149674_2_, p_149674_3_, p_149674_4_);
                    byte b0 = 0;

                    if (flag1)
                    {
                        b0 = -50;
                    }

                    this.tryCatchFire(p_149674_1_, p_149674_2_ + 1, p_149674_3_, p_149674_4_, 300 + b0, p_149674_5_, l, WEST);
                    this.tryCatchFire(p_149674_1_, p_149674_2_ - 1, p_149674_3_, p_149674_4_, 300 + b0, p_149674_5_, l, EAST);
                    this.tryCatchFire(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_, 250 + b0, p_149674_5_, l, UP);
                    this.tryCatchFire(p_149674_1_, p_149674_2_, p_149674_3_ + 1, p_149674_4_, 250 + b0, p_149674_5_, l, DOWN);
                    this.tryCatchFire(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_ - 1, 300 + b0, p_149674_5_, l, SOUTH);
                    this.tryCatchFire(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_ + 1, 300 + b0, p_149674_5_, l, NORTH);

                    for (int i1 = p_149674_2_ - 1; i1 <= p_149674_2_ + 1; ++i1)
                    {
                        for (int j1 = p_149674_4_ - 1; j1 <= p_149674_4_ + 1; ++j1)
                        {
                            for (int k1 = p_149674_3_ - 1; k1 <= p_149674_3_ + 4; ++k1)
                            {
                                if (i1 != p_149674_2_ || k1 != p_149674_3_ || j1 != p_149674_4_)
                                {
                                    int l1 = 100;

                                    if (k1 > p_149674_3_ + 1)
                                    {
                                        l1 += (k1 - (p_149674_3_ + 1)) * 100;
                                    }

                                    int i2 = this.getChanceOfNeighborsEncouragingFire(p_149674_1_, i1, k1, j1);

                                    if (i2 > 0)
                                    {
                                        int j2 = (i2 + 40 + p_149674_1_.difficultySetting.getDifficultyId() * 7) / (l + 30);

                                        if (flag1)
                                        {
                                            j2 /= 2;
                                        }

                                        if (j2 > 0 && p_149674_5_.nextInt(l1) <= j2 && (!p_149674_1_.isRaining() || !p_149674_1_
                                                .canLightningStrikeAt(i1, k1, j1)) && !p_149674_1_.canLightningStrikeAt(i1 - 1, k1, p_149674_4_) && !p_149674_1_
                                                .canLightningStrikeAt(i1 + 1, k1, j1) && !p_149674_1_.canLightningStrikeAt(i1, k1, j1 - 1) && !p_149674_1_
                                                .canLightningStrikeAt(i1, k1, j1 + 1))
                                        {
                                            int k2 = l + p_149674_5_.nextInt(5) / 4;

                                            if (k2 > 15)
                                            {
                                                k2 = 15;
                                            }

                                            // FE: Throw event
                                            if (!MinecraftForge.EVENT_BUS.post(new FireSpreadEvent(i1, k1, j1, p_149674_1_, this, k2, p_149674_2_, p_149674_3_, p_149674_4_)))

                                            p_149674_1_.setBlock(i1, k1, j1, this, k2, 3);
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
}
