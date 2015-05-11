package com.forgeessentials.afterlife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.google.gson.annotations.Expose;

public class Grave
{

    public static Map<Point, Grave> graves = new HashMap<Point, Grave>();

    protected WorldPoint point;

    protected UUID owner;

    protected List<ItemStack> inventory = new ArrayList<ItemStack>();

    protected int xp;

    protected int protTime;

    protected boolean hasFencePost;

    protected boolean isProtected = true;

    @Expose(serialize = false)
    private boolean opened;

    @Expose(serialize = false)
    private long lastTick;

    public static Grave createGrave(EntityPlayer player, List<EntityItem> drops)
    {
        if (!PermissionsManager.checkPermission(player, ModuleAfterlife.PERM_DEATHCHEST))
            return null;

        if (player.posY < 0)
        {
            OutputHandler.chatWarning(player, "No deathchest for you as you fell out of the world!");
            return null;
        }

        int xp = 0;
        Double xpModifier = FunctionHelper.tryParseDouble(APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_XP));
        if (xpModifier != null)
        {
            xp = (int) (player.experienceLevel * xpModifier);
            player.experienceLevel = 0;
            player.experienceTotal = 0;
        }

        // Create no grave if no experience / items available
        if (xp <= 0 && drops.isEmpty())
            return null;

        Grave grave = new Grave(player, drops, xp);
        graves.put(grave.point, grave);
        return grave;
    }

    public Grave(EntityPlayer player, List<EntityItem> drops, int xp)
    {
        this.xp = xp;
        this.owner = player.getPersistentID();
        this.hasFencePost = PermissionsManager.checkPermission(player, ModuleAfterlife.PERM_DEATHCHEST_FENCE);
        this.lastTick = System.currentTimeMillis();
        this.protTime = FunctionHelper.parseIntDefault(APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_SAFETIME), 0);
        if (protTime <= 0)
            isProtected = false;
        for (int i = 0; i < drops.size(); i++)
            inventory.add(drops.get(i).getEntityItem().copy());

        point = new WorldPoint(player);
        point.setY(FunctionHelper.placeInWorld(player.worldObj, point.getX(), point.getY(), point.getZ(), hasFencePost ? 2 : 1));
        if (hasFencePost)
        {
            player.worldObj.setBlock(point.getX(), point.getY(), point.getZ(), Blocks.fence);
            point.setY(point.getY() + 1);
        }
        FEskullTe.createPlayerSkull(player.getGameProfile(), player.worldObj, point.getX(), point.getY(), point.getZ());
    }

    public void updateBlocks()
    {
        if (point.getWorld().getBlock(point.getX(), point.getY(), point.getZ()) != Blocks.skull)
            FEskullTe.createPlayerSkull(UserIdent.getGameProfileByUuid(owner), point.getWorld(), point.getX(), point.getY(), point.getZ());
        if (hasFencePost && point.getWorld().getBlock(point.getX(), point.getY() - 1, point.getZ()) != Blocks.fence)
            point.getWorld().setBlock(point.getX(), point.getY() - 1, point.getZ(), Blocks.fence);
    }

    public void update()
    {
        if (isProtected)
        {
            long currentTimeMillis = System.currentTimeMillis();
            protTime -= currentTimeMillis - lastTick;
            lastTick = currentTimeMillis;
            if (protTime < 0)
                isProtected = false;
        }
    }

    public boolean canOpen(EntityPlayer player)
    {
        if (opened)
            return false;
        if (!isProtected)
            return true;
        if (player.getUniqueID().equals(owner))
            return true;
        if (PermissionsManager.checkPermission(player, ModuleAfterlife.PERM_DEATHCHEST_BYPASS))
            return true;
        return false;
    }

    public void setOpen(boolean open)
    {
        opened = open;
    }

    public boolean isOpen()
    {
        return opened;
    }

    public WorldPoint getPosition()
    {
        return point;
    }

    public void interact(EntityPlayerMP player)
    {
        if (!canOpen(player))
        {
            OutputHandler.chatWarning(player, "This grave is still under divine protection.");
            return;
        }

        if (xp > 0)
        {
            player.addExperienceLevel(xp);
            xp = 0;
        }

        InventoryGrave invGrave = new InventoryGrave(this);

        if (player.openContainer != player.inventoryContainer)
            player.closeScreen();
        player.getNextWindowId();
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, invGrave.getInventoryName(), invGrave.getSizeInventory(),
                true));
        player.openContainer = new ContainerChest(player.inventory, invGrave);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
    }

    protected void dropItems()
    {
        for (ItemStack is : inventory)
        {
            EntityItem entity = new EntityItem(point.getWorld(), point.getX(), point.getY(), point.getZ(), is);
            point.getWorld().spawnEntityInWorld(entity);
        }
        inventory.clear();
    }

    public void remove(boolean dropItems)
    {
        if (dropItems)
            dropItems();

        point.getWorld().setBlock(point.getX(), point.getY(), point.getZ(), Blocks.air);
        if (hasFencePost && point.getWorld().getBlock(point.getX(), point.getY() - 1, point.getZ()) == Blocks.fence)
            point.getWorld().setBlock(point.getX(), point.getY() - 1, point.getZ(), Blocks.air);

        DataManager.getInstance().delete(Grave.class, point.toString());
        graves.remove(point);
    }

    public static void loadAll()
    {
        graves.clear();
        for (Grave grave : DataManager.getInstance().loadAll(Grave.class).values())
            graves.put(grave.getPosition(), grave);
    }

    public static void saveAll()
    {
        for (Grave grave : graves.values())
        {
            grave.update();
            DataManager.getInstance().save(grave, grave.getPosition().toString());
        }
    }

}
