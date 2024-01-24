package com.forgeessentials.commands.player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.preloader.api.EntityTrackerHelper;
import com.forgeessentials.util.CommandParserArgs;

public class CommandVanish extends ParserCommandBase
{

    public static final String PERM = "fe.commands.vanish";

    public static final String PERM_OTHERS = PERM + ".others";

    private static Set<UserIdent> vanishedPlayers = new HashSet<>();

    @Override
    public String getCommandName()
    {
        return "fevanish";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "vanish" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/vanish: Become invisible";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_OTHERS, PermissionLevel.OP, "Allow to vanish other players");
    }

    @Override
    public void parse(CommandParserArgs arguments)
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
        WorldServer world = (WorldServer) player.worldObj;
        @SuppressWarnings("unchecked")
        List<EntityPlayerMP> players = world.playerEntities;
        if (vanish)
        {
            vanishedPlayers.add(ident);
            EntityTrackerEntry tracker = ((EntityTrackerHelper) world.getEntityTracker()).getEntityTrackerEntry(player);

            Set<EntityPlayerMP> tracked = new HashSet<EntityPlayerMP>(tracker.trackingPlayers);
            world.getEntityTracker().removePlayerFromTrackers(player);
            tracked.forEach(otherPlayer -> {
                player.playerNetServerHandler.sendPacket(new S0CPacketSpawnPlayer(otherPlayer));
            });
        }
        else
        {
            vanishedPlayers.remove(ident);
            EntityTrackerEntry tracker = ((EntityTrackerHelper) world.getEntityTracker()).getEntityTrackerEntry(player);
            for (EntityPlayerMP otherPlayer : players)
                if (otherPlayer != player)
                {
                    tracker.trackingPlayers.remove(otherPlayer);
                    tracker.tryStartWachingThis(otherPlayer);
                }
        }
    }

}
