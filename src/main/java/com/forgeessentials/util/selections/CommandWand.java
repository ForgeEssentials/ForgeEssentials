package com.forgeessentials.util.selections;

//Depreciated

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandWand extends ForgeEssentialsCommandBuilder
{

    public CommandWand(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "/fewand";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("unbind")
                        .executes(CommandContext -> execute(CommandContext, "unbind")
                                )
                        )
                .then(Commands.literal("rebind")
                        .executes(CommandContext -> execute(CommandContext, "rebind")
                                )
                        );
    }
    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (ModuleLauncher.getModuleList().contains("WEIntegrationTools"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), "WorldEdit is installed. Please use WorldEdit selections (//wand, //set, etc)");
            ChatOutputHandler.chatNotification(ctx.getSource(), "Please refer to http://wiki.sk89q.com/wiki/WorldEdit/Selection for more info.");
            return Command.SINGLE_SUCCESS;
        }

        // Get the wand item (or hands)
        Item wandItem;
        String wandId, wandName;
        int wandDmg = 0;
        PlayerEntity player = getServerPlayer(ctx.getSource());
        if (getServerPlayer(ctx.getSource()).getMainHandItem() != null)
        {
            wandName = player.getMainHandItem().getDisplayName().getString();
            wandItem = player.getMainHandItem().getItem();
            wandDmg = player.getMainHandItem().getDamageValue();
            wandId = wandItem.getRegistryName().getNamespace();
            if (wandDmg == -1)
            {
                wandDmg = 0;
            }
        }
        else
        {
            wandName = "your hands";
            wandId = "hands";
        }

        PlayerInfo info = PlayerInfo.get(player.getUUID());

        // Check for unbind
        if ((params.toString()=="unbind") && ((info.isWandEnabled() && info.getWandID().equals(wandId)) ))
        {
            ChatOutputHandler.sendMessage(ctx.getSource(), TextFormatting.LIGHT_PURPLE + "Wand unbound from " + wandName);
            info.setWandEnabled(false);
            return Command.SINGLE_SUCCESS;
        }else 
        {
            // Check for permissions
            if (!checkCommandPermission(ctx.getSource()))
                throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

            // Bind wand
            info.setWandEnabled(true);
            info.setWandID(wandId);
            info.setWandDmg(wandDmg);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Wand bound to " + wandName);
            return Command.SINGLE_SUCCESS;
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.core.pos.wand";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }
}
