package com.forgeessentials.commands.player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;

public class CommandVanish extends ParserCommandBase
{

    public static final String PERM = "fe.commands.vanish";

    public static final String PERM_OTHERS = PERM + ".others";

    private static Set<UserIdent> vanishedPlayers = new HashSet<>();

    @Override
    public String getPrimaryAlias()
    {
        return "vanish";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/vanish: Become invisible";
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
        return PERM;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_OTHERS, DefaultPermissionLevel.OP, "Allow to vanish other players");
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        UserIdent player;
        if (arguments.isEmpty())
        {
            if (arguments.ident == null)
            {
                return;
            }
            player = arguments.ident;
        }
        else
        {
            if (!arguments.hasPermission(PERM_OTHERS))
                throw new TranslatedCommandException("You don't have permission to vanish other players");
            player = arguments.parsePlayer(true, true);
        }
        if (arguments.isTabCompletion)
            return;

        vanishToggle(player);
        if (isVanished(player))
            arguments.confirm("You are vanished now");
        else
            arguments.confirm("You are visible now");
    }

    public static void vanishToggle(UserIdent ident)
    {
        vanish(ident, !isVanished(ident));
    }

    public static boolean isVanished(UserIdent ident)
    {
        return vanishedPlayers.contains(ident);
    }

    public static void vanish(UserIdent ident, boolean vanish)
    {
        EntityPlayerMP player = ident.getPlayerMP();
        WorldServer world = (WorldServer) player.world;
        List<EntityPlayer> players = world.playerEntities;
        if (vanish)
        {
            vanishedPlayers.add(ident);
            EntityTrackerEntry tracker = world.getEntityTracker().trackedEntityHashTable.lookup(player.getEntityId());

            Set<EntityPlayerMP> tracked = new HashSet<>(tracker.trackingPlayers);
            world.getEntityTracker().untrack(player);
            tracked.forEach(tP -> {
                player.connection.sendPacket(new SPacketSpawnPlayer(tP));
            });
        }
        else
        {
            vanishedPlayers.remove(ident);
            world.getEntityTracker().track(player);
        }
    }

}
