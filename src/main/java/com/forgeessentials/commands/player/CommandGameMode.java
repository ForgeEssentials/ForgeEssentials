package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;



public class CommandGameMode extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "gamemode";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "gm" };
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        WorldSettings.GameType gm;
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
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        WorldSettings.GameType gm;
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
        setGameMode(sender, sender, sender.capabilities.isCreativeMode ? WorldSettings.GameType.SURVIVAL : WorldSettings.GameType.CREATIVE);
    }

    public void setGameMode(ICommandSender sender, String target)
    {
        EntityPlayer player = UserIdent.getPlayerByMatchOrUsername(sender, target);
        if (player == null)
        {
            ChatOutputHandler.chatError(sender, Translator.format("Unable to find player: %1$s.", target));
            return;
        }
        setGameMode(sender, target, player.capabilities.isCreativeMode ? WorldSettings.GameType.SURVIVAL : WorldSettings.GameType.CREATIVE);
    }

    public void setGameMode(ICommandSender sender, String target, WorldSettings.GameType mode)
    {
        EntityPlayer player = UserIdent.getPlayerByMatchOrUsername(sender, target);
        if (player == null)
        {
            ChatOutputHandler.chatError(sender, Translator.format("Unable to find player: %1$s.", target));
            return;
        }
        setGameMode(sender, player, mode);
    }

    public void setGameMode(ICommandSender sender, EntityPlayer target, WorldSettings.GameType mode)
    {
        target.setGameType(mode);
        target.fallDistance = 0.0F;
        String modeName = StatCollector.translateToLocal("gameMode." + mode.getName());
        ChatOutputHandler.chatNotification(sender, Translator.format("%1$s's gamemode was changed to %2$s.", target.getName(), modeName));
    }

    private WorldSettings.GameType getGameTypeFromString(String string)
    {
        int id;

        try
        {
            id = Integer.parseInt(string);
        }
        catch(NumberFormatException e)
        {
            id = -1;
        }

        if (string.equalsIgnoreCase(WorldSettings.GameType.SURVIVAL.getName()) || string.equalsIgnoreCase("s") || id == 0)
        {
            return WorldSettings.GameType.SURVIVAL;
        }
        else if (string.equalsIgnoreCase(WorldSettings.GameType.CREATIVE.getName()) || string.equalsIgnoreCase("c") || id == 1)
        {
            return WorldSettings.GameType.CREATIVE;
        }
        else if (string.equalsIgnoreCase(WorldSettings.GameType.ADVENTURE.getName()) || string.equalsIgnoreCase("a") || id == 2)
        {
            return WorldSettings.GameType.ADVENTURE;
        }
        else
        {
            WorldSettings.GameType[] allGameTypes = WorldSettings.GameType.values();

            for (WorldSettings.GameType gameType : allGameTypes)
            {
                if (string.equalsIgnoreCase(gameType.getName()) || id == gameType.getID())
                    return gameType;
            }

            return null;
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            WorldSettings.GameType[] allGameTypes = WorldSettings.GameType.values();
            String[] names = new String[allGameTypes.length];

            for (int i = 0; i < allGameTypes.length; i++)
            {
                names[i] = allGameTypes[i].getName().toLowerCase();
            }

            return getListOfStringsMatchingLastWord(args, names);
        }
        else
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }

    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", PermissionLevel.OP);
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
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
        return ModuleCommands.PERM + "." + getCommandName();
    }
}
