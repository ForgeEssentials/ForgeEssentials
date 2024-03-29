package com.forgeessentials.commands.item;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

/**
 * Opens your enderchest.
 */
public class CommandEnderchest extends ForgeEssentialsCommandBuilder
{
    public CommandEnderchest(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "enderchest";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "echest" };
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
        if (player.containerMenu != player.inventoryMenu)
        {
            player.closeContainer();
        }
        player.nextContainerCounter();

        // player.getEnderChestInventory().startOpen(player);
        player.openMenu(new SimpleNamedContainerProvider(
                (i, inv, p) -> ChestContainer.threeRows(i, inv, player.getEnderChestInventory()),
                new TranslationTextComponent("container.enderchest")));
        return Command.SINGLE_SUCCESS;
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

}
