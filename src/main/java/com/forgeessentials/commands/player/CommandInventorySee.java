package com.forgeessentials.commands.player;

import com.forgeessentials.commands.util.SeeablePlayerInventory;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .executes(CommandContext -> execute(CommandContext, "blank")));

    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity source = getServerPlayer(ctx.getSource());

        if (!FMLEnvironment.dist.isDedicatedServer())
        {
            return Command.SINGLE_SUCCESS;
        }
        ServerPlayerEntity victim = EntityArgument.getPlayer(ctx, "player");
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

        source.openMenu(new INamedContainerProvider() {

            @Override
            public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity player)
            {
            	return new ChestContainer(ContainerType.GENERIC_9x5, id, playerInventory,
                        new SeeablePlayerInventory(victim), 5);
            }

            @Override
            public @NotNull ITextComponent getDisplayName()
            {
                return new StringTextComponent(victim.getDisplayName().getString() + "'s inventory");
            }
        });
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Does the other player deserve this?");
        return Command.SINGLE_SUCCESS;
    }
}
