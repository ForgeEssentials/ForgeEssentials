package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPaidCommand extends ForgeEssentialsCommandBuilder
{
    public CommandPaidCommand(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "paidcommand";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "pc", "pcmd" };
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.NONE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    /*
     * Expected structure: "/paidcommand <player> <amount> <command...>"
     */

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .then(Commands.argument("command", StringArgumentType.greedyString())
                                .executes(CommandContext -> execute(CommandContext, "blank")))));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        UserIdent ident = UserIdent.get(EntityArgument.getPlayer(ctx, "player"));
        if (!ident.hasPlayer())
        {
            ChatOutputHandler.chatError(ctx.getSource(),
                    Translator.format("Player %s is currently offline", ident.getUsername()));
            return Command.SINGLE_SUCCESS;
        }

        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        Wallet wallet = APIRegistry.economy.getWallet(ident);
        if (!wallet.withdraw(amount))
        {
            ChatOutputHandler.chatError(ident.getPlayerMP(), Translator.translate("You can't afford that"));
            return Command.SINGLE_SUCCESS;
        }

        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(
                new DoAsCommandSender(ModuleEconomy.ECONOMY_IDENT, ident.getPlayerMP().createCommandSourceStack())
                        .createCommandSourceStack(),
                StringArgumentType.getString(ctx, "command"));

        ChatOutputHandler.chatConfirmation(ident.getPlayerMP(),
                Translator.format("That cost you %s", APIRegistry.economy.toString(amount)));
        ModuleEconomy.confirmNewWalletAmount(ident, wallet);
        return Command.SINGLE_SUCCESS;
    }
}
