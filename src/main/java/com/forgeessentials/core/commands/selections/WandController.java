package com.forgeessentials.core.commands.selections;

//Depreciated

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQueryPlayerArea;
import com.forgeessentials.core.compat.EnvironmentChecker;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class WandController {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        // if worldedit is installed, don't do anything
        // and only handle server events
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() || EnvironmentChecker.worldEditFEtoolsInstalled)
            return;

        // get info now rather than later
        EntityPlayer player = event.entityPlayer;
        PlayerInfo info = PlayerInfo.getPlayerInfo(player.getPersistentID());
        
        if (!info.wandEnabled)
        	return;
        
        // Check if wand should activate
        if (player.getCurrentEquippedItem() == null) {
        	if (info.wandID != null)
        		return;
        } else {
        	if (!(player.getCurrentEquippedItem().getItem().getUnlocalizedName().equals(info.wandID)))
        		return;
        	if (player.getCurrentEquippedItem().getItemDamage() != info.wandDmg)
        		return;
        }

        Point point = new Point(event.x, event.y, event.z);
        if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayerArea(player, "ForgeEssentials.CoreCommands.select.pos", point)))
        {
            OutputHandler.chatError(player,
                    "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
            return;
        }

        // left Click
        if (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
        {
            info.setPoint1(point);
            IChatComponent format = ChatUtils.createFromText("Pos1 set to " + event.x + ", " + event.y + ", " + event.z);
            player.addChatMessage(ChatUtils.colourize(format, EnumChatFormatting.DARK_PURPLE));
            event.setCanceled(true);
        }
        // right Click
        else if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
        {
            info.setPoint2(point);
            IChatComponent format = ChatUtils.createFromText("Pos2 set to " + event.x + ", " + event.y + ", " + event.z);
            player.addChatMessage(ChatUtils.colourize(format, EnumChatFormatting.DARK_PURPLE));
            event.setCanceled(true);
        }
    }
}
