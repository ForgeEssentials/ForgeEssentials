package com.forgeessentials.core.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class BlockPortalSize
{
    private final World field_150867_a;
    private final int field_150865_b;
    private final int field_150866_c;
    private final int field_150863_d;
    public int field_150864_e = 0;
    private ChunkCoordinates field_150861_f;
    public int field_150862_g;
    public int field_150868_h;

    public BlockPortalSize(World p_i45415_1_, int p_i45415_2_, int p_i45415_3_, int p_i45415_4_, int p_i45415_5_)
    {
        this.field_150867_a = p_i45415_1_;
        this.field_150865_b = p_i45415_5_;
        this.field_150863_d = BlockPortal.field_150001_a[p_i45415_5_][0];
        this.field_150866_c = BlockPortal.field_150001_a[p_i45415_5_][1];

        for (int i1 = p_i45415_3_; p_i45415_3_ > i1 - 21 && p_i45415_3_ > 0
                && this.func_150857_a(p_i45415_1_.getBlock(p_i45415_2_, p_i45415_3_ - 1, p_i45415_4_)); --p_i45415_3_)
        { /* nop */
        }

        int j1 = this.func_150853_a(p_i45415_2_, p_i45415_3_, p_i45415_4_, this.field_150863_d) - 1;

        if (j1 >= 0)
        {
            this.field_150861_f = new ChunkCoordinates(p_i45415_2_ + j1 * Direction.offsetX[this.field_150863_d], p_i45415_3_, p_i45415_4_ + j1
                    * Direction.offsetZ[this.field_150863_d]);
            this.field_150868_h = this.func_150853_a(this.field_150861_f.posX, this.field_150861_f.posY, this.field_150861_f.posZ, this.field_150866_c);

            if (this.field_150868_h < 2 || this.field_150868_h > 21)
            {
                this.field_150861_f = null;
                this.field_150868_h = 0;
            }
        }

        if (this.field_150861_f != null)
        {
            this.field_150862_g = this.func_150858_a();
        }
    }

    protected int func_150853_a(int p_150853_1_, int p_150853_2_, int p_150853_3_, int p_150853_4_)
    {
        int j1 = Direction.offsetX[p_150853_4_];
        int k1 = Direction.offsetZ[p_150853_4_];
        int i1;
        Block block;

        for (i1 = 0; i1 < 22; ++i1)
        {
            block = this.field_150867_a.getBlock(p_150853_1_ + j1 * i1, p_150853_2_, p_150853_3_ + k1 * i1);

            if (!this.func_150857_a(block))
            {
                break;
            }

            Block block1 = this.field_150867_a.getBlock(p_150853_1_ + j1 * i1, p_150853_2_ - 1, p_150853_3_ + k1 * i1);

            if (block1 != Blocks.obsidian)
            {
                break;
            }
        }

        block = this.field_150867_a.getBlock(p_150853_1_ + j1 * i1, p_150853_2_, p_150853_3_ + k1 * i1);
        return block == Blocks.obsidian ? i1 : 0;
    }

    protected int func_150858_a()
    {
        int i;
        int j;
        int k;
        int l;
        label56:

        for (this.field_150862_g = 0; this.field_150862_g < 21; ++this.field_150862_g)
        {
            i = this.field_150861_f.posY + this.field_150862_g;

            for (j = 0; j < this.field_150868_h; ++j)
            {
                k = this.field_150861_f.posX + j * Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][1]];
                l = this.field_150861_f.posZ + j * Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][1]];
                Block block = this.field_150867_a.getBlock(k, i, l);

                if (!this.func_150857_a(block))
                {
                    break label56;
                }

                if (block == Blocks.portal)
                {
                    ++this.field_150864_e;
                }

                if (j == 0)
                {
                    block = this.field_150867_a.getBlock(k + Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][0]], i, l
                            + Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][0]]);

                    if (block != Blocks.obsidian)
                    {
                        break label56;
                    }
                }
                else if (j == this.field_150868_h - 1)
                {
                    block = this.field_150867_a.getBlock(k + Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][1]], i, l
                            + Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][1]]);

                    if (block != Blocks.obsidian)
                    {
                        break label56;
                    }
                }
            }
        }

        for (i = 0; i < this.field_150868_h; ++i)
        {
            j = this.field_150861_f.posX + i * Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][1]];
            k = this.field_150861_f.posY + this.field_150862_g;
            l = this.field_150861_f.posZ + i * Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][1]];

            if (this.field_150867_a.getBlock(j, k, l) != Blocks.obsidian)
            {
                this.field_150862_g = 0;
                break;
            }
        }

        if (this.field_150862_g <= 21 && this.field_150862_g >= 3)
        {
            return this.field_150862_g;
        }
        else
        {
            this.field_150861_f = null;
            this.field_150868_h = 0;
            this.field_150862_g = 0;
            return 0;
        }
    }

    protected boolean func_150857_a(Block p_150857_1_)
    {
        return p_150857_1_.getMaterial() == Material.air || p_150857_1_ == Blocks.fire || p_150857_1_ == Blocks.portal;
    }

    public boolean func_150860_b()
    {
        return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
    }

    public void func_150859_c()
    {
        for (int i = 0; i < this.field_150868_h; ++i)
        {
            int j = this.field_150861_f.posX + Direction.offsetX[this.field_150866_c] * i;
            int k = this.field_150861_f.posZ + Direction.offsetZ[this.field_150866_c] * i;

            for (int l = 0; l < this.field_150862_g; ++l)
            {
                int i1 = this.field_150861_f.posY + l;
                this.field_150867_a.setBlock(j, i1, k, Blocks.portal, this.field_150865_b, 2);
            }
        }
    }
}