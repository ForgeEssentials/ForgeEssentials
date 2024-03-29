package com.forgeessentials.commands.item;

import java.util.List;
import java.util.Objects;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.PlayerUtil;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

/**
 * Opens a configurable virtual chest
 */
public class CommandVirtualchest extends ForgeEssentialsCommandBuilder
{
    public CommandVirtualchest(boolean enabled)
    {
        super(enabled);
    }

    public static final String VIRTUALCHEST_TAG = "VirtualChestItems";

    public static final List<ContainerType<ChestContainer>> chestTypes = ImmutableList.of(ContainerType.GENERIC_9x1,
            ContainerType.GENERIC_9x2, ContainerType.GENERIC_9x3, ContainerType.GENERIC_9x4, ContainerType.GENERIC_9x5,
            ContainerType.GENERIC_9x6);

    public static int size = 54;
    public static int rowCount = 6;
    public static String name = "Vault 13";

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "virtualchest";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "vchest" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "me"))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity playerServer;
        if (params.equals("me"))
        {
            playerServer = getServerPlayer(ctx.getSource());
        }
        else
        {
            playerServer = EntityArgument.getPlayer(ctx, "player");
        }
        if (playerServer.containerMenu != playerServer.inventoryMenu)
        {
            playerServer.doCloseContainer();
        }
        playerServer.nextContainerCounter();

        playerServer.openMenu(new SimpleNamedContainerProvider(
                (syncId, inv, player) -> new ChestContainer(
                        CommandVirtualchest.chestTypes.get(CommandVirtualchest.rowCount - 1), syncId, inv,
                        Objects.requireNonNull(getVirtualChest(1, playerServer)), CommandVirtualchest.rowCount),
                new StringTextComponent(CommandVirtualchest.name)));
        return Command.SINGLE_SUCCESS;
    }

    public static Inventory getVirtualChest(int id, PlayerEntity player)
    {
        // TODO add multiple virtualChests
        int maxNumberVC = 1;
        // if (id > maxNumberVC) return null;
        int rows = CommandVirtualchest.rowCount;
        ListNBT virtualchests = PlayerUtil.getPersistedTag(player, false).getList(VIRTUALCHEST_TAG, 9);
        if (virtualchests.size() < maxNumberVC)
            for (int i = 0; i < maxNumberVC - virtualchests.size() + 1; i++)
                virtualchests.add(new ListNBT());
        Inventory inv = new Inventory(rows * 9);
        ListNBT virtualchest = virtualchests.getList(id - 1);
        for (int i = 0; i < virtualchest.size(); i++)
            inv.setItem(i, ItemStack.of(virtualchest.getCompound(i)));
        inv.addListener(inventory -> {
            virtualchests.remove(id - 1);
            ListNBT stacks = new ListNBT();
            for (int i = 0; i < inv.getContainerSize(); i++)
                stacks.add(inv.getItem(i).save(new CompoundNBT()));
            virtualchests.add(id - 1, stacks);
            PlayerUtil.getPersistedTag(player, true).put(VIRTUALCHEST_TAG, virtualchests);
        });
        return inv;
    }
}
