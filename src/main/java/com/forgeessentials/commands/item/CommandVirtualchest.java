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

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
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

    public static final List<MenuType<ChestMenu>> chestTypes = ImmutableList.of(MenuType.GENERIC_9x1,
            MenuType.GENERIC_9x2, MenuType.GENERIC_9x3, MenuType.GENERIC_9x4, MenuType.GENERIC_9x5,
            MenuType.GENERIC_9x6);

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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "me"))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer playerServer;
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

        playerServer.openMenu(new SimpleMenuProvider(
                (syncId, inv, player) -> new ChestMenu(
                        CommandVirtualchest.chestTypes.get(CommandVirtualchest.rowCount - 1), syncId, inv,
                        Objects.requireNonNull(getVirtualChest(1, playerServer)), CommandVirtualchest.rowCount),
                new TextComponent(CommandVirtualchest.name)));
        return Command.SINGLE_SUCCESS;
    }

    public static SimpleContainer getVirtualChest(int id, Player player)
    {
        // TODO add multiple virtualChests
        int maxNumberVC = 1;
        // if (id > maxNumberVC) return null;
        int rows = CommandVirtualchest.rowCount;
        ListTag virtualchests = PlayerUtil.getPersistedTag(player, false).getList(VIRTUALCHEST_TAG, 9);
        if (virtualchests.size() < maxNumberVC)
            for (int i = 0; i < maxNumberVC - virtualchests.size() + 1; i++)
                virtualchests.add(new ListTag());
        SimpleContainer inv = new SimpleContainer(rows * 9);
        ListTag virtualchest = virtualchests.getList(id - 1);
        for (int i = 0; i < virtualchest.size(); i++)
            inv.setItem(i, ItemStack.of(virtualchest.getCompound(i)));
        inv.addListener(inventory -> {
            virtualchests.remove(id - 1);
            ListTag stacks = new ListTag();
            for (int i = 0; i < inv.getContainerSize(); i++)
                stacks.add(inv.getItem(i).save(new CompoundTag()));
            virtualchests.add(id - 1, stacks);
            PlayerUtil.getPersistedTag(player, true).put(VIRTUALCHEST_TAG, virtualchests);
        });
        return inv;
    }
}
