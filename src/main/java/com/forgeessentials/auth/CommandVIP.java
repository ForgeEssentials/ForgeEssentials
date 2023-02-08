package com.forgeessentials.auth;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandVIP extends ForgeEssentialsCommandBuilder
{

    public CommandVIP(String name, int permissionLevel, boolean enabled) {
		super(enabled);
	}

	@Override
    public String getPrimaryAlias()
    {
        return "vip";
    }

    public LiteralArgumentBuilder<CommandSource> setExecution()
	{
    	return builder
    	        .then(Commands.literal("add")
    	                .then(Commands.argument("player", EntityArgument.player())
    	                        .executes(CommandContext -> execute(CommandContext, "add")
    	                                )
    	                        )
    	                )
    	        .then(Commands.literal("add")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "remove")
                                        )
                                )
                        );
	}

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        EntityArgument.getPlayer(ctx, "player").getName().getString();
        PlayerEntity arg = null;
        try
        {
            arg = EntityArgument.getPlayer(ctx,"player");
        }
        catch (CommandSyntaxException e)
        {
            e.printStackTrace();
        }
        if (params.toString() == "add")
        {
            APIRegistry.perms.setPlayerPermission(UserIdent.get(arg), "fe.auth.vip", true);
        }
        else if (params.toString() == "remove")
        {
            APIRegistry.perms.setPlayerPermission(UserIdent.get(arg), "fe.auth.vip", false);
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.auth.vipcmd";
    }


    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

}
