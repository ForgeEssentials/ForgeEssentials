package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandGameMode extends ForgeEssentialsCommandBase
{
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
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
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
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
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

    public void setGameMode(EntityPlayer sender)
    {
        setGameMode(sender, sender, sender.capabilities.isCreativeMode ? GameType.SURVIVAL : GameType.CREATIVE);
    }

    public void setGameMode(ICommandSender sender, String target)
    {
        EntityPlayer player = UserIdent.getPlayerByMatchOrUsername(sender, target);
        if (player == null)
        {
            ChatOutputHandler.chatError(sender, Translator.format("Unable to find player: %1$s.", target));
            return;
        }
        setGameMode(sender, target, player.capabilities.isCreativeMode ? GameType.SURVIVAL : GameType.CREATIVE);
    }

    public void setGameMode(ICommandSender sender, String target, GameType mode)
    {
        EntityPlayer player = UserIdent.getPlayerByMatchOrUsername(sender, target);
        if (player == null)
        {
            ChatOutputHandler.chatError(sender, Translator.format("Unable to find player: %1$s.", target));
            return;
        }
        setGameMode(sender, player, mode);
    }

    public void setGameMode(ICommandSender sender, EntityPlayer target, GameType mode)
    {
        target.setGameType(mode);
        target.fallDistance = 0.0F;
        String modeName = I18n.translateToLocal("gameMode." + mode.getName());
        ChatOutputHandler.chatNotification(sender, Translator.format("%1$s's gamemode was changed to %2$s.", target.getName(), modeName));
    }

    private GameType getGameTypeFromString(String string)
    {
        if(StringUtils.isNumeric(string))
        {
            return GameType.getByID(Integer.parseInt(string));
        }
        else
        {
            return GameType.parseGameTypeWithDefault(string, GameType.SURVIVAL);
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
    public String getUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/gamemode [gamemode] [player(s)] Change a player's gamemode.";
        }
        else
        {
            return "/gamemode [gamemode] <player(s)> Change a player's gamemode.";
        }
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + "." + getName();
    }
}
