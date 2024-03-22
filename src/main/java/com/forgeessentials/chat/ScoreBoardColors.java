package com.forgeessentials.chat;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.PermissionEvent.Group;
import com.forgeessentials.api.permissions.PermissionEvent.User;
import com.forgeessentials.util.output.LoggingHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ScoreBoardColors
{
    public static final String PERM_SCOREBOARD_COLOR = ModuleChat.PERM + ".scoreboardcolor";
    // From https://stackoverflow.com/a/13667522
    public static final Pattern HEX_PATTERN = Pattern.compile("\\p{XDigit}+");

    private int tickCount = 0;
    private final int TICK_REFRESH = 40;

    public ScoreBoardColors()
    {
        APIRegistry.FE_EVENTBUS.register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerPerms()
    {
        APIRegistry.perms.registerPermissionProperty(PERM_SCOREBOARD_COLOR, "f",
                "Format colors for tab menu/scoreboard. USE ONLY CHARACTERS AND NO &");
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent e)
    {
        if (tickCount % TICK_REFRESH == 0)
        {
            tickCount = 0;
            for (EntityPlayerMP playerList : FMLCommonHandler.instance().
                    getMinecraftServerInstance().
                    getPlayerList().getPlayers())
            {
                UserIdent userIdent = UserIdent.get(playerList);
                updatePlayerColor(userIdent);
            }
        }
        tickCount++;
    }

    public void updatePlayerColor(UserIdent userIdent)
    {
        if (APIRegistry.perms.getUserPermissionProperty(
                userIdent, PERM_SCOREBOARD_COLOR) != null)
        {
            // User has permissions set individually
            setPlayerColor(userIdent, APIRegistry.perms.getUserPermissionProperty(userIdent, PERM_SCOREBOARD_COLOR));
        } else if (APIRegistry.perms.getGroupPermissionProperty(
                APIRegistry.perms.getPrimaryGroup(userIdent), PERM_SCOREBOARD_COLOR) != null)
        {
            // User has permissions set as part of group
            setPlayerColor(userIdent, APIRegistry.perms.getUserPermissionProperty(userIdent, PERM_SCOREBOARD_COLOR));
        }

    }

    public void setPlayerColor(UserIdent userIdent, String colorHex)
    {
        if (HEX_PATTERN.matcher(colorHex).matches())
        {
            Scoreboard scoreboard = DimensionManager.getWorld(DimensionType.OVERWORLD.getId()).getScoreboard();
            // Team names are same as color codes, to make things easier to deal with
            if (scoreboard.getTeam(colorHex) == null)
            {
                // Creates team and sets formatting code as prefix
                scoreboard.createTeam(colorHex).setPrefix("\u00A7" + colorHex);
            }
            // If statement in case teams get added instead of set
            if (scoreboard.getPlayersTeam(userIdent.getUsername()) == null ||
                    !Objects.equals(scoreboard.getPlayersTeam(userIdent.getUsername()).getName(), colorHex))
            {
                scoreboard.addPlayerToTeam(userIdent.getUsername(), colorHex);
            }
        }
    }
}
