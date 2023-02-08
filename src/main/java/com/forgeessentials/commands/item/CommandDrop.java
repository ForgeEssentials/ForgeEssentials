package com.forgeessentials.commands.item;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandDrop extends BaseCommand
{

    public CommandDrop(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "drop";
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

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("pos", Vec3Argument.vec3())
                        .then(Commands.argument("count", IntegerArgumentType.integer(0, 64))
                                .then(Commands.argument("item", ItemArgument.item())
                                        .executes(CommandContext -> execute(CommandContext))
                                        )
                                )
                        );
    }

    @SuppressWarnings("deprecation")
    public void processCommand(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        Vector3d vector = Vec3Argument.getVec3(ctx, "pos");
        ICommandSource source = CommandUtils.GetSource(ctx.getSource());
        World world = null;
        int x = (int) vector.x;
        int y = (int) vector.y;
        int z = (int) vector.z;

        if (source instanceof DedicatedServer)
        {
            world = ((DedicatedServer) source).getLevel(World.OVERWORLD);
        }
        else if (source instanceof ServerPlayerEntity)
        {
            world = ((Entity) ctx.getSource().getEntity()).level;
        }
        else if (source instanceof TileEntity)
        {
            world = ((TileEntity) source).getLevel();
        }
        BlockPos pos = new BlockPos(x, y, z);

        int count = IntegerArgumentType.getInteger(ctx, "count");
        int j = Math.min(ItemArgument.getItem(ctx, "item").getItem().getMaxStackSize(), count);
        ItemStack itemstack = ItemArgument.getItem(ctx, "item").createItemStack(j, false);
        ItemStack tmpStack;

        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ChestTileEntity)
        {
            ChestTileEntity var10 = (ChestTileEntity) tileEntity;

            for (int slot = 0; slot < var10.getContainerSize(); ++slot)
            {
                if (var10.getItem(slot) == ItemStack.EMPTY)
                {
                    var10.setItem(slot, itemstack);
                    break;
                }

                if (var10.getItem(slot).getDisplayName().equals(itemstack.getDisplayName()) && var10.getItem(slot).getDamageValue() == itemstack.getDamageValue())
                {
                    if (var10.getItem(slot).getMaxStackSize() - var10.getItem(slot).getCount() >= count)
                    {
                        tmpStack = var10.getItem(slot);
                        tmpStack.setCount(tmpStack.getCount() + count);
                        break;
                    }

                    count -= var10.getItem(slot).getMaxStackSize() - var10.getItem(slot).getCount();
                    var10.getItem(slot).setCount(var10.getItem(slot).getMaxStackSize());
                }
            }
        }
        else if (tileEntity instanceof DropperTileEntity)
        {
            DropperTileEntity var13 = (DropperTileEntity) tileEntity;

            for (int slot = 0; slot < var13.getContainerSize(); ++slot)
            {
                if (var13.getItem(slot) == ItemStack.EMPTY)
                {
                    var13.setItem(slot, itemstack);
                    break;
                }

                if (var13.getItem(slot).getDisplayName().equals(itemstack.getDisplayName()) && var13.getItem(slot).getDamageValue() == itemstack.getDamageValue())
                {
                    if (var13.getItem(slot).getMaxStackSize() - var13.getItem(slot).getCount() >= count)
                    {
                        tmpStack = var13.getItem(slot);
                        tmpStack.setCount(tmpStack.getCount() + count);
                        break;
                    }

                    count -= var13.getItem(slot).getMaxStackSize() - var13.getItem(slot).getCount();
                    var13.getItem(slot).setCount(var13.getItem(slot).getMaxStackSize());
                }
            }
        }
        else if (tileEntity instanceof DispenserTileEntity)
        {
            DispenserTileEntity var14 = (DispenserTileEntity) tileEntity;

            for (int slot = 0; slot < var14.getContainerSize(); ++slot)
            {
                if (var14.getItem(slot) == ItemStack.EMPTY)
                {
                    var14.setItem(slot, itemstack);
                    break;
                }

                if (var14.getItem(slot).getDisplayName().equals(itemstack.getDisplayName()) && var14.getItem(slot).getDamageValue() == itemstack.getDamageValue())
                {
                    if (var14.getItem(slot).getMaxStackSize() - var14.getItem(slot).getCount() >= count)
                    {
                        tmpStack = var14.getItem(slot);
                        tmpStack.setCount(tmpStack.getCount() + count);
                        break;
                    }

                    count -= var14.getItem(slot).getMaxStackSize() - var14.getItem(slot).getCount();
                    var14.getItem(slot).setCount(var14.getItem(slot).getMaxStackSize());
                }
            }
        }
        else if (tileEntity instanceof HopperTileEntity)
        {
            HopperTileEntity var12 = (HopperTileEntity) tileEntity;

            for (int slot = 0; slot < var12.getContainerSize(); ++slot)
            {
                if (var12.getItem(slot) == ItemStack.EMPTY)
                {
                    var12.setItem(slot, itemstack);
                    count = 0;
                    break;
                }

                if (var12.getItem(slot).getDisplayName().equals(itemstack.getDisplayName()) && var12.getItem(slot).getDamageValue() == itemstack.getDamageValue())
                {
                    if (var12.getItem(slot).getMaxStackSize() - var12.getItem(slot).getCount() >= count)
                    {
                        tmpStack = var12.getItem(slot);
                        tmpStack.setCount(tmpStack.getCount() + count);
                        count = 0;
                        break;
                    }

                    count -= var12.getItem(slot).getMaxStackSize() - var12.getItem(slot).getCount();
                    var12.getItem(slot).setCount(var12.getItem(slot).getMaxStackSize());
                }
            }
        }
        else
        {
            throw new TranslatedCommandException("No viable container found to put item in.");
        }
        if (count > 0)
        {
            throw new TranslatedCommandException("Not enough room for items.");
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Items dropped into container.");
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        processCommand(ctx, params);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        processCommand(ctx, params);
        return Command.SINGLE_SUCCESS;
    }
}