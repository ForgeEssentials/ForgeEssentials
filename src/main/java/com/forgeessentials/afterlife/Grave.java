package com.forgeessentials.afterlife;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.annotations.Expose;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.*;
import static java.util.stream.Collectors.toList;


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

    protected Block block = Blocks.PLAYER_HEAD;

    @Expose(serialize = false)
    private boolean opened;

    @Expose(serialize = false)
    private long lastTick;

    @Expose(serialize = false)
    private BlockState blockState = block.defaultBlockState();

    public static Grave createGrave(PlayerEntity player, Collection<ItemEntity> drops)
    {
        if (!PermissionAPI.hasPermission(player, ModuleAfterlife.PERM_DEATHCHEST))
            return null;

        int xp = 0;
        Double xpModifier = ServerUtil.tryParseDouble(APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_XP));
        if (xpModifier != null)
        {
            xp = (int) (player.experienceLevel * xpModifier);
            player.experienceLevel = 0;
            player.totalExperience = 0;
        }

        // Create no grave if no experience / items available
        if (xp <= 0 && drops.isEmpty())
            return null;

        Grave grave = new Grave(player, drops, xp);
        graves.put(grave.point, grave);
        return grave;
    }

    public Grave(PlayerEntity player, Collection<ItemEntity> drops, int xp)
    {
        this.xp = xp;
        this.owner = player.getUUID();
        this.hasFencePost = PermissionAPI.hasPermission(player, ModuleAfterlife.PERM_DEATHCHEST_FENCE);
        this.lastTick = System.currentTimeMillis();
        this.protTime = ServerUtil.parseIntDefault(APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_SAFETIME), 0);
        if (protTime <= 0)
            isProtected = false;
        List<ItemEntity> newList = drops.stream().collect(toList());
        for (int i = 0; i < newList.size(); i++)
            inventory.add(newList.get(i).getItem().copy());

        // Get grave block
        String blockName = APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_BLOCK);
        if (blockName != null && !blockName.isEmpty())
        {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
            this.blockState = b.defaultBlockState();
        }

        point = new WorldPoint(player);
        point.setY(WorldUtil.placeInWorld(player.level, point.getX(), point.getY(), point.getZ(), hasFencePost ? 2 : 1, true));
        if (hasFencePost)
        {
            player.level.setBlockAndUpdate(point.getBlockPos(), Blocks.OAK_FENCE.defaultBlockState());
            LoggingHandler.felog.debug(String.format("Placing graveFence for player %s at %s", player.getName(), point.getBlockPos()));
            point.setY(point.getY() + 1);
        }
        player.level.setBlockAndUpdate(point.getBlockPos(), blockState);
        if (blockState.getBlock() == block)
        {
            TileEntitySkullGrave skull = new TileEntitySkullGrave(UserIdent.getGameProfileByUuid(owner));
            player.level.setBlockEntity(point.getBlockPos(), skull);
            LoggingHandler.felog.debug(String.format("Placing playerHead for player %s at %s", player.getName(), point.getBlockPos()));
        }
    }

    @Override
    public void afterLoad()
    {
        if (block == null)
            block = Blocks.PLAYER_HEAD;
    }

    public void updateBlocks()
    {
        if (point.getDimension().equals(null))
        {//!DimensionManager.isDimensionRegistered(point.getDimension())
            ServerWorld dworld = ServerLifecycleHooks.getCurrentServer().getLevel(point.getWorld().dimension());
            if (dworld.isLoaded(point.getBlockPos())) {
                DataManager.getInstance().delete(Grave.class, point.toString());
                graves.remove(point);
            }
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

        BlockState graveBlock = point.getWorld().getBlockState(point.getBlockPos());
        if (graveBlock != blockState && graveBlock.getBlock() != Blocks.CHEST)
        {
            // Grave is destroyed - repair if protection is still active
            if (isProtected)
            {
                point.getWorld().setBlockAndUpdate(point.getBlockPos(), blockState);
                if (blockState.getBlock() == block)
                {
                    TileEntitySkullGrave skull = new TileEntitySkullGrave(UserIdent.getGameProfileByUuid(owner));
                    point.getWorld().setBlockEntity(point.getBlockPos(), skull);
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
            if (point.getWorld().getBlockState(fencePos) != Blocks.OAK_FENCE.defaultBlockState())
                point.getWorld().setBlockAndUpdate(fencePos, Blocks.OAK_FENCE.defaultBlockState());
        }
    }

    public boolean canOpen(PlayerEntity player)
    {
        if (opened)
            return false;
        if (!isProtected)
            return true;
        if (player.getUUID().equals(owner))
            return true;
        if (PermissionAPI.hasPermission(player, ModuleAfterlife.PERM_DEATHCHEST_BYPASS))
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

    public void interact(ServerPlayerEntity player)
    {
        if (!canOpen(player))
        {
            ChatOutputHandler.chatWarning(player.createCommandSourceStack(), "This grave is still under divine protection.");
            return;
        }

        if (xp > 0)
        {
            player.giveExperienceLevels(xp);
            xp = 0;
        }

        InventoryGrave invGrave = new InventoryGrave(this);

        if (player.containerMenu != player.inventoryMenu)
            player.closeContainer();
        player.nextContainerCounter();
        player.openMenu(new SimpleNamedContainerProvider((i, inv, p) -> ChestContainer.threeRows(i, inv, invGrave), invGrave.getDisplayName()));
    }

    protected void dropItems()
    {
        for (ItemStack is : inventory)
        {
            if (is == ItemStack.EMPTY)
                continue;
            ItemEntity entity = new ItemEntity(point.getWorld(), point.getX(), point.getY(), point.getZ(), is);
            point.getWorld().addFreshEntity(entity);
        }
        inventory.clear();
    }

    public void remove(boolean dropItems)
    {
        if (dropItems)
            dropItems();

        point.getWorld().removeBlock(point.getBlockPos(), false);
        if (hasFencePost)
        {
            BlockPos fencePos = new BlockPos(point.getX(), point.getY() - 1, point.getZ());
            if (point.getWorld().getBlockState(fencePos) == Blocks.OAK_FENCE.defaultBlockState())
                point.getWorld().removeBlock(fencePos, false);
        }

        DataManager.getInstance().delete(Grave.class, point.toString());
        graves.remove(point);
    }

    public static void loadAll()
    {
        graves.clear();
        for (Grave grave : DataManager.getInstance().loadAll(Grave.class).values())
        {
            if (grave.getPosition().getDimension().equals(null))
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
