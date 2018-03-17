package com.forgeessentials.commands.item;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandDrop extends ForgeEssentialsCommandBase
{

    @Override
    public String getName()
    {
        return "fedrop";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "drop" };
    }

    @Override
    public String getUsage(ICommandSender par1ICommandSender)
    {
        return "/drop <X> <Y> <Z> <ItemID> <Meta> <Qty>";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".drop";
    }

    @SuppressWarnings("deprecation")
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 6)
        {
            throw new TranslatedCommandException(getUsage(sender));
        }
        World world = null;
        int x = (int) this.func_82368_a(sender, 0.0D, args[0]);
        int y = (int) this.func_82367_a(sender, 0.0D, args[1], 0, 0);
        int z = (int) this.func_82368_a(sender, 0.0D, args[2]);

        if (sender instanceof DedicatedServer)
        {
            world = ((DedicatedServer) sender).getWorld(0);
        }
        else if (sender instanceof EntityPlayerMP)
        {
            world = ((Entity) sender).world;
            x = (int) this.func_82368_a(sender, ((Entity) sender).posX, args[0]);
            y = (int) this.func_82367_a(sender, ((Entity) sender).posY, args[1], 0, 0);
            z = (int) this.func_82368_a(sender, ((Entity) sender).posZ, args[2]);
        }
        else if (sender instanceof TileEntity)
        {
            world = ((TileEntity) sender).getWorld();
            x = (int) this.func_82368_a(sender, ((TileEntity) sender).getPos().getX(), args[0]);
            y = (int) this.func_82367_a(sender, ((TileEntity) sender).getPos().getY(), args[1], 0, 0);
            z = (int) this.func_82368_a(sender, ((TileEntity) sender).getPos().getZ(), args[2]);
        }
        BlockPos pos = new BlockPos(x, y, z);

        String var7 = args[3];
        Item item = CommandBase.getItemByText(sender, var7);
        int var8 = parseInt(args[4], 0, Integer.MAX_VALUE);
        int var9 = parseInt(args[5], 1, Item.REGISTRY.getObject(new ResourceLocation(var7)).getItemStackLimit());
        ItemStack tmpStack;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof IInventory)
        {
            IInventory var10 = (IInventory) tileEntity;

            for (int slot = 0; slot < var10.getSizeInventory(); ++slot)
            {
                final ItemStack itemStack = var10.getStackInSlot(slot);
                if (itemStack == ItemStack.EMPTY)
                {
                    var10.setInventorySlotContents(slot, new ItemStack(item, var9, var8));
                    break;
                }

                if (itemStack.getUnlocalizedName().equals(var7) && itemStack.getItemDamage() == var8)
                {
                    if (itemStack.getMaxStackSize() - itemStack.getCount() >= var9)
                    {
                        tmpStack = itemStack;
                        tmpStack.setCount(tmpStack.getCount() + var9);
                        break;
                    }

                    var9 -= itemStack.getMaxStackSize() - itemStack.getCount();
                    itemStack.setCount(itemStack.getMaxStackSize());
                }
            }
        }
        else
        {
            throw new TranslatedCommandException("No viable container found to put item in.");
        }
        if (var9 > 0)
        {
            throw new TranslatedCommandException("Not enough room for items.");
        }
        ChatOutputHandler.chatConfirmation(sender, "Items dropped into container.");
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

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        EntityPlayerMP playermp = UserIdent.getPlayerByMatchOrUsername(sender, sender.getName());
        processCommand(playermp, args);
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        processCommand(sender, args);
    }

}