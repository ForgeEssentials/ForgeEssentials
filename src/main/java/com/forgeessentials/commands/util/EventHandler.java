package com.forgeessentials.commands.util;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.google.common.base.Strings;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class EventHandler {
    @ForgeSubscribe
    public void playerInteractEvent(PlayerInteractEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }

		/*
         * Colorize!
		 */
        if (e.entityPlayer.getEntityData().getBoolean("colorize"))
        {
            e.setCanceled(true);
            TileEntity te = e.entityPlayer.worldObj.getBlockTileEntity(e.x, e.y, e.z);
            if (te != null)
            {
                if (te instanceof TileEntitySign)
                {
                    String[] signText = ((TileEntitySign) te).signText;

                    signText[0] = FunctionHelper.formatColors(signText[0]);
                    signText[1] = FunctionHelper.formatColors(signText[1]);
                    signText[2] = FunctionHelper.formatColors(signText[2]);
                    signText[3] = FunctionHelper.formatColors(signText[3]);

                    ((TileEntitySign) te).signText = signText;
                    e.entityPlayer.worldObj.setBlockTileEntity(e.x, e.y, e.z, te);
                    e.entityPlayer.worldObj.markBlockForUpdate(e.x, e.y, e.z);
                }
                else
                {
                    ChatUtils.sendMessage(e.entityPlayer, "That is no sign!");
                }
            }
            else
            {
                ChatUtils.sendMessage(e.entityPlayer, "That is no sign!");
            }

            e.entityPlayer.getEntityData().setBoolean("colorize", false);
        }

		/*
         * Jump with compass
		 */
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK)
        {
            if (e.entityPlayer.getCurrentEquippedItem() != null && FMLCommonHandler.instance().getEffectiveSide().isServer())
            {
                if (e.entityPlayer.getCurrentEquippedItem().itemID == Item.compass.itemID)
                {
                    if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(e.entityPlayer, "fe.commands.jump")))
                    {
                        try
                        {
                            MovingObjectPosition mo = FunctionHelper.getPlayerLookingSpot(e.entityPlayer, false);

                            ((EntityPlayerMP) e.entityPlayer).playerNetServerHandler
                                    .setPlayerLocation(mo.blockX, mo.blockY, mo.blockZ, e.entityPlayer.rotationPitch, e.entityPlayer.rotationYaw);
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                }
            }
        }

		/*
		 * 
		 */
        if (e.entityPlayer.getCurrentEquippedItem() != null && FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            ItemStack is = e.entityPlayer.inventory.getCurrentItem();
            if (is != null && is.getTagCompound() != null && is.getTagCompound().hasKey("FEbinding"))
            {
                String cmd = null;
                NBTTagCompound nbt = is.getTagCompound().getCompoundTag("FEbinding");

                if (e.action.equals(Action.LEFT_CLICK_BLOCK))
                {
                    cmd = nbt.getString("left");
                }
                else if (e.action.equals(Action.RIGHT_CLICK_AIR))
                {
                    cmd = nbt.getString("right");
                }

                if (!Strings.isNullOrEmpty(cmd))
                {
                    MinecraftServer.getServer().getCommandManager().executeCommand(e.entityPlayer, cmd);
                    e.setCanceled(true);
                }
            }
        }
    }
}
