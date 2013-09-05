package com.ForgeEssentials.commands;

import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;

public class CommandDrop extends FEcmdModuleCommands
{
	public String getCommandName()
	{
		return "drop";
	}

	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	public String getCommandUsage(ICommandSender par1ICommandSender)
	{
		return "/drop <X> <Y> <Z> <ItemID> <Meta> <Qty>";
	}

	public void processCommand(ICommandSender var1, String[] var2)
	{
		if (var2.length != 6)
			throw new WrongUsageException("/drop <X> <Y> <Z> <ItemID> <Meta> <Qty>", new Object[0]);
		Object var3 = null;
		int var4 = (int) this.func_82368_a(var1, 0.0D, var2[0]);
		int var5 = (int) this.func_82367_a(var1, 0.0D, var2[1], 0, 0);
		int var6 = (int) this.func_82368_a(var1, 0.0D, var2[2]);

		if (var1 instanceof DedicatedServer)
		{
			var3 = ((DedicatedServer) var1).worldServerForDimension(0);
		}
		else if (var1 instanceof EntityPlayerMP)
		{
			var3 = ((Entity) var1).worldObj;
			var4 = (int) this.func_82368_a(var1, ((Entity) var1).posX, var2[0]);
			var5 = (int) this.func_82367_a(var1, ((Entity) var1).posY, var2[1], 0, 0);
			var6 = (int) this.func_82368_a(var1, ((Entity) var1).posZ, var2[2]);
		}
		else if (var1 instanceof TileEntity)
		{
			var3 = ((TileEntity) var1).worldObj;
			var4 = (int) this.func_82368_a(var1, ((TileEntity) var1).xCoord, var2[0]);
			var5 = (int) this.func_82367_a(var1, ((TileEntity) var1).yCoord, var2[1], 0, 0);
			var6 = (int) this.func_82368_a(var1, ((TileEntity) var1).zCoord, var2[2]);
		}
		int var7 = parseIntWithMin(var1, var2[3], 1);
		int var8 = parseIntWithMin(var1, var2[4], 0);
		int var9 = parseIntBounded(var1, var2[5], 1, Item.itemsList[var7].getItemStackLimit());
		int var11;
		ItemStack var10000;

		if (((World) var3).getBlockTileEntity(var4, var5, var6) instanceof TileEntityChest)
		{
			TileEntityChest var10 = (TileEntityChest) ((World) var3).getBlockTileEntity(var4, var5, var6);

			for (var11 = 0; var11 < var10.getSizeInventory(); ++var11)
			{
				if (var10.getStackInSlot(var11) == null)
				{
					var10.setInventorySlotContents(var11, new ItemStack(var7, var9, var8));
					break;
				}

				if (var10.getStackInSlot(var11).itemID == var7 && var10.getStackInSlot(var11).getItemDamage() == var8)
				{
					if (var10.getStackInSlot(var11).getMaxStackSize() - var10.getStackInSlot(var11).stackSize >= var9)
					{
						var10000 = var10.getStackInSlot(var11);
						var10000.stackSize += var9;
						break;
					}

					var9 -= var10.getStackInSlot(var11).getMaxStackSize() - var10.getStackInSlot(var11).stackSize;
					var10.getStackInSlot(var11).stackSize = var10.getStackInSlot(var11).getMaxStackSize();
				}
			}
		}
		else if (((World) var3).getBlockTileEntity(var4, var5, var6) instanceof TileEntityDropper)
		{
			TileEntityDropper var13 = (TileEntityDropper) ((World) var3).getBlockTileEntity(var4, var5, var6);

			for (var11 = 0; var11 < var13.getSizeInventory(); ++var11)
			{
				if (var13.getStackInSlot(var11) == null)
				{
					var13.setInventorySlotContents(var11, new ItemStack(var7, var9, var8));
					break;
				}

				if (var13.getStackInSlot(var11).itemID == var7 && var13.getStackInSlot(var11).getItemDamage() == var8)
				{
					if (var13.getStackInSlot(var11).getMaxStackSize() - var13.getStackInSlot(var11).stackSize >= var9)
					{
						var10000 = var13.getStackInSlot(var11);
						var10000.stackSize += var9;
						break;
					}

					var9 -= var13.getStackInSlot(var11).getMaxStackSize() - var13.getStackInSlot(var11).stackSize;
					var13.getStackInSlot(var11).stackSize = var13.getStackInSlot(var11).getMaxStackSize();
				}
			}
		}
		else if (((World) var3).getBlockTileEntity(var4, var5, var6) instanceof TileEntityDispenser)
		{
			TileEntityDispenser var14 = (TileEntityDispenser) ((World) var3).getBlockTileEntity(var4, var5, var6);

			for (var11 = 0; var11 < var14.getSizeInventory(); ++var11)
			{
				if (var14.getStackInSlot(var11) == null)
				{
					var14.setInventorySlotContents(var11, new ItemStack(var7, var9, var8));
					break;
				}

				if (var14.getStackInSlot(var11).itemID == var7 && var14.getStackInSlot(var11).getItemDamage() == var8)
				{
					if (var14.getStackInSlot(var11).getMaxStackSize() - var14.getStackInSlot(var11).stackSize >= var9)
					{
						var10000 = var14.getStackInSlot(var11);
						var10000.stackSize += var9;
						break;
					}

					var9 -= var14.getStackInSlot(var11).getMaxStackSize() - var14.getStackInSlot(var11).stackSize;
					var14.getStackInSlot(var11).stackSize = var14.getStackInSlot(var11).getMaxStackSize();
				}
			}
		}
		else if (((World) var3).getBlockTileEntity(var4, var5, var6) instanceof TileEntityHopper)
		{
			TileEntityHopper var12 = (TileEntityHopper) ((World) var3).getBlockTileEntity(var4, var5, var6);

			for (var11 = 0; var11 < var12.getSizeInventory(); ++var11)
			{
				if (var12.getStackInSlot(var11) == null)
				{
					var12.setInventorySlotContents(var11, new ItemStack(var7, var9, var8));
					var9 = 0;
					break;
				}

				if (var12.getStackInSlot(var11).itemID == var7 && var12.getStackInSlot(var11).getItemDamage() == var8)
				{
					if (var12.getStackInSlot(var11).getMaxStackSize() - var12.getStackInSlot(var11).stackSize >= var9)
					{
						var10000 = var12.getStackInSlot(var11);
						var10000.stackSize += var9;
						var9 = 0;
						break;
					}

					var9 -= var12.getStackInSlot(var11).getMaxStackSize() - var12.getStackInSlot(var11).stackSize;
					var12.getStackInSlot(var11).stackSize = var12.getStackInSlot(var11).getMaxStackSize();
				}
			}
		}
		else
		{
			throw new CommandException("No viable Container found to put item in");
		}
		if (var9 > 0)
		{
			throw new CommandException("Not enough Room for Items");
		}
		ChatUtils.sendMessage(var1, "Items Droped into container");
	}

	private double func_82368_a(ICommandSender par1ICommandSender, double par2, String par4Str)
    {
        return this.func_82367_a(par1ICommandSender, par2, par4Str, -30000000, 30000000);
    }

    private double func_82367_a(ICommandSender par1ICommandSender, double par2, String par4Str, int par5, int par6)
    {
        boolean flag = par4Str.startsWith("~");
        double d1 = flag ? par2 : 0.0D;

        if (!flag || par4Str.length() > 1)
        {
            boolean flag1 = par4Str.contains(".");

            if (flag)
            {
                par4Str = par4Str.substring(1);
            }

            d1 += parseDouble(par1ICommandSender, par4Str);

            if (!flag1 && !flag)
            {
                d1 += 0.5D;
            }
        }

        if (par5 != 0 || par6 != 0)
        {
            if (d1 < (double)par5)
            {
                throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d1), Integer.valueOf(par5)});
            }

            if (d1 > (double)par6)
            {
                throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d1), Integer.valueOf(par6)});
            }
        }

        return d1;
    }
	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.MEMBERS;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		EntityPlayerMP playermp = FunctionHelper.getPlayerForName(sender, sender.username);
		processCommand(playermp, args);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		processCommand(sender, args);
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
