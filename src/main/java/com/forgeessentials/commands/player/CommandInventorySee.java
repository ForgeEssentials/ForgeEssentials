package com.forgeessentials.commands.player;

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

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.SeenPlayerInventory;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

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
    public String getPrimaryAlias()
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".invsee";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext)
                                )
                        );
    
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ServerPlayerEntity source = getServerPlayer(ctx.getSource());

        if (!FMLEnvironment.dist.isDedicatedServer())
        {
            return Command.SINGLE_SUCCESS;
        }
        ServerPlayerEntity victim = EntityArgument.getPlayer(ctx, "player");
        if (victim.hasDisconnected())
            throw new TranslatedCommandException("Player %s not found.", victim.getDisplayName());

        if (source.containerMenu != source.inventoryMenu)
        {
            source.closeContainer();
        }
        source.nextContainerCounter();
        
        source.openMenu(new INamedContainerProvider() {

            @Override
            public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity victim)
            {
                return new ChestContainer(ContainerType.GENERIC_9x5, id, playerInventory, new SeenPlayerInventory(victim), 5);
            }

            @Override
            public ITextComponent getDisplayName()
            {
                return new StringTextComponent(victim.getName() + "'s inventory");
            }
            });
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Does the other player deserve this?");
        return Command.SINGLE_SUCCESS;
    }
}
