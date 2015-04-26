package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.TickTaskPulseHelper;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.tasks.TaskRegistry;

public class CommandPulse extends FEcmdModuleCommands {
	
    @Override
	public String getCommandName()
    {
        return "pulse";
    }

    @Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/pulse <X> <Y> <Z> [Pulse Length]";
    }

    @Override
	public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length >= 3 && args.length <= 4)
        {
            int var3 = 0;
            int var4 = 0;
            int var5 = 0;
            int var6 = 10;
            World var11 = null;
            if (sender instanceof TileEntityCommandBlock)
            {
                var3 = (int) this.func_82368_a(sender, ((TileEntityCommandBlock) sender).xCoord, args[0]);
                var4 = (int) this.func_82367_a(sender, ((TileEntityCommandBlock) sender).yCoord, args[1], 0, 0);
                var5 = (int) this.func_82368_a(sender, ((TileEntityCommandBlock) sender).zCoord, args[2]);
                var11 = ((TileEntityCommandBlock) sender).getWorldObj();
            }
            else if (sender instanceof EntityPlayerMP)
            {
                var3 = (int) this.func_82368_a(sender, ((EntityPlayerMP) sender).posX, args[0]);
                var4 = (int) this.func_82367_a(sender, ((EntityPlayerMP) sender).posY, args[1], 0, 0);
                var5 = (int) this.func_82368_a(sender, ((EntityPlayerMP) sender).posZ, args[2]);
                var11 = ((EntityPlayerMP) sender).worldObj;
            }
            else if (sender instanceof DedicatedServer)
            {
                var3 = (int) this.func_82368_a(sender, 0.0D, args[0]);
                var4 = (int) this.func_82367_a(sender, 0.0D, args[1], 0, 0);
                var5 = (int) this.func_82368_a(sender, 0.0D, args[2]);
                var11 = ((DedicatedServer) sender).worldServerForDimension(0);
            }

            if (args.length >= 4)
            {
                var6 = parseIntWithMin(sender, args[3], 1);
            }

            TaskRegistry.registerTask(new TickTaskPulseHelper(var11, new Point(var3, var4, var5), var6));
            OutputHandler.chatConfirmation(sender, "Redstone Pulsed for " + var6 + " Ticks");
        }
        else
        {
        	throw new TranslatedCommandException(getCommandUsage(sender));
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
            if (d1 < par5)
            {
                throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] { Double.valueOf(d1), Integer.valueOf(par5) });
            }

            if (d1 > par6)
            {
                throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] { Double.valueOf(d1), Integer.valueOf(par6) });
            }
        }

        return d1;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        EntityPlayerMP playermp = UserIdent.getPlayerByMatchOrUsername(sender, sender.getCommandSenderName());
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
}
