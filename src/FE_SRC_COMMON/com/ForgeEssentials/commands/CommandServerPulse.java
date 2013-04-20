package com.ForgeEssentials.commands;

import com.ForgeEssentials.commands.util.BlockPoweredOreMod;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.*;
import net.minecraft.world.World;

public class CommandServerPulse extends CommandBase
{
    public String getCommandName()
    {
        return "pulse";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.translateString("/pulse <X> <Y> <Z> [PulseLength] [pulsestrength]", new Object[0]);
    }
    public void processCommand(ICommandSender var1, String[] var2)
    {
        if (var2.length >= 3 && var2.length <= 5)
        {
            int var3 = 0;
            int var4 = 0;
            int var5 = 0;
            int var6 = 10;
            int var7 = 0;
            int var8 = 0;
            Object var9 = null;
            Object var11 = null;

            if (var1 instanceof TileEntity)
            {
                var3 = (int)this.func_82368_a(var1, (double)((TileEntity)var1).xCoord, var2[0]);
                var4 = (int)this.func_82367_a(var1, (double)((TileEntity)var1).yCoord, var2[1], 0, 0);
                var5 = (int)this.func_82368_a(var1, (double)((TileEntity)var1).zCoord, var2[2]);
                var11 = ((TileEntity)var1).worldObj;
            }
            else if (var1 instanceof EntityPlayerMP)
            {
                var3 = (int)this.func_82368_a(var1, ((EntityPlayerMP)var1).posX, var2[0]);
                var4 = (int)this.func_82367_a(var1, ((EntityPlayerMP)var1).posY, var2[1], 0, 0);
                var5 = (int)this.func_82368_a(var1, ((EntityPlayerMP)var1).posZ, var2[2]);
                var11 = ((EntityPlayerMP)var1).worldObj;
            }
            else if (var1 instanceof DedicatedServer)
            {
                var3 = (int)this.func_82368_a(var1, 0.0D, var2[0]);
                var4 = (int)this.func_82367_a(var1, 0.0D, var2[1], 0, 0);
                var5 = (int)this.func_82368_a(var1, 0.0D, var2[2]);
                var11 = ((DedicatedServer)var1).worldServerForDimension(0);
            }

            if (var2.length >= 4)
            {
                var6 = parseIntWithMin(var1, var2[3], 1);
            }

            if (var2.length == 5)
            {
                parseIntBounded(var1, var2[4], 1, 15);
            }

            if (((World)var11).getBlockId(var3, var4, var5) != 0)
            {
                var7 = Block.blocksList[((World)var11).getBlockId(var3, var4, var5)].blockID;
                var8 = ((World)var11).getBlockMetadata(var3, var4, var5);
                var9 = ((World)var11).getBlockTileEntity(var3, var4, var5);
                if (var9 != null && !(var9 instanceof TileEntityBrewingStand)&& !(var9 instanceof TileEntityChest)&& !(var9 instanceof TileEntityComparator)&& !(var9 instanceof TileEntityDaylightDetector)&& !(var9 instanceof TileEntityDispenser)
                		&& !(var9 instanceof TileEntityDropper) && !(var9 instanceof TileEntityEnchantmentTable) && !(var9 instanceof TileEntityEnderChest) && !(var9 instanceof TileEntityFurnace) && !(var9 instanceof TileEntityHopper) 
                		&& !(var9 instanceof TileEntityNote) && !(var9 instanceof TileEntityPiston) && !(var9 instanceof TileEntityRecordPlayer) && !(var9 instanceof TileEntitySign))
                {
                    throw new CommandException("Can not pulse where Block has a Tile Entity that can not be preserved", new Object[0]);
                }
                if (!(var9 instanceof TileEntityChest)&& !(var9 instanceof TileEntityDispenser)&& !(var9 instanceof TileEntityDropper)&& !(var9 instanceof TileEntityHopper)){
                	var9 = null;
                }
                
            }

            ((World)var11).setBlock(var3, var4, var5, 152, 0, 3);

            if (var2.length == 5)
            {
                ((BlockPoweredOreMod)Block.blocksList[((World)var11).getBlockId(var3, var4, var5)]).strength = parseIntBounded(var1, var2[4], 1, 15);
                ((World)var11).notifyBlocksOfNeighborChange(var3, var4, var5, 152);
            }

            ((BlockPoweredOreMod)Block.blocksList[((World)var11).getBlockId(var3, var4, var5)]).isPulsed = true;
            ((BlockPoweredOreMod)Block.blocksList[((World)var11).getBlockId(var3, var4, var5)]).previousBlockID = var7;
            ((BlockPoweredOreMod)Block.blocksList[((World)var11).getBlockId(var3, var4, var5)]).previusMetaData = var8;
            ((BlockPoweredOreMod)Block.blocksList[((World)var11).getBlockId(var3, var4, var5)]).previusEntity = var9;
            ((World)var11).scheduleBlockUpdate(var3, var4, var5, 152, var6 + 1);
            var1.sendChatToPlayer("Redstone Pulsed for " + var6 + " Ticks at Power level " + ((BlockPoweredOreMod)Block.blocksList[((World)var11).getBlockId(var3, var4, var5)]).strength);
        }
        else
        {
            throw new WrongUsageException("/pulse <X> <Y> <Z> [PulseLength] [pulsestrength]", new Object[0]);
        }
    }

    private double func_82368_a(ICommandSender var1, double var2, String var4)
    {
        return this.func_82367_a(var1, var2, var4, -30000000, 30000000);
    }

    private double func_82367_a(ICommandSender var1, double var2, String var4, int var5, int var6)
    {
        boolean var7 = var4.startsWith("~");
        double var8 = var7 ? var2 : 0.0D;

        if (!var7 || var4.length() > 1)
        {
            boolean var10 = var4.contains(".");

            if (var7)
            {
                var4 = var4.substring(1);
            }

            var8 += func_82363_b(var1, var4);

            if (!var10 && !var7)
            {
                var8 += 0.5D;
            }
        }

        if (var5 != 0 || var6 != 0)
        {
            if (var8 < (double)var5)
            {
                throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(var8), Integer.valueOf(var5)});
            }

            if (var8 > (double)var6)
            {
                throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(var8), Integer.valueOf(var6)});
            }
        }

        return var8;
    }
}
