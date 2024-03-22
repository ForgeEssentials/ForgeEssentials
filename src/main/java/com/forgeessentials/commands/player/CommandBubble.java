package com.forgeessentials.commands.player;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandBubble extends ForgeEssentialsCommandBuilder
{

    public CommandBubble(boolean enabled)
    {
        super(enabled);
    }

    public static String BUBBLE_GROUP = "command_bubble";

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "bubble";
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
    public void registerExtraPermissions()
    {
    	APIRegistry.perms.getServerZone().setGroupPermissionProperty(BUBBLE_GROUP, FEPermissions.GROUP_PRIORITY, "45");
    	APIRegistry.perms.getServerZone().setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_USE + Zone.ALL_PERMS, false);
        APIRegistry.perms.getServerZone().setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, false);
        APIRegistry.perms.getServerZone().setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, false);
        APIRegistry.perms.getServerZone().setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, false);
        APIRegistry.perms.getServerZone().setGroupPermission(BUBBLE_GROUP, ModuleProtection.PERM_INTERACT_ENTITY + Zone.ALL_PERMS, false);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
        		.then(Commands.literal("on").
        				executes(CommandContext -> execute(CommandContext, "on")))
                .then(Commands.literal("off")
                		.executes(CommandContext -> execute(CommandContext, "off")))
                .executes(CommandContext -> execute(CommandContext, "toggle"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        boolean toggleOn = false;
        if (params.equals("toggle")) {
            toggleOn = !APIRegistry.perms.getServerZone().getIncludedGroups(Zone.GROUP_DEFAULT).contains(BUBBLE_GROUP);
        }
        else if(params.equals("on")) {
        	toggleOn = true;
        }
        else {
        	toggleOn = false;
        }
        if (toggleOn)
        {
            APIRegistry.perms.getServerZone().groupIncludeAdd(Zone.GROUP_DEFAULT, BUBBLE_GROUP);
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "Activated bubble. Players are now unable to interact with the world.");
        }
        else
        {
            APIRegistry.perms.getServerZone().groupIncludeRemove(Zone.GROUP_DEFAULT, BUBBLE_GROUP);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Deactivated bubble");
        }
        return Command.SINGLE_SUCCESS;
    }
}