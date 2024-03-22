package com.forgeessentials.commands.player;

import com.forgeessentials.commands.util.SeeablePlayerInventory;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

/**
 * Opens other player inventory.
 */
public class CommandInventorySee extends ForgeEssentialsCommandBuilder
{

    public CommandInventorySee(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "invsee";
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
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .executes(CommandContext -> execute(CommandContext, "blank")));

    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer source = getServerPlayer(ctx.getSource());

        if (!FMLEnvironment.dist.isDedicatedServer())
        {
            return Command.SINGLE_SUCCESS;
        }
        ServerPlayer victim = EntityArgument.getPlayer(ctx, "player");
        if (victim.hasDisconnected())
        {
            ChatOutputHandler.chatError(ctx.getSource(),
                    Translator.format("Player %s not found.", victim.getDisplayName().getString()));
            return Command.SINGLE_SUCCESS;
        }
        if(victim == source) {
        	ChatOutputHandler.chatNotification(ctx.getSource(), "Pressing E is just one key, "
        			+ "Why go through all the trouble of using this command?");
            return Command.SINGLE_SUCCESS;
        }
        if (source.containerMenu != source.inventoryMenu)
        {
            source.closeContainer();
        }
        source.nextContainerCounter();

        source.openMenu(new MenuProvider() {

            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player)
            {
            	return new ChestMenu(MenuType.GENERIC_9x5, id, playerInventory,
                        new SeeablePlayerInventory(victim), 5);
            }

            @Override
            public @NotNull Component getDisplayName()
            {
                return new TextComponent(victim.getDisplayName().getString() + "'s inventory");
            }
        });
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Does the other player deserve this?");
        return Command.SINGLE_SUCCESS;
    }
}
