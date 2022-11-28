package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandHeal extends BaseCommand
{

    @Override
    public String getPrimaryAlias()
    {
        return "heal";
    }

    public String getUsage(ServerPlayerEntity sender)
    {
        if (sender instanceof PlayerEntity)
        {
            return "/heal <player> Heal yourself or other players (if you have permission).";
        }
        else
        {
            return "/heal <player> Heal a player.";
        }
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".heal";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP, "Heal others");
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            heal(sender);
        }
        else if (args.length == 1 && PermissionAPI.hasPermission(sender, getPermissionNode() + ".others"))
        {
            ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                heal(player);
            }
            else
            {
                ChatOutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            throw new TranslatedCommandException(getUsage(sender));
        }
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 1)
        {
            ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                heal(player);
            }
            else
            {
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            }
        }
        else
        {
            throw new TranslatedCommandException(getUsage(sender));
        }

    }

    public void heal(PlayerEntity target)
    {
        float toHealBy = target.getMaxHealth() - target.getHealth();
        target.heal(toHealBy);
        target.clearFire();;
        target.getFoodData().eat(20, 1.0F);
        ChatOutputHandler.chatConfirmation(target, "You were healed.");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return matchToPlayers(args);
        }
        else
        {
            return null;
        }
    }

}
