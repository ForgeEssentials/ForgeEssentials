package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.commands.util.TickTaskPulseHelper;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.tasks.TaskRegistry;


import cpw.mods.fml.server.FMLServerHandler;

public class CommandPulse extends FEcmdModuleCommands
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
        return par1ICommandSender.translateString("/pulse <X> <Y> <Z> [PulseLength]", new Object[0]);
    }
    public void processCommand(ICommandSender var1, String[] var2)
    {
        if (var2.length >= 3 && var2.length <= 4)
        {
            int var3 = 0;
            int var4 = 0;
            int var5 = 0;
            int var6 = 10;
            World var11 = null;
            if (var1 instanceof TileEntityCommandBlock)
            {
                var3 = (int)this.func_82368_a(var1, (double)((TileEntityCommandBlock)var1).xCoord, var2[0]);
                var4 = (int)this.func_82367_a(var1, (double)((TileEntityCommandBlock)var1).yCoord, var2[1], 0, 0);
                var5 = (int)this.func_82368_a(var1, (double)((TileEntityCommandBlock)var1).zCoord, var2[2]);
                var11 = ((TileEntityCommandBlock)var1).worldObj;
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
            
            TaskRegistry.registerTask(new TickTaskPulseHelper(var11,new Point(var3, var4, var5), var6));
            var1.sendChatToPlayer("Redstone Pulsed for " + var6 + " Ticks");
        }
        else
        {
            throw new WrongUsageException("/pulse <X> <Y> <Z> [PulseLength]", new Object[0]);
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
	public RegGroup getReggroup() {
		return RegGroup.MEMBERS;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) {
		EntityPlayerMP playermp = FunctionHelper.getPlayerForName(sender, sender.username);
		processCommand(playermp, args);
	}
	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		processCommand(sender, args);
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args) {
		return null;
	}

	@Override
	public String getCommandPerm() {
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
