package com.forgeessentials.chat;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.PermissionEvent.User.ModifyPermission;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.regex.Pattern;


public class ScoreBoardColors {
    public static final String PERM_SCOREBOARD_COLOR = "fe.chat.scoreboardcolor";
    // From https://stackoverflow.com/a/13667522
    public static final Pattern HEX_PATTERN = Pattern.compile("\\p{XDigit}+");
    public void serverStart() {
        APIRegistry.perms.registerPermissionProperty(PERM_SCOREBOARD_COLOR, "", "Format colors for tab menu/scoreboard. USE ONLY CHARACTERS AND NO &");
        APIRegistry.FE_EVENTBUS.register(this);
    }
    @SubscribeEvent()
    public void colorChange(ModifyPermission e)
    {
        if (e.permissionNode == PERM_SCOREBOARD_COLOR && HEX_PATTERN.matcher(e.value).matches()) {
            Scoreboard scoreboard = DimensionManager.getWorld(DimensionType.OVERWORLD.getId()).getScoreboard();
            // Team names are same as color codes, to make things easier to deal with
            if (scoreboard.getTeam(e.value) == null)
            {
                scoreboard.createTeam(e.value).setPrefix(e.value);
            }
            scoreboard.addPlayerToTeam(e.ident.getPlayer().getName(), e.value);
        }
    }
}
