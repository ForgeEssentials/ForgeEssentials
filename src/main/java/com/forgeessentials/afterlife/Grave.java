package com.forgeessentials.afterlife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.gson.annotations.Expose;

public class Grave implements Loadable
{

    protected static Map<Point, Grave> graves = new HashMap<Point, Grave>();

    protected WorldPoint point;

    protected UUID owner;

    protected List<ItemStack> inventory = new ArrayList<ItemStack>();

    protected int xp;

    protected int protTime;

    protected boolean hasFencePost;

    protected boolean isProtected = true;

    protected Block block = Blocks.skull;

    @Expose(serialize = false)
    private boolean opened;

    @Expose(serialize = false)
    private long lastTick;

    @Expose(serialize = false)
    private IBlockState blockState = Blocks.skull.getDefaultState();

    public static Grave createGrave(EntityPlayer player, List<EntityItem> drops)
    {
        if (!PermissionManager.checkPermission(player, ModuleAfterlife.PERM_DEATHCHEST))
            return null;

        int xp = 0;
        Double xpModifier = ServerUtil.tryParseDouble(APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_XP));
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
        this.hasFencePost = PermissionManager.checkPermission(player, ModuleAfterlife.PERM_DEATHCHEST_FENCE);
        this.lastTick = System.currentTimeMillis();
        this.protTime = ServerUtil.parseIntDefault(APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_SAFETIME), 0);
        if (protTime <= 0)
            isProtected = false;
        for (int i = 0; i < drops.size(); i++)
            inventory.add(drops.get(i).getEntityItem().copy());

        // Get grave block
        String blockName = APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_BLOCK);
        if (blockName != null && !blockName.isEmpty())
        {
            Block b = GameData.getBlockRegistry().getObject(blockName);
            if (b != null)
                this.blockState = b.getDefaultState();
        }

        point = new WorldPoint(player);
        point.setY(WorldUtil.placeInWorld(player.worldObj, point.getX(), point.getY(), point.getZ(), hasFencePost ? 2 : 1));
        if (hasFencePost)
        {
            player.worldObj.setBlockState(point.getBlockPos(), Blocks.oak_fence.getDefaultState());
            point.setY(point.getY() + 1);
        }
        point.getWorld().setBlockState(point.getBlockPos(), blockState);
        if (blockState == Blocks.skull)
        {
            TileEntitySkullGrave skull = new TileEntitySkullGrave(UserIdent.getGameProfileByUuid(owner));
            point.getWorld().setTileEntity(point.getBlockPos(), skull);
        }
    }

    @Override
    public void afterLoad()
    {
        if (block == null)
            block = Blocks.skull;
    }

    public void updateBlocks()
    {
        if (point.getWorld() == null)
        {
            DataManager.getInstance().delete(Grave.class, point.toString());
            graves.remove(point);
            return;
        }
        if (isProtected)
        {
            long currentTimeMillis = System.currentTimeMillis();
            protTime -= currentTimeMillis - lastTick;
            lastTick = currentTimeMillis;
            if (protTime < 0)
                isProtected = false;
        }

        IBlockState graveBlock = point.getWorld().getBlockState(point.getBlockPos());
        if (graveBlock != blockState && graveBlock != Blocks.chest)
        {
            // Grave is destroyed - repair if protection is still active
            if (isProtected)
            {
                point.getWorld().setBlockState(point.getBlockPos(), blockState);
                if (blockState == Blocks.skull)
                {
                    TileEntitySkullGrave skull = new TileEntitySkullGrave(UserIdent.getGameProfileByUuid(owner));
                    point.getWorld().setTileEntity(point.getBlockPos(), skull);
                }
            }
            else
            {
                remove(true);
            }
        }
        if (hasFencePost)
        {
            BlockPos fencePos = new BlockPos(point.getX(), point.getY() - 1, point.getZ());
            if (point.getWorld().getBlockState(fencePos) != Blocks.oak_fence.getDefaultState())
                point.getWorld().setBlockState(fencePos, Blocks.oak_fence.getDefaultState());
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
        if (PermissionManager.checkPermission(player, ModuleAfterlife.PERM_DEATHCHEST_BYPASS))
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
            ChatOutputHandler.chatWarning(player, "This grave is still under divine protection.");
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
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, "minecraft:chest", invGrave.getDisplayName(), invGrave
                .getSizeInventory()));
        player.openContainer = new ContainerChest(player.inventory, invGrave, player);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
    }

    protected void dropItems()
    {
        for (ItemStack is : inventory)
        {
            if (is == null || is.getItem() == null)
                continue;
            EntityItem entity = new EntityItem(point.getWorld(), point.getX(), point.getY(), point.getZ(), is);
            point.getWorld().spawnEntityInWorld(entity);
        }
        inventory.clear();
    }

    public void remove(boolean dropItems)
    {
        if (dropItems)
            dropItems();

        point.getWorld().setBlockToAir(point.getBlockPos());
        if (hasFencePost)
        {
            BlockPos fencePos = new BlockPos(point.getX(), point.getY() - 1, point.getZ());
            if (point.getWorld().getBlockState(fencePos) == Blocks.oak_fence.getDefaultState())
                point.getWorld().setBlockToAir(fencePos);
        }

        DataManager.getInstance().delete(Grave.class, point.toString());
        graves.remove(point);
    }

    public static void loadAll()
    {
        graves.clear();
        for (Grave grave : DataManager.getInstance().loadAll(Grave.class).values())
        {
            if (grave.getPosition().getWorld() == null)
                continue;
            graves.put(grave.getPosition(), grave);
        }
    }

    public static void saveAll()
    {
        for (Grave grave : graves.values())
            DataManager.getInstance().save(grave, grave.getPosition().toString());
    }

}
