package com.forgeessentials.commands.util;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.CommandVanish;
import com.forgeessentials.core.misc.LoginMessage;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.PlayerInfo;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    public static final String BYPASS_KIT_COOLDOWN = "fe.TickHandlerCommands.BypassKitCooldown";
    public static List<AFKdata> afkList = new ArrayList<AFKdata>();
    public static List<AFKdata> afkListToAdd = new ArrayList<AFKdata>();
    public static List<AFKdata> afkListToRemove = new ArrayList<AFKdata>();
    public static HashMultimap<EntityPlayer, PlayerInvChest> map = HashMultimap.create();

    public static int getWorldHour(World world)
    {
        return (int) ((world.getWorldTime() % 24000) / 1000);
    }

    public static int getWorldDays(World world)
    {
        return (int) (world.getWorldTime() / 24000);
    }

    public static void makeWorldTimeHours(World world, int target)
    {
        world.setWorldTime((getWorldDays(world) + 1) * 24000 + (target * 1000));
    }

    public static void register(PlayerInvChest inv)
    {
        map.put(inv.owner, inv);
    }

    public static void remove(PlayerInvChest inv)
    {
        map.remove(inv.owner, inv);
    }

    @SubscribeEvent
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
                if (e.entityPlayer.getCurrentEquippedItem().getItem() == Items.compass)
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

    @SubscribeEvent
    public void doWorldTick(TickEvent.WorldTickEvent e)
    {
        /*
         * Time settings
	     */
        if (!CommandDataManager.WTmap.containsKey(e.world.provider.dimensionId))
        {
            WeatherTimeData wt = new WeatherTimeData(e.world.provider.dimensionId);
            wt.freezeTime = e.world.getWorldTime();
            CommandDataManager.WTmap.put(e.world.provider.dimensionId, wt);
        }
        else
        {
            WeatherTimeData wt = CommandDataManager.WTmap.get(e.world.provider.dimensionId);
	        /*
	         * Weather part
	         */
            if (wt.weatherSpecified)
            {
                WorldInfo winfo = e.world.getWorldInfo();
                if (!wt.rain)
                {
                    winfo.setRainTime(20 * 300);
                    winfo.setRaining(false);
                    winfo.setThunderTime(20 * 300);
                    winfo.setThundering(false);
                }
                else if (!wt.storm)
                {
                    winfo.setThunderTime(20 * 300);
                    winfo.setThundering(false);
                }
            }

	        /*
	         * Time part
	         */
            if (wt.timeFreeze)
            {
                e.world.setWorldTime(wt.freezeTime);
            }
            else if (wt.timeSpecified)
            {
                int h = getWorldHour(e.world);

                if (wt.day)
                {
                    if (h >= WeatherTimeData.dayTimeEnd)
                    {
                        makeWorldTimeHours(e.world, WeatherTimeData.dayTimeStart);
                    }
                }
                else
                {
                    if (h >= WeatherTimeData.nightTimeEnd)
                    {
                        makeWorldTimeHours(e.world, WeatherTimeData.nightTimeStart);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void doServerTick(TickEvent.ServerTickEvent e)
    {
	    /*
         * Kit system
         */
        for (Object player : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
        {
            PlayerInfo.getPlayerInfo(((EntityPlayer) player).username).KitCooldownTick();
        }

        /*
         * AFK system
         */
        try
        {
            afkList.addAll(afkListToAdd);
            afkListToAdd.clear();
            for (AFKdata data : afkList)
            {
                data.count();
            }
            afkList.removeAll(afkListToRemove);
            afkListToRemove.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(EntityPlayer player)
    {
        if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean(CommandVanish.TAGNAME))
        {
            CommandVanish.vanishedPlayers.add(player.getPersistentID());
        }
        LoginMessage.sendLoginMessage(player);
    }

    @SubscribeEvent
    public void onPlayerLogout(EntityPlayer player)
    {
        CommandVanish.vanishedPlayers.remove(player.getPersistentID());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(EntityPlayer player)
    {
        if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean(CommandVanish.TAGNAME))
        {
            CommandVanish.vanishedPlayers.add(player.getPersistentID());
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(EntityPlayer player)
    {

        if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean(CommandVanish.TAGNAME))
        {
            CommandVanish.vanishedPlayers.add(player.getPersistentID());
        }
    }

    @SubscribeEvent
    public void tickStart(TickEvent.PlayerTickEvent event)
    {
        if (map.containsKey(event.player))
        {
            for (PlayerInvChest inv : map.get(event.player))
            {
                inv.update();
            }
        }
    }

}
