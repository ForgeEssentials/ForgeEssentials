package com.forgeessentials.economy.shop;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.ServerUtil;
import com.google.gson.annotations.Expose;

public class ShopData
{

    public static final Pattern PATTERN_BUY;

    public static final Pattern PATTERN_SELL;

    public static final Pattern PATTERN_AMOUNT;

    static
    {
        PATTERN_BUY = Pattern.compile(Translator.translate("buy\\s+(?:for\\s+)?(\\d+)"));
        PATTERN_SELL = Pattern.compile(Translator.translate("sell\\s+(?:for\\s+)?(\\d+)"));
        PATTERN_AMOUNT = Pattern.compile(Translator.translate("amount\\s+(\\d+)"));
    }

    /* ------------------------------------------------------------ */

    protected final WorldPoint pos;

    protected final UUID itemFrameId;

    @Expose(serialize = false, deserialize = false)
    protected WeakReference<ItemFrameEntity> itemFrame;

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

    public ShopData(WorldPoint point, ItemFrameEntity frame)
    {
        this.pos = point;
        this.itemFrameId = frame.getUUID();
        this.itemFrame = new WeakReference<ItemFrameEntity>(frame);
    }

    public void update()
    {
        isValid = false;
        error = null;
        item = null;

        // if (!ItemUtil.isSign(signPosition.getBlock())) return;
        ITextComponent[] text = ItemUtil.getSignText(pos);
        if (text == null || text.length < 2 || !ShopManager.shopTags.contains(text[0].plainCopy().getContents()))
        {
            error = Translator.translate("Sign header missing");
            return;
        }

        ItemFrameEntity frame = getItemFrame();
        if (frame == null)
        {
            error = Translator.translate("Item frame missing");
            return;
        }

        item = frame.getItem();
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
            Matcher matcher = PATTERN_BUY.matcher(text[i].plainCopy().getContents());
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
            matcher = PATTERN_SELL.matcher(text[i].plainCopy().getContents());
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
            matcher = PATTERN_AMOUNT.matcher(text[i].plainCopy().getContents());
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
        item.setCount(amount);
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

    public ItemFrameEntity getItemFrame()
    {
        ItemFrameEntity frame = itemFrame == null ? null : itemFrame.get();
        if (frame == null)
        {
            List<ItemFrameEntity> entities = getEntitiesWithinAABB(pos.getWorld(), ItemFrameEntity.class, getSignAABB(pos));
            for (ItemFrameEntity entityItemFrame : entities)
            {
                if (entityItemFrame.getUUID().equals(itemFrameId))
                {
                    frame = entityItemFrame;
                    itemFrame = new WeakReference<ItemFrameEntity>(frame);
                    break;
                }
            }
        }
        return frame;
    }

    public static ItemFrameEntity findFrame(WorldPoint p)
    {
        AxisAlignedBB aabb = getSignAABB(p);
        List<ItemFrameEntity> entities = getEntitiesWithinAABB(p.getWorld(), ItemFrameEntity.class, aabb);
        if (entities.isEmpty())
            return null;
        if (entities.size() == 1)
            return entities.get(0);

        final Vector3d offset = new Vector3d(p.getX(), p.getY() + 0.5, p.getZ());
        Collections.sort(entities, new Comparator<ItemFrameEntity>() {
            @Override
            public int compare(ItemFrameEntity o1, ItemFrameEntity o2)
            {
                Vector3d v1 = new Vector3d(o1.position().x, o1.position().y, o1.position().z);
                Vector3d v2 = new Vector3d(o2.position().x, o2.position().y, o2.position().z);
                return (int) Math.signum(offset.distanceTo(v1) - offset.distanceTo(v2));
            }
        });

        for (Iterator<ItemFrameEntity> it = entities.iterator(); it.hasNext();)
        {
            if (entities.size() == 1)
                break;
            if (ShopManager.shopFrameMap.containsKey(it.next().getUUID()))
                it.remove();
        }
        return entities.get(0);
    }

    public static <T extends Entity> List<T> getEntitiesWithinAABB(World world, Class<? extends T> clazz, AxisAlignedBB aabb)
    {
        return world.getEntitiesOfClass(clazz, aabb);
    }

    public static AxisAlignedBB getSignAABB(WorldPoint p)
    {
        double x = p.getX();
        double y = p.getY() + 0.5;
        double z = p.getZ();
        double D = 1.4;
        AxisAlignedBB aabb = new AxisAlignedBB(x - D, y - D, z - D, x + D, y + D, z + D);
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
