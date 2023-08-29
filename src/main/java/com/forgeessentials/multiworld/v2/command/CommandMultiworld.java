package com.forgeessentials.multiworld.v2.command;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.multiworld.v2.ModuleMultiworldV2;
import com.forgeessentials.multiworld.v2.Multiworld;
import com.forgeessentials.multiworld.v2.MultiworldException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandMultiworld extends ForgeEssentialsCommandBuilder
{

    public CommandMultiworld(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "mw";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "placeholder"));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
    	 Multiworld world = new Multiworld("tesdt", "gg1", "gg2", 33445, "");
         try
         {
             ModuleMultiworldV2.getMultiworldManager().addWorld(world);
         }
         catch (MultiworldException e)
         {
             throw new CommandException( new StringTextComponent(e.type.error));
         }
         for (Multiworld world1 : ModuleMultiworldV2.getMultiworldManager().getWorlds())
         {
        	 ChatOutputHandler.chatConfirmation(ctx.getSource(), "###################WORLDS#######################");
        	 ChatOutputHandler.chatConfirmation(ctx.getSource(), "#" + world1.getInternalID() + " " + world1.getName() + ": " + world1.getProvider());
         }
        return Command.SINGLE_SUCCESS;
    }
}
