package com.ForgeEssentials.commands;

import java.util.List;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLever;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommandPush extends ForgeEssentialsCommandBase
{
    public String getCommandName()
    {
        return "push";
    }
  
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.translateString("/push <X> <Y> <Z>", new Object[0]);
    }
    
    @Override
    public void processCommandConsole(ICommandSender var1, String[] var2)
    {
        if (var2.length != 3)
        {
            throw new WrongUsageException("/push <X> <Y> <Z>", new Object[0]);
        }
        else
        {
            int var3 = 0;
            int var4 = 0;
            int var5 = 0;
            Object var7 = null;

            if (var1 instanceof TileEntity)
            {
                var3 = (int)this.func_82368_a(var1, (double)((TileEntity)var1).xCoord, var2[0]);
                var4 = (int)this.func_82367_a(var1, (double)((TileEntity)var1).yCoord, var2[1], 0, 0);
                var5 = (int)this.func_82368_a(var1, (double)((TileEntity)var1).zCoord, var2[2]);
                var7 = ((TileEntity)var1).worldObj;
            }
            else if (var1 instanceof EntityPlayerMP)
            {
                var3 = (int)this.func_82368_a(var1, ((EntityPlayerMP)var1).posX, var2[0]);
                var4 = (int)this.func_82367_a(var1, ((EntityPlayerMP)var1).posY, var2[1], 0, 0);
                var5 = (int)this.func_82368_a(var1, ((EntityPlayerMP)var1).posZ, var2[2]);
                var7 = ((EntityPlayerMP)var1).worldObj;
            }
            else if (var1 instanceof DedicatedServer)
            {
                var3 = (int)this.func_82368_a(var1, 0.0D, var2[0]);
                var4 = (int)this.func_82367_a(var1, 0.0D, var2[1], 0, 0);
                var5 = (int)this.func_82368_a(var1, 0.0D, var2[2]);
                var7 = ((DedicatedServer)var1).worldServerForDimension(0);
            }

            if ((((World)var7).getBlockId(var3, var4, var5) == 0 || !(Block.blocksList[((World)var7).getBlockId(var3, var4, var5)] instanceof BlockButton)) && !(Block.blocksList[((World)var7).getBlockId(var3, var4, var5)] instanceof BlockLever))
            {
                 throw new CommandException("Button/Lever Not Found");
            }
            else
            {
                Block.blocksList[((World)var7).getBlockId(var3, var4, var5)].onBlockActivated((World)var7, var3, var4, var5, (EntityPlayer)null, 0, 0.0F, 0.0F, 0.0F);
                var1.sendChatToPlayer("Button/Lever Pushed");
            }
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

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canConsoleUseCommand() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandPerm() {
		// TODO Auto-generated method stub
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
