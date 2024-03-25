package com.forgeessentials.afterlife;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.google.gson.annotations.Expose;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class Grave implements Loadable
{

    protected static Map<Point, Grave> graves = new HashMap<>();

    protected WorldPoint point;

    protected UUID owner;

    protected List<ItemStack> inventory = new ArrayList<>();

    protected int xp;

    protected int protTime;

    protected boolean hasFencePost;

    protected boolean isProtected = true;

    @Expose(serialize = false)
    private Block block = Blocks.PLAYER_HEAD;

    @Expose(serialize = false)
    private boolean open;

    @Expose(serialize = false)
    private long lastTick;

    @Expose(serialize = false)
    private BlockState blockState = block.defaultBlockState();

    public static Grave createGrave(Player player, Collection<ItemEntity> drops)
    {
        if (!APIRegistry.perms.checkPermission(player, ModuleAfterlife.PERM_DEATHCHEST))
            return null;

        int xp = 0;
        Double xpModifier = ServerUtil
                .tryParseDouble(APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_XP));
        if (xpModifier != null)
        {
            //System.out.println("TodalXP is: "+ player.totalExperience);
            //System.out.println("LevelXP is: "+ player.experienceLevel);
            //System.out.println("ProgrXP is: "+ player.experienceProgress);
            //which value should i use for experience?
            xp = (int) (player.experienceLevel * xpModifier);
            player.experienceLevel = 0;
            player.totalExperience = 0;
            player.experienceProgress = 0;
        }

        // Create no grave if no experience / items available
        if (xp <= 0 && drops.isEmpty())
            return null;

        Grave grave = new Grave(player, drops, xp);
        graves.put(grave.point, grave);
        return grave;
    }

    public Grave(Player player, Collection<ItemEntity> drops, int xp)
    {
        this.xp = xp;
        this.owner = player.getGameProfile().getId();
        this.hasFencePost = APIRegistry.perms.checkPermission(player, ModuleAfterlife.PERM_DEATHCHEST_FENCE);
        this.lastTick = System.currentTimeMillis();
        this.protTime = ServerUtil.parseIntDefault(
                APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_SAFETIME), 0);
        if (protTime <= 0)
            isProtected = false;
        List<ItemEntity> newList = new ArrayList<>(drops);
        for (ItemEntity itemEntity : newList) inventory.add(itemEntity.getItem().copy());

        // Get grave block
        String blockName = APIRegistry.perms.getPermissionProperty(player, ModuleAfterlife.PERM_DEATHCHEST_BLOCK);
        if (blockName != null && !blockName.isEmpty())
        {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
            this.blockState = b.defaultBlockState();
        }

        point = new WorldPoint(player);
        point.setY(WorldUtil.placeInWorld(player.level, point.getX(), point.getY(), point.getZ(), hasFencePost ? 2 : 1,
                true));
        if (hasFencePost)
        {
            player.level.setBlockAndUpdate(point.getBlockPos(), Blocks.OAK_FENCE.defaultBlockState());
            LoggingHandler.felog.debug(String.format("Placing graveFence for player %s at %s",
                    player.getDisplayName().getString(), point.getBlockPos()));
            point.setY(point.getY() + 1);
        }
        player.level.setBlockAndUpdate(point.getBlockPos(), blockState);
        if (blockState.getBlock() == block)
        {
            TileEntitySkullGrave skull = new TileEntitySkullGrave(point.getBlockPos(), blockState);
            skull.setOwner(UserIdent.getGameProfileByUuid(owner));
            player.level.setBlockEntity(skull);
            LoggingHandler.felog.debug(String.format("Placing playerHead for player %s at %s",
                    player.getDisplayName().getString(), point.getBlockPos()));
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
        if (point.getWorld() == null)
        {
            ServerLevel dworld = ServerLifecycleHooks.getCurrentServer()
                    .getLevel(ServerUtil.getWorldKeyFromString(point.getDimension()));
            if (dworld == null)
            {
                DataManager.getInstance().delete(Grave.class, point.toString().replace(":", "-"));
                graves.remove(point);
            }
            return;
        }
        if (isProtected)
        {
            long currentTimeMillis = System.currentTimeMillis();
            protTime -= (currentTimeMillis - lastTick) / 1000;
            lastTick = currentTimeMillis;
            if (protTime < 0)
                isProtected = false;
        }

        BlockState graveBlock = point.getWorld().getBlockState(point.getBlockPos());
        if (graveBlock != blockState && graveBlock.getBlock() != block)
        {
            // Grave is destroyed - repair if protection is still active
            if (isProtected)
            {
                if (blockState.getBlock() == block)
                {
                    TileEntitySkullGrave skull = new TileEntitySkullGrave(point.getBlockPos(), blockState);
                    skull.setOwner(UserIdent.getGameProfileByUuid(owner));
                    point.getWorld().setBlockEntity(skull);
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

    public boolean canOpen(Player player)
    {
        if (open)
            return false;
        if (player.getGameProfile().getId().equals(owner))
            return true;
        if (!isProtected)
            return true;
        return APIRegistry.perms.checkPermission(player, ModuleAfterlife.PERM_DEATHCHEST_BYPASS);
    }

    public void setOpen(boolean openT)
    {
        open = openT;
    }

    public boolean isOpen()
    {
        return open;
    }

    public WorldPoint getPosition()
    {
        return point;
    }

    public void interact(ServerPlayer player)
    {
        if (isOpen())
        {
            return;
        }
        if (!canOpen(player))
        {
            ChatOutputHandler.chatWarning(player.createCommandSourceStack(),
                    "This grave is still under divine protection.");
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
        player.openMenu(new SimpleMenuProvider(
                (i, inv, p) -> new ChestMenu(MenuType.GENERIC_9x5, i, inv, invGrave, 5),
                invGrave.getDisplayName()));
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

        DataManager.getInstance().delete(Grave.class, point.toString().replace(":", "-"));
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
            DataManager.getInstance().save(grave, grave.getPosition().toString().replace(":", "-"));
    }

}
