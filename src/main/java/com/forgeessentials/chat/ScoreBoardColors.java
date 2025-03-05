package com.forgeessentials.chat;

import java.util.Objects;
import java.util.regex.Pattern;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.TabListNameFormat;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;

public class ScoreBoardColors
{

    public static final String PERM_SCOREBOARD_COLOR = ModuleChat.PERM + ".scoreboardcolor";
    // From https://stackoverflow.com/a/13667522
    public static final Pattern HEX_PATTERN = Pattern.compile("\\p{XDigit}{6}");
    private final String DEFAULT_COLOR = "FFFFFF";

    public ScoreBoardColors()
    {
        APIRegistry.FE_EVENTBUS.register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerPerms()
    {
        APIRegistry.perms.registerPermissionProperty(PERM_SCOREBOARD_COLOR, DEFAULT_COLOR,
                "Format colors for tab menu/scoreboard. This is an RGB hexidecimal value");
    }

    @SubscribeEvent()
    public void updatePlayerColor(TabListNameFormat e)
    {
        UserIdent userIdent = UserIdent.get(e.getEntity());
        String userColor = APIRegistry.perms.getUserPermissionProperty(userIdent, PERM_SCOREBOARD_COLOR);
        String groupColor = APIRegistry.perms.getGroupPermissionProperty(
                APIRegistry.perms.getPrimaryGroup(userIdent), PERM_SCOREBOARD_COLOR);
        if (!Objects.equals(userColor, DEFAULT_COLOR) &&
                userColor != null &&
                HEX_PATTERN.matcher(userColor).matches())
        {
            // User has permissions set individually
            e.setDisplayName(
                    new TextComponent(e.getEntity().getName().getString()).
                            withStyle(Style.EMPTY.withColor(TextColor.fromRgb(Integer.parseInt(userColor, 16))))
            );
        }
        else if (!Objects.equals(groupColor, DEFAULT_COLOR) &&
                groupColor != null && // Why is this null sometimes?
                HEX_PATTERN.matcher(groupColor).matches())
        {
            // User has permissions set as part of group
            e.setDisplayName(
                    new TextComponent(e.getEntity().getName().getString()).
                            withStyle(Style.EMPTY.withColor(TextColor.fromRgb(Integer.parseInt(groupColor, 16))))
            );
        }
        else
        {
            // User has default permissions
            e.setDisplayName(
                    new TextComponent(e.getEntity().getName().getString()).
                            withStyle(Style.EMPTY.withColor(TextColor.fromRgb(Integer.parseInt("FFFFFF", 16))))
            );

        }
    }
}