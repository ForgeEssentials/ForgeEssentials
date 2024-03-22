package com.forgeessentials.commands.item;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandDrop extends ForgeEssentialsCommandBuilder
{

    public CommandDrop(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("pos", Vec3Argument.vec3())
                .then(Commands.argument("count", IntegerArgumentType.integer(0, 64))
                        .then(Commands.argument("item", ItemArgument.item())
                                .executes(CommandContext -> execute(CommandContext, "blank")))));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        Vec3 vector = Vec3Argument.getVec3(ctx, "pos");
        Level world = null;
        int x = (int) vector.x;
        int y = (int) vector.y;
        int z = (int) vector.z;

        if (ctx.getSource().getEntity() instanceof ServerPlayer)
        {
            world = ctx.getSource().getEntity().level;
        }
        else
        {
            CommandSource source = CommandUtils.GetSource(ctx.getSource());
            if (source instanceof MinecraftServer)
            {
                world = ((DedicatedServer) source).getLevel(Level.OVERWORLD);
            }
        }
        BlockPos pos = new BlockPos(x, y, z);

        int count = IntegerArgumentType.getInteger(ctx, "count");
        int j = Math.min(new ItemStack(ItemArgument.getItem(ctx, "item").getItem(), count).getMaxStackSize(), count);
        ItemStack itemstack = ItemArgument.getItem(ctx, "item").createItemStack(j, false);
        ItemStack tmpStack;

        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof Container)
        {
            Container inventory = (Container) tileEntity;
            for (int slot = 0; slot < inventory.getContainerSize(); ++slot)
            {
                itemstack.setCount(count);
                if (inventory.getItem(slot) == ItemStack.EMPTY)
                {
                    inventory.setItem(slot, itemstack);
                    count = 0;
                    break;
                }

                if (inventory.getItem(slot).sameItemStackIgnoreDurability(itemstack))
                {
                    if (inventory.getItem(slot).getMaxStackSize() - inventory.getItem(slot).getCount() >= count)
                    {
                        tmpStack = inventory.getItem(slot);
                        tmpStack.setCount(tmpStack.getCount() + count);
                        count = 0;
                        break;
                    }

                    count -= (inventory.getItem(slot).getMaxStackSize() - inventory.getItem(slot).getCount());
                    inventory.getItem(slot).setCount(inventory.getItem(slot).getMaxStackSize());
                }
            }
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No viable container found to put item in.");
            return Command.SINGLE_SUCCESS;
        }
        if (count > 0)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Not enough room for all the items.");
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Items dropped into container.");
        return Command.SINGLE_SUCCESS;

    }

}