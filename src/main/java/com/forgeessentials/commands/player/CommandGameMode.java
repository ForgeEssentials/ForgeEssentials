package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandGameMode extends BaseCommand
{
    public CommandGameMode(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "gamemode";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "gm" };
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        GameType gm;
        switch (args.length)
        {
        case 0:
            setGameMode(sender);
            break;
        case 1:
            gm = getGameTypeFromString(args[0]);
            if (gm != null)
            {
                setGameMode(sender, sender, gm);
            }
            else
            {
                setGameMode(sender, args[0]);
            }
            break;
        default:
            gm = getGameTypeFromString(args[0]);
            if (gm != null)
            {
                for (int i = 1; i < args.length; i++)
                {
                    setGameMode(sender, args[i], gm);
                }
            }
            else
                throw new CommandException("commands.gamemode.usage");
        }
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        GameType gm;
        switch (args.length)
        {
        case 0:
            throw new CommandException("commands.gamemode.usage");
        case 1:
            gm = getGameTypeFromString(args[0]);
            if (gm != null)
            {
                throw new CommandException("commands.gamemode.usage");
            }
            else
            {
                setGameMode(sender, args[0]);
            }
            break;
        default:
            gm = getGameTypeFromString(args[0]);
            if (gm != null)
            {
                for (int i = 1; i < args.length; i++)
                {
                    setGameMode(sender, args[i], gm);
                }
            }
            else
            {
                throw new CommandException("commands.gamemode.usage");
            }
            break;
        }
    }

    public void setGameMode(PlayerEntity sender)
    {
        setGameMode(sender.createCommandSourceStack(), sender, sender.isCreative() ? GameType.SURVIVAL : GameType.CREATIVE);
    }

    public void setGameMode(CommandSource sender, String target)
    {
        PlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, target);
        if (player == null)
        {
            ChatOutputHandler.chatError(sender, Translator.format("Unable to find player: %1$s.", target));
            return;
        }
        setGameMode(sender, target, player.isCreative() ? GameType.SURVIVAL : GameType.CREATIVE);
    }

    public void setGameMode(CommandSource sender, String target, GameType mode)
    {
        PlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, target);
        if (player == null)
        {
            ChatOutputHandler.chatError(sender, Translator.format("Unable to find player: %1$s.", target));
            return;
        }
        setGameMode(sender, player, mode);
    }

    public void setGameMode(CommandSource sender, PlayerEntity target, GameType mode)
    {
        target.setGameMode(mode);
        target.fallDistance = 0.0F;
        String modeName = I18n.get("gameMode." + mode.getName());
        ChatOutputHandler.chatNotification(sender, Translator.format("%1$s's gamemode was changed to %2$s.", target.getName(), modeName));
    }

    private GameType getGameTypeFromString(String string)
    {
        if (StringUtils.isNumeric(string))
        {
            return GameType.byId(Integer.parseInt(string));
        }
        else
        {
            return GameType.byName(string, GameType.SURVIVAL);
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, new String[] { "survival", "creative", "adventure", "spectator" });
        }
        else
        {
            return matchToPlayers(args);
        }

    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP, "Change others' game modes");
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + "." + getName();
    }

}
