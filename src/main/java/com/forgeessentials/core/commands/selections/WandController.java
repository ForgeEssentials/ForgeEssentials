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
        if (EnvironmentChecker.worldEditFEtoolsInstalled)
        {
            return;
        }

        // only server events please.
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }

        // get info now rather than later
        EntityPlayer player = event.entityPlayer;
        PlayerInfo info = PlayerInfo.getPlayerInfo(player.getPersistentID());

        if (player.getCurrentEquippedItem() == null)
        {
        	return;
        }
        Item id = player.getCurrentEquippedItem().getItem();
        int damage = 0;
        if (id.getUnlocalizedName() != Blocks.air.getUnlocalizedName() && player.getCurrentEquippedItem().getHasSubtypes())
        {
            damage = player.getCurrentEquippedItem().getItemDamage();
        }

        if (id.getUnlocalizedName() != info.wandID || !info.wandEnabled || damage != info.wandDmg)
        {
            return; // wand does not activate
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
