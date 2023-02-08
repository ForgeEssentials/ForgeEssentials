package com.forgeessentials.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandNickname extends ForgeEssentialsCommandBuilder
{

    public CommandNickname(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM = ModuleChat.PERM + ".nickname";

    public static final String PERM_OTHERS = PERM + ".others";

    @Override
    public String getPrimaryAlias()
    {
        return "nickname";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "nick" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_OTHERS, DefaultPermissionLevel.OP, "Edit other players' nicknames");
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("clearSelf")
                        .executes(CommandContext -> execute(CommandContext, "delS")
                                )
                        )
                .then(Commands.literal("clearPlayer")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "delO")
                                        )
                                )
                        )
                .then(Commands.literal("setSelf")
                        .then(Commands.argument("name", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "setS")
                                        )
                                )
                        )
                .then(Commands.literal("setPlayer")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("name", MessageArgument.message())
                                        .executes(CommandContext -> execute(CommandContext, "setO")
                                                )
                                        )
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "delS")
        {
            ModuleChat.setPlayerNickname((PlayerEntity) ctx.getSource().getEntity(), null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Nickname removed.");
            return Command.SINGLE_SUCCESS;
        }
        if(params.toString() == "setS")
        {
            ITextComponent name = MessageArgument.getMessage(ctx, "name");
            ModuleChat.setPlayerNickname((PlayerEntity) ctx.getSource().getEntity(), name.getString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Nickname set to " + name.getString());
            return Command.SINGLE_SUCCESS;
        }

        if (!PermissionAPI.hasPermission((PlayerEntity) ctx.getSource().getEntity(), PERM_OTHERS))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (params.toString() == "delO")
        {
            ModuleChat.setPlayerNickname(player, null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Removed nickname of %s", player));
            return Command.SINGLE_SUCCESS;
        }
        if(params.toString() == "setO")
        {
            ITextComponent name = MessageArgument.getMessage(ctx, "name");
            ModuleChat.setPlayerNickname(player, name.getString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set nickname of %s to %s", player, name.getString()));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "delS" || params.toString() == "setS")
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Console can only modify player nicknames!"));
            return Command.SINGLE_SUCCESS;
        }

        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (params.toString() == "delO")
        {
            ModuleChat.setPlayerNickname(player, null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Removed nickname of %s", player));
            return Command.SINGLE_SUCCESS;
        }
        if(params.toString() == "setO")
        {
            ITextComponent name = MessageArgument.getMessage(ctx, "name");
            ModuleChat.setPlayerNickname(player, name.getString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set nickname of %s to %s", player, name.getString()));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }
}
