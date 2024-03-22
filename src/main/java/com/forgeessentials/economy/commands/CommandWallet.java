package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandWallet extends ForgeEssentialsCommandBuilder
{

    public CommandWallet(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM = ModuleEconomy.PERM_COMMAND + ".wallet";
    public static final String PERM_OTHERS = PERM + ".others";
    public static final String PERM_MODIFY = PERM + ".modify";

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "wallet";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_OTHERS, DefaultPermissionLevel.OP,
                "Allows viewing other player's wallets");
        APIRegistry.perms.registerPermission(PERM_MODIFY, DefaultPermissionLevel.OP, "Allows modifying wallets");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(CommandContext -> execute(CommandContext, "add"))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(CommandContext -> execute(CommandContext, "set"))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(CommandContext -> execute(CommandContext, "remove"))))
                        .executes(CommandContext -> execute(CommandContext, "walletOther")))
                .executes(CommandContext -> execute(CommandContext, "wallet"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("wallet"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Your wallet contains %s",
                    APIRegistry.economy.getWallet(getIdent(ctx.getSource())).toString()));
            return Command.SINGLE_SUCCESS;
        }

        UserIdent player = getIdent(EntityArgument.getPlayer(ctx, "player"));
        if (!player.equals(getIdent(ctx.getSource())))
            if (!hasPermission(ctx.getSource(), PERM_OTHERS))
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
                return Command.SINGLE_SUCCESS;
            }

        Wallet wallet = APIRegistry.economy.getWallet(player);
        if (params.equals("walletOther"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Wallet of %s contains %s", player.getUsernameOrUuid(), wallet.toString()));
            return Command.SINGLE_SUCCESS;
        }
        if (!hasPermission(ctx.getSource(), PERM_MODIFY))
        {
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
            return Command.SINGLE_SUCCESS;
        }

        long amount = LongArgumentType.getLong(ctx, "amount");

        switch (params)
        {
        case "set":
            wallet.set(amount);
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Set wallet of %s to %s", player.getUsernameOrUuid(), wallet.toString()));
            break;
        case "add":
            wallet.add(amount);
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Added %s to %s's wallet. It now contains %s",
                            APIRegistry.economy.toString(amount), player.getUsernameOrUuid(), wallet.toString()));
            break;
        case "remove":
            if (!wallet.withdraw(amount))
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Player %s does not have enough %s in his wallet",
                        player.getUsernameOrUuid(), APIRegistry.economy.currency(2));
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Removed %s from %s's wallet. It now contains %s",
                            APIRegistry.economy.toString(amount), player.getUsernameOrUuid(), wallet.toString()));
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params);
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }
}
