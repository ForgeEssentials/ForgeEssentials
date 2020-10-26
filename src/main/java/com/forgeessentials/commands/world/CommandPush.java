package com.forgeessentials.commands.world;

import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandPush extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "push";
    }

    @Override
    public String getUsage(ICommandSender par1ICommandSender)
    {
        return "/push <X> <Y> <Z>: Push a button or pressureplate somewhere";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".push";
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 3)
        {
            throw new TranslatedCommandException(getUsage(sender));
        }
        else
        {
            int x = 0;
            int y = 0;
            int z = 0;
            World world = null;

            if (sender instanceof TileEntity)
            {
                x = (int) this.func_82368_a(sender, ((TileEntity) sender).getPos().getX(), args[0]);
                y = (int) this.func_82367_a(sender, ((TileEntity) sender).getPos().getY(), args[1], 0, 0);
                z = (int) this.func_82368_a(sender, ((TileEntity) sender).getPos().getZ(), args[2]);
                world = ((TileEntity) sender).getWorld();
            }
            else if (sender instanceof EntityPlayerMP)
            {
                x = (int) this.func_82368_a(sender, ((EntityPlayerMP) sender).posX, args[0]);
                y = (int) this.func_82367_a(sender, ((EntityPlayerMP) sender).posY, args[1], 0, 0);
                z = (int) this.func_82368_a(sender, ((EntityPlayerMP) sender).posZ, args[2]);
                world = ((EntityPlayerMP) sender).world;
            }
            else if (sender instanceof DedicatedServer)
            {
                x = (int) this.func_82368_a(sender, 0.0D, args[0]);
                y = (int) this.func_82367_a(sender, 0.0D, args[1], 0, 0);
                z = (int) this.func_82368_a(sender, 0.0D, args[2]);
                world = ((DedicatedServer) sender).getWorld(0);
            }
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = world.getBlockState(pos);
            
            if ((state == Blocks.AIR.getDefaultState() || !(state.getBlock() instanceof BlockButton))
                    && !(state.getBlock() instanceof BlockLever))
            {
                throw new TranslatedCommandException("Button/Lever Not Found");
            }
            else
            {
                state.getBlock().onBlockActivated(world, pos, state, (EntityPlayer) null, EnumHand.MAIN_HAND, null, EnumFacing.DOWN.getIndex(), 0.0F, 0.0F);
                ChatOutputHandler.chatConfirmation(sender, "Button/Lever Pushed");
            }
        }
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        EntityPlayerMP playermp = UserIdent.getPlayerByMatchOrUsername(sender, sender.getName());
        if (args.length != 3)
        {
            throw new TranslatedCommandException("/push <X> <Y> <Z>", new Object[0]);
        }
        else
        {
            int x = 0;
            int y = 0;
            int z = 0;
            World world = null;

            x = (int) this.func_82368_a(playermp, playermp.posX, args[0]);
            y = (int) this.func_82367_a(playermp, playermp.posY, args[1], 0, 0);
            z = (int) this.func_82368_a(playermp, playermp.posZ, args[2]);
            world = playermp.world;
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = world.getBlockState(pos);
            
            if ((state == Blocks.AIR.getDefaultState() || !(state.getBlock() instanceof BlockButton))
                    && !(state.getBlock() instanceof BlockLever))
            {
                throw new TranslatedCommandException("Button/Lever Not Found");
            }
            else
            {
                state.getBlock().onBlockActivated(world, pos, state, (EntityPlayer) null, EnumHand.MAIN_HAND, null, EnumFacing.DOWN.getIndex(), 0.0F, 0.0F);
                ChatOutputHandler.chatConfirmation(sender, "Button/Lever Pushed");
            }
        }
    }

    private double func_82368_a(ICommandSender par1ICommandSender, double par2, String par4Str) throws CommandException
    {
        return this.func_82367_a(par1ICommandSender, par2, par4Str, -30000000, 30000000);
    }

    private double func_82367_a(ICommandSender par1ICommandSender, double par2, String par4Str, int par5, int par6) throws CommandException
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

            d1 += parseDouble(par4Str);

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

}
