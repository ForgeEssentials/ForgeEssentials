package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

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
    public String getPrimaryAlias()
    {
        return "wallet";
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_OTHERS, DefaultPermissionLevel.OP, "Allows viewing other player's wallets");
        APIRegistry.perms.registerPermission(PERM_MODIFY, DefaultPermissionLevel.OP, "Allows modifying wallets");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(CommandContext -> execute(CommandContext, "add")
                                                )
                                        )
                                )
                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(CommandContext -> execute(CommandContext, "set")
                                                )
                                        )
                                )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(CommandContext -> execute(CommandContext, "remove")
                                                )
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "walletOther")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "wallet")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString().equals("wallet"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Your wallet contains %s", APIRegistry.economy.getWallet(getIdent(ctx.getSource())).toString()));
            return Command.SINGLE_SUCCESS;
        }

        UserIdent player = getIdent(EntityArgument.getPlayer(ctx, "player"));
        if (!player.equals(getIdent(ctx.getSource())))
            checkPermission(ctx.getSource(), PERM_OTHERS);

        Wallet wallet = APIRegistry.economy.getWallet(player);
        if (params.toString().equals("walletOther"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Wallet of %s contains %s", player.getUsernameOrUuid(), wallet.toString()));
            return Command.SINGLE_SUCCESS;
        }

        checkPermission(ctx.getSource(), PERM_MODIFY);

        Long amount = LongArgumentType.getLong(ctx, "amount");

        switch (params.toString())
        {
        case "set":
            wallet.set(amount);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set wallet of %s to %s", player.getUsernameOrUuid(), wallet.toString()));
            break;
        case "add":
            wallet.add(amount);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Added %s to %s's wallet. It now contains %s", //
                    APIRegistry.economy.toString(amount), player.getUsernameOrUuid(), wallet.toString()));
            break;
        case "remove":
            if (!wallet.withdraw(amount))
                throw new TranslatedCommandException("Player %s does not have enough %s in his wallet", //
                        player.getUsernameOrUuid(), APIRegistry.economy.currency(2));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Removed %s from %s's wallet. It now contains %s", //
                    APIRegistry.economy.toString(amount), player.getUsernameOrUuid(), wallet.toString()));
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params.toString());
        }
        return Command.SINGLE_SUCCESS;
    }
}
