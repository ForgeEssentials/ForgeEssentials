package com.forgeessentials.commands.item;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * Opens your enderchest.
 */
public class CommandEnderchest extends BaseCommand
{
    public CommandEnderchest(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "enderchest";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "echest" };
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
        if (player.containerMenu != player.inventoryMenu)
        {
            player.closeContainer();
        }
        player.nextContainerCounter();

        //chest.setChestTileEntity(null);
        player.getEnderChestInventory().startOpen(player);
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

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".enderchest";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .executes(CommandContext -> execute(CommandContext)
                        );
    }

}
