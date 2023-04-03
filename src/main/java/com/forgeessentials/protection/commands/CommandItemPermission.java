package com.forgeessentials.protection.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandItemPermission extends ForgeEssentialsCommandBuilder
{

    public CommandItemPermission(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "itemperm";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.protection.cmd.itemperm";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }
    List<String> types = Arrays.asList("break", "place", "inventory", "exist");
    List<String> function = Arrays.asList("allow", "deny", "clear");

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        baseBuilder.executes(CommandContext -> execute(CommandContext, "blank")
                        );
                for(String type : types) {
                    baseBuilder
                    .then(Commands.literal(type)
                            .then(Commands.literal("allow")
                                    .then(Commands.literal("all")
                                            .executes(CommandContext -> execute(CommandContext, type+"-allow-all")
                                                    )
                                            )
                                    .then(Commands.literal("*")
                                            .executes(CommandContext -> execute(CommandContext, type+"-allow-all")
                                                    )
                                            )
                                    .executes(CommandContext -> execute(CommandContext, type+"-allow-noall")
                                            )
                                    )
                            .then(Commands.literal("deny")
                                    .then(Commands.literal("all")
                                            .executes(CommandContext -> execute(CommandContext, type+"-deny-all")
                                                    )
                                            )
                                    .then(Commands.literal("*")
                                            .executes(CommandContext -> execute(CommandContext, type+"-deny-all")
                                                    )
                                            )
                                    .executes(CommandContext -> execute(CommandContext, type+"-deny-noall")
                                            )
                                    )
                            .then(Commands.literal("clear")
                                    .then(Commands.literal("all")
                                            .executes(CommandContext -> execute(CommandContext, type+"-clear-all")
                                                    )
                                            )
                                    .then(Commands.literal("*")
                                            .executes(CommandContext -> execute(CommandContext, type+"-clear-all")
                                                    )
                                            )
                                    .executes(CommandContext -> execute(CommandContext, type+"-clear-noall")
                                            )
                                    )
                            );
                 };
                 return baseBuilder;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ItemStack stack = getServerPlayer(ctx.getSource()).getMainHandItem();

        if (params.toString().equals("blank"))
        {
            if (stack == ItemStack.EMPTY)
                throw new TranslatedCommandException("No item equipped in main hand!");
            ChatOutputHandler.chatNotification(ctx.getSource(), ModuleProtection.getItemPermission(stack));
            return Command.SINGLE_SUCCESS;
        }

        String[] para = params.toString().split("-");
        if (!types.contains(para[1]))
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, para[1]);

        Boolean value;
        switch (para[1])
        {
        case "allow":
            value = true;
            break;
        case "deny":
            value = false;
            break;
        case "clear":
            value = null;
            break;
        default:
            throw new TranslatedCommandException("Need to specify allow, deny or clear");
        }

        if (stack == ItemStack.EMPTY)
            throw new TranslatedCommandException("No item equipped!");

        String permStart = ModuleProtection.BASE_PERM + '.';
        String permEnd;
        if (para[2]=="all")
        {
            permEnd = '.' + ModuleProtection.getItemPermission(stack) + ".*";
        }
        else
        {
            permEnd = '.' + ModuleProtection.getItemPermission(stack);
        }


        if (value == null)
            APIRegistry.perms.getServerZone().clearGroupPermission(Zone.GROUP_DEFAULT, permStart + para[0] + permEnd);
        else
            APIRegistry.perms.getServerZone().setGroupPermission(Zone.GROUP_DEFAULT, permStart + para[0] + permEnd, value);
        ChatOutputHandler.chatConfirmation(ctx.getSource(),value == null ? "Cleared [%s] for item %s" : 
                (value ? "Allowed [%s] for item %s" : "Denied [%s] for item %s"), para[0], ModuleProtection.getItemPermission(stack));
        return Command.SINGLE_SUCCESS;
    }
}
