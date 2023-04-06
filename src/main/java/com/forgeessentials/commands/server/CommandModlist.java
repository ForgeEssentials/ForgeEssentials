package com.forgeessentials.commands.server;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.List;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

public class CommandModlist extends ForgeEssentialsCommandBuilder
{

    public CommandModlist(boolean enabled)
    {
		super(enabled);
	}

	@Override
    public String getPrimaryAlias()
    {
        return "modlist";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".modlist";
    }
    
    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
	{
    	return baseBuilder
    			.then(Commands.argument("page", IntegerArgumentType.integer(1, 40))
                        .executes(CommandContext -> execute(CommandContext, "select"))
                     )
    			.executes(CommandContext -> execute(CommandContext, "one"));
	}
    
    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandException
    {
        int num = 0;
        if(params.equals("select")) {num = IntegerArgumentType.getInteger(ctx,"page");}
        int size = ModList.get().size();
        int perPage = 7;
        int pages = (int) Math.ceil(size / (float) perPage);
        int page =  num == 0 ? 0 : (num>pages ? pages : num)-1;
        int min = Math.min(page * perPage, size);

        ChatOutputHandler.chatNotification(ctx.getSource(), String.format("--- Showing modlist page %1$d of %2$d ---", page + 1, pages));
        List<ModInfo> mods = ModList.get().getMods();
        for (int i = page * perPage; i < min + perPage; i++)
        {
            if (i >= size)
            {
               break;
            }
            ModInfo mod = mods.get(i);
            ChatOutputHandler.chatNotification(ctx.getSource(), mod.getDisplayName() + " - " + mod.getVersion());
        }
        return Command.SINGLE_SUCCESS;
    }

}
