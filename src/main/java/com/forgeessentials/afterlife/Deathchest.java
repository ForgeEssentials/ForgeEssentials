package com.forgeessentials.afterlife;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class Deathchest extends ServerEventHandler {
    /**
     * This permissions is needed to get the skull, Default = members.
     */
    public static final String PERMISSION_MAKE = ModuleAfterlife.BASEPERM + ".deathchest.make";

    /**
     * This is the permissions that allows you to bypass the protection timer.
     */
    public static final String PERMISSION_BYPASS = ModuleAfterlife.BASEPERM + ".deathchest.protectionBypass";

    public static final String PERMPROP_XP_MODIFIER = ModuleAfterlife.BASEPERM + ".deathchest.xpmultiplier";

    public static boolean enable;
    public static boolean enableXP;
    public static boolean enableFencePost;
    public static int protectionTime;

    public Map<String, Grave> gravemap = new HashMap<String, Grave>();

    public Deathchest()
    {
        super();
        TileEntity.addMapping(FEskullTe.class, "FESkull");
    }

    public void load()
    {
        gravemap = DataManager.getInstance().loadAll(Grave.class);
    }

    public void save()
    {
        for (Grave grave : gravemap.values())
        {
            grave.setSaveProtTime();
            DataManager.getInstance().save(grave, grave.key);
        }
    }

    @SubscribeEvent
    public void handleDeath(PlayerDropsEvent e)
    {
        if (!enable)
        {
            return;
        }
        if (!PermissionsManager.checkPermission(e.entityPlayer, PERMISSION_MAKE))
        {
            return;
        }
        WorldPoint point = new WorldPoint(e.entityPlayer);
        if (point.getY() < 0)
        {
            OutputHandler.chatWarning(e.entityPlayer, "No deathchest for you as you fell out of the world!");
            return;
        }
        World world = e.entityPlayer.worldObj;

        // get height for grave
        point.setY(FunctionHelper.placeInWorld(world, point.getX(), point.getY(), point.getZ(), enableFencePost ? 2 : 1));
        e.setCanceled(true);

        if (enableFencePost)
        {
            world.setBlock(point.getX(), point.getY(), point.getZ(), Blocks.fence);
            point.setY(point.getY() + 1);
        }

        gravemap.put(point.toString(), new Grave(point, e.entityPlayer, e.drops, this));
        FEskullTe.createPlayerSkull(e.entityPlayer, world, point.getX(), point.getY(), point.getZ());
    }

    @SubscribeEvent
    public void handleClick(PlayerInteractEvent e)
    {
        if (e.entity.worldObj.isRemote)
        {
            return;
        }

        if (e.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
        {
            WorldPoint point = new WorldPoint(e.entity.worldObj, e.x, e.y, e.z);
            if (gravemap.containsKey(point.toString()))
            {
                Grave grave = gravemap.get(point.toString());
                Block block = e.entity.worldObj.getBlock(e.x, e.y, e.z);
                if (block == Blocks.skull || block == Blocks.chest || block == Blocks.fence)
                {
                    if (!grave.canOpen(e.entityPlayer))
                    {
                        OutputHandler.chatWarning(e.entityPlayer, "This grave is still under divine protection.");
                        e.setCanceled(true);
                    }
                    else
                    {
                        EntityPlayerMP player = (EntityPlayerMP) e.entityPlayer;
                        if (grave.xp > 0)
                        {
                            String modifier = APIRegistry.perms.getPermissionProperty(player, PERMPROP_XP_MODIFIER);
                            double intmod = Double.parseDouble(modifier);
                            int toAdd = (int)(grave.xp * intmod);
                            player.addExperienceLevel(toAdd);
                            grave.xp = 0;
                        }

                        if (player.openContainer != player.inventoryContainer)
                        {
                            player.closeScreen();
                        }
                        player.getNextWindowId();
                        grave.setOpen(true);

                        InventoryGrave invGrave = new InventoryGrave(grave);
                        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, invGrave.getInventoryName(), invGrave
                                .getSizeInventory(), true));
                        player.openContainer = new ContainerChest(player.inventory, invGrave);
                        player.openContainer.windowId = player.currentWindowId;
                        player.openContainer.addCraftingToCrafters(player);
                        e.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void mineGrave(BreakEvent e)
    {
        WorldPoint point = new WorldPoint(e.world, e.x, e.y, e.z); // the grave, or fencepost if fence is enabled
        WorldPoint point2 = new WorldPoint(e.world, e.x, e.y + 1, e.z); // the grave, if fencepost is enabled
        if (e.world.isRemote)
        {
            return;
        }

        if (enableFencePost)
        {
            if (gravemap.containsKey(point2.toString()))
            {
                e.setCanceled(true);
                if (e.world.getBlock(e.x, e.y, e.z) == Blocks.fence)
                {
                    OutputHandler.chatError(e.getPlayer(), "You may not defile the grave of a player.");
                }
                else
                {
                    Grave grave = gravemap.get(point2.toString());
                    removeGrave(grave, true);
                }
            }
            else if (gravemap.containsKey(point.toString()))
            {
                e.setCanceled(true);
                Grave grave = gravemap.get(point.toString());
                removeGrave(grave, true);
            }
        }

        else
        {
            if (gravemap.containsKey(point.toString()))
            {
                e.setCanceled(true);
                Grave grave = gravemap.get(point.toString());
                removeGrave(grave, true);
            }
        }
    }

    public void removeGrave(Grave grave, boolean mined)
    {
        if (grave == null)
        {
            return;
        }
        DataManager.getInstance().delete(Grave.class, grave.point.toString());
        gravemap.remove(grave.point.toString());
        if (mined)
        {
            for (ItemStack is : grave.inv)
            {
                try
                {
                    EntityItem entity = new EntityItem(DimensionManager.getWorld(grave.point.getDimension()), grave.point.getX(), grave.point.getY(),
                            grave.point.getZ(), is);
                    DimensionManager.getWorld(grave.point.getDimension()).spawnEntityInWorld(entity);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        World world = DimensionManager.getWorld(grave.point.getDimension());
        world.setBlock(grave.point.getX(), grave.point.getY(), grave.point.getZ(), Blocks.air);
        if (enableFencePost && world.getBlock(grave.point.getX(), grave.point.getY() - 1, grave.point.getZ()) == Blocks.fence)
            world.setBlock(grave.point.getX(), grave.point.getY() - 1, grave.point.getZ(), Blocks.air);
    }

    @SubscribeEvent
    public void tickGraves(TickEvent.ServerTickEvent e)
    {
        for (Grave grave : gravemap.values())
        {
            grave.tick();
        }
    }
}
