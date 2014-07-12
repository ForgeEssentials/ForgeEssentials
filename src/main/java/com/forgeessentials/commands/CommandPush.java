package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLever;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommandPush extends FEcmdModuleCommands {
    public String getCommandName()
    {
        return "push";
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/push <X> <Y> <Z>";
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
            World var7 = null;

            if (var1 instanceof TileEntity)
            {
                var3 = (int) this.func_82368_a(var1, (double) ((TileEntity) var1).xCoord, var2[0]);
                var4 = (int) this.func_82367_a(var1, (double) ((TileEntity) var1).yCoord, var2[1], 0, 0);
                var5 = (int) this.func_82368_a(var1, (double) ((TileEntity) var1).zCoord, var2[2]);
                var7 = ((TileEntity) var1).getWorldObj();
            }
            else if (var1 instanceof EntityPlayerMP)
            {
                var3 = (int) this.func_82368_a(var1, ((EntityPlayerMP) var1).posX, var2[0]);
                var4 = (int) this.func_82367_a(var1, ((EntityPlayerMP) var1).posY, var2[1], 0, 0);
                var5 = (int) this.func_82368_a(var1, ((EntityPlayerMP) var1).posZ, var2[2]);
                var7 = ((EntityPlayerMP) var1).worldObj;
            }
            else if (var1 instanceof DedicatedServer)
            {
                var3 = (int) this.func_82368_a(var1, 0.0D, var2[0]);
                var4 = (int) this.func_82367_a(var1, 0.0D, var2[1], 0, 0);
                var5 = (int) this.func_82368_a(var1, 0.0D, var2[2]);
                var7 = ((DedicatedServer) var1).worldServerForDimension(0);
            }

            if ((var7.getBlock(var3, var4, var5) == Blocks.air || !((var7.getBlock(var3, var4, var5)) instanceof BlockButton))
                    && !(((var7.getBlock(var3, var4, var5)) instanceof BlockLever)))
            {
                throw new CommandException("Button/Lever Not Found");
            }
            else
            {
                var7.getBlock(var3, var4, var5).onBlockActivated(var7, var3, var4, var5, (EntityPlayer) null, 0, 0.0F, 0.0F, 0.0F);
                ChatUtils.sendMessage(var1, "Button/Lever Pushed");
            }
        }
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        EntityPlayerMP playermp = FunctionHelper.getPlayerForName(sender, sender.getCommandSenderName());
        if (args.length != 3)
        {
            throw new WrongUsageException("/push <X> <Y> <Z>", new Object[0]);
        }
        else
        {
            int var3 = 0;
            int var4 = 0;
            int var5 = 0;
            World var7 = null;

            var3 = (int) this.func_82368_a(playermp, ((EntityPlayerMP) playermp).posX, args[0]);
            var4 = (int) this.func_82367_a(playermp, ((EntityPlayerMP) playermp).posY, args[1], 0, 0);
            var5 = (int) this.func_82368_a(playermp, ((EntityPlayerMP) playermp).posZ, args[2]);
            var7 = ((EntityPlayerMP) playermp).worldObj;

            if ((var7.getBlock(var3, var4, var5) == Blocks.air || !((var7.getBlock(var3, var4, var5)) instanceof BlockButton))
                    && !(((var7.getBlock(var3, var4, var5)) instanceof BlockLever)))
            {
                throw new CommandException("Button/Lever Not Found");
            }
            else
            {
                var7.getBlock(var3, var4, var5).onBlockActivated(var7, var3, var4, var5, (EntityPlayer) null, 0, 0.0F, 0.0F, 0.0F);
                ChatUtils.sendMessage(sender, "Button/Lever Pushed");
            }
        }
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
            if (d1 < (double) par5)
            {
                throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] { Double.valueOf(d1), Integer.valueOf(par5) });
            }

            if (d1 > (double) par6)
            {
                throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] { Double.valueOf(d1), Integer.valueOf(par6) });
            }
        }

        return d1;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.MEMBERS;
    }

}
