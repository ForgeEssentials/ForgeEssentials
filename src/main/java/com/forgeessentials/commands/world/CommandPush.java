package com.forgeessentials.commands.world;

import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLever;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandPush extends FEcmdModuleCommands
{

    @Override
    public String getCommandName()
    {
        return "push";
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/push <X> <Y> <Z>";
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length != 3)
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
        else
        {
            int var3 = 0;
            int var4 = 0;
            int var5 = 0;
            World var7 = null;

            if (sender instanceof TileEntity)
            {
                var3 = (int) this.func_82368_a(sender, ((TileEntity) sender).xCoord, args[0]);
                var4 = (int) this.func_82367_a(sender, ((TileEntity) sender).yCoord, args[1], 0, 0);
                var5 = (int) this.func_82368_a(sender, ((TileEntity) sender).zCoord, args[2]);
                var7 = ((TileEntity) sender).getWorldObj();
            }
            else if (sender instanceof EntityPlayerMP)
            {
                var3 = (int) this.func_82368_a(sender, ((EntityPlayerMP) sender).posX, args[0]);
                var4 = (int) this.func_82367_a(sender, ((EntityPlayerMP) sender).posY, args[1], 0, 0);
                var5 = (int) this.func_82368_a(sender, ((EntityPlayerMP) sender).posZ, args[2]);
                var7 = ((EntityPlayerMP) sender).worldObj;
            }
            else if (sender instanceof DedicatedServer)
            {
                var3 = (int) this.func_82368_a(sender, 0.0D, args[0]);
                var4 = (int) this.func_82367_a(sender, 0.0D, args[1], 0, 0);
                var5 = (int) this.func_82368_a(sender, 0.0D, args[2]);
                var7 = ((DedicatedServer) sender).worldServerForDimension(0);
            }

            if ((var7.getBlock(var3, var4, var5) == Blocks.air || !((var7.getBlock(var3, var4, var5)) instanceof BlockButton))
                    && !(((var7.getBlock(var3, var4, var5)) instanceof BlockLever)))
            {
                throw new TranslatedCommandException("Button/Lever Not Found");
            }
            else
            {
                var7.getBlock(var3, var4, var5).onBlockActivated(var7, var3, var4, var5, (EntityPlayer) null, 0, 0.0F, 0.0F, 0.0F);
                ChatOutputHandler.chatConfirmation(sender, "Button/Lever Pushed");
            }
        }
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        EntityPlayerMP playermp = UserIdent.getPlayerByMatchOrUsername(sender, sender.getCommandSenderName());
        if (args.length != 3)
        {
            throw new TranslatedCommandException("/push <X> <Y> <Z>", new Object[0]);
        }
        else
        {
            int var3 = 0;
            int var4 = 0;
            int var5 = 0;
            World var7 = null;

            var3 = (int) this.func_82368_a(playermp, playermp.posX, args[0]);
            var4 = (int) this.func_82367_a(playermp, playermp.posY, args[1], 0, 0);
            var5 = (int) this.func_82368_a(playermp, playermp.posZ, args[2]);
            var7 = playermp.worldObj;

            if ((var7.getBlock(var3, var4, var5) == Blocks.air || !((var7.getBlock(var3, var4, var5)) instanceof BlockButton))
                    && !(((var7.getBlock(var3, var4, var5)) instanceof BlockLever)))
            {
                throw new TranslatedCommandException("Button/Lever Not Found");
            }
            else
            {
                var7.getBlock(var3, var4, var5).onBlockActivated(var7, var3, var4, var5, (EntityPlayer) null, 0, 0.0F, 0.0F, 0.0F);
                ChatOutputHandler.chatConfirmation(sender, "Button/Lever Pushed");
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
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

}
