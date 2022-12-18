package com.forgeessentials.economy.commands;

import java.util.Arrays;

import com.forgeessentials.core.commands.BaseCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.misc.TranslatedCommandException.InvalidSyntaxException;
import com.forgeessentials.core.misc.TranslatedCommandException.PlayerNotFoundException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandPaidCommand extends BaseCommand
{
    public CommandPaidCommand(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "paidcommand";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "pc", "pcmd" };
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".paidcommand";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                .then(Commands.argument("command", MessageArgument.message())
                                        .executes(CommandContext -> execute(CommandContext)
                                                )
                                        )
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        UserIdent ident = UserIdent.get(EntityArgument.getPlayer(ctx, "player"));
        if (!ident.hasPlayer())
            throw new PlayerNotFoundException("commands.generic.player.notFound");

        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        Wallet wallet = APIRegistry.economy.getWallet(ident);
        if (!wallet.withdraw(amount))
        {
            ChatOutputHandler.chatError(ident.getPlayerMP(), Translator.translate("You can't afford that"));
            return Command.SINGLE_SUCCESS;
        }

        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(new DoAsCommandSender(ModuleEconomy.ECONOMY_IDENT, ident.getPlayerMP()), MessageArgument.getMessage(ctx, "command").getString());

        ChatOutputHandler.chatConfirmation(ident.getPlayerMP(), Translator.format("That cost you %s", APIRegistry.economy.toString(amount)));
        ModuleEconomy.confirmNewWalletAmount(ident, wallet);
    }
}
