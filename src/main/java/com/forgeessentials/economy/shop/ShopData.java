package com.forgeessentials.economy.shop;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.ServerUtil;
import com.google.gson.annotations.Expose;

public class ShopData
{

    public static final Pattern PATTERN_BUY = Pattern.compile("buy\\s+(?:for\\s+)?(\\d+)");

    public static final Pattern PATTERN_SELL = Pattern.compile("sell\\s+(?:for\\s+)?(\\d+)");

    public static final Pattern PATTERN_AMOUNT = Pattern.compile("amount\\s+(\\d+)");

    /* ------------------------------------------------------------ */

    protected final WorldPoint pos;

    protected final UUID itemFrameId;

    @Expose(serialize = false, deserialize = false)
    protected WeakReference<EntityItemFrame> itemFrame;

    @Expose(serialize = false, deserialize = false)
    protected boolean isValid;

    @Expose(serialize = false, deserialize = false)
    protected int buyPrice = -1;

    @Expose(serialize = false, deserialize = false)
    protected int sellPrice = -1;

    @Expose(serialize = false, deserialize = false)
    protected int amount = 1;

    @Expose(serialize = false, deserialize = false)
    protected String error;

    @Expose(serialize = false, deserialize = false)
    protected ItemStack item;

    private int stock;

    /* ------------------------------------------------------------ */

    public ShopData(WorldPoint point, EntityItemFrame frame)
    {
        this.pos = point;
        this.itemFrameId = frame.getPersistentID();
        this.itemFrame = new WeakReference<EntityItemFrame>(frame);
    }

    public void update()
    {
        isValid = false;
        error = null;
        item = null;

        // if (!ItemUtil.isSign(signPosition.getBlock())) return;
        IChatComponent[] text = ItemUtil.getSignText(pos);
        if (text == null || text.length < 2 || !ShopManager.shopTags.contains(text[0].getUnformattedText()))
        {
            error = Translator.translate("Sign header missing");
            return;
        }

        EntityItemFrame frame = getItemFrame();
        if (frame == null)
        {
            error = Translator.translate("Item frame missing");
            return;
        }

        item = frame.getDisplayedItem();
        if (item == null)
        {
            error = Translator.translate("Item frame empty");
            return;
        }

        buyPrice = -1;
        sellPrice = -1;
        amount = 1;
        for (int i = 1; i < text.length; i++)
        {
            Matcher matcher = PATTERN_BUY.matcher(text[i].getUnformattedText());
            if (matcher.matches())
            {
                if (buyPrice != -1)
                {
                    error = Translator.translate("Buy price specified twice");
                    return;
                }
                buyPrice = ServerUtil.parseIntDefault(matcher.group(1), -1);
                continue;
            }
            matcher = PATTERN_SELL.matcher(text[i].getUnformattedText());
            if (matcher.matches())
            {
                if (sellPrice != -1)
                {
                    error = Translator.translate("Sell price specified twice");
                    return;
                }
                sellPrice = ServerUtil.parseIntDefault(matcher.group(1), -1);
                continue;
            }
            matcher = PATTERN_AMOUNT.matcher(text[i].getUnformattedText());
            if (matcher.matches())
            {
                if (amount != 1)
                {
                    error = Translator.translate("Amount specified twice");
                    return;
                }
                amount = ServerUtil.parseIntDefault(matcher.group(1), 1);
                continue;
            }
        }

        if (buyPrice == -1 && sellPrice == -1)
        {
            error = Translator.translate("No price specified");
            return;
        }
        if (amount < 1)
        {
            error = Translator.translate("Amount smaller than 1");
            return;
        }

        isValid = true;
    }

    public ItemStack getItemStack()
    {
        if (!isValid)
            return null;
        ItemStack itemStackCopy = item.copy();
        item.stackSize = amount;
        return itemStackCopy;
    }

    public WorldPoint getSignPosition()
    {
        return pos;
    }

    public String getError()
    {
        return error;
    }

    public EntityItemFrame getItemFrame()
    {
        EntityItemFrame frame = itemFrame == null ? null : itemFrame.get();
        if (frame == null)
        {
            List<EntityItemFrame> entities = getEntitiesWithinAABB(pos.getWorld(), EntityItemFrame.class, getSignAABB(pos));
            for (EntityItemFrame entityItemFrame : entities)
            {
                if (entityItemFrame.getPersistentID().equals(itemFrameId))
                {
                    frame = entityItemFrame;
                    itemFrame = new WeakReference<EntityItemFrame>(frame);
                    break;
                }
            }
        }
        return frame;
    }

    public static EntityItemFrame findFrame(WorldPoint p)
    {
        AxisAlignedBB aabb = getSignAABB(p);
        List<EntityItemFrame> entities = getEntitiesWithinAABB(p.getWorld(), EntityItemFrame.class, aabb);
        if (entities.isEmpty())
            return null;
        if (entities.size() == 1)
            return entities.get(0);

        final Vec3 offset = new Vec3(p.getX(), p.getY() + 0.5, p.getZ());
        Collections.sort(entities, new Comparator<EntityItemFrame>() {
            @Override
            public int compare(EntityItemFrame o1, EntityItemFrame o2)
            {
                Vec3 v1 = new Vec3(o1.posX, o1.posY, o1.posZ);
                Vec3 v2 = new Vec3(o2.posX, o2.posY, o2.posZ);
                return (int) Math.signum(offset.distanceTo(v1) - offset.distanceTo(v2));
            }
        });

        for (Iterator<EntityItemFrame> it = entities.iterator(); it.hasNext();)
        {
            if (entities.size() == 1)
                break;
            if (ShopManager.shopFrameMap.containsKey(it.next().getPersistentID()))
                it.remove();
        }
        return entities.get(0);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getEntitiesWithinAABB(World world, Class<? extends T> clazz, AxisAlignedBB aabb)
    {
        return world.getEntitiesWithinAABB(clazz, aabb);
    }

    public static AxisAlignedBB getSignAABB(WorldPoint p)
    {
        double x = p.getX();
        double y = p.getY() + 0.5;
        double z = p.getZ();
        double D = 1.4;
        AxisAlignedBB aabb = AxisAlignedBB.fromBounds(x - D, y - D, z - D, x + D, y + D, z + D);
        return aabb;
    }

    public int getStock()
    {
        return stock;
    }

    public void setStock(int stock)
    {
        this.stock = stock;
    }

}
