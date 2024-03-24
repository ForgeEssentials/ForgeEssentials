package com.forgeessentials.economy.shop;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.ServerUtil;
import com.google.gson.annotations.Expose;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

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
    protected WeakReference<ItemFrame> itemFrame;

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

    public ShopData(WorldPoint point, ItemFrame frame)
    {
        this.pos = point;
        this.itemFrameId = frame.getUUID();
        this.itemFrame = new WeakReference<>(frame);
        this.stock = 0;
    }

    public void update()
    {
        isValid = false;
        error = null;
        item = null;

        // if (!ItemUtil.isSign(signPosition.getBlock())) return;
        Component[] text = ItemUtil.getSignText(pos);
        if (text == null || text.length < 2 || !ShopManager.shopTags.contains(text[0].plainCopy().getContents()))
        {
            error = Translator.translate("Sign header missing");
            return;
        }

        ItemFrame frame = getItemFrame();
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
            Matcher matcher = PATTERN_BUY.matcher(text[i].getString());
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
            matcher = PATTERN_SELL.matcher(text[i].getString());
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
            matcher = PATTERN_AMOUNT.matcher(text[i].getString());
            if (matcher.matches())
            {
                if (amount != 1)
                {
                    error = Translator.translate("Amount specified twice");
                    return;
                }
                amount = ServerUtil.parseIntDefault(matcher.group(1), 1);
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

    public ItemFrame getItemFrame()
    {
        ItemFrame frame = itemFrame == null ? null : itemFrame.get();
        if (frame == null)
        {
            List<ItemFrame> entities = getEntitiesWithinAABB(pos.getWorld(), ItemFrame.class,
                    getSignAABB(pos));
            for (ItemFrame entityItemFrame : entities)
            {
                if (entityItemFrame.getUUID().equals(itemFrameId))
                {
                    frame = entityItemFrame;
                    itemFrame = new WeakReference<>(frame);
                    break;
                }
            }
        }
        return frame;
    }

    public static ItemFrame findFrame(WorldPoint p)
    {
        AABB aabb = getSignAABB(p);
        List<ItemFrame> entities = getEntitiesWithinAABB(p.getWorld(), ItemFrame.class, aabb);
        if (entities.isEmpty())
            return null;
        if (entities.size() == 1)
            return entities.get(0);

        final Vec3 offset = new Vec3(p.getX(), p.getY() + 0.5, p.getZ());
        entities.sort(new Comparator<ItemFrame>() {
            @Override
            public int compare(ItemFrame o1, ItemFrame o2) {
                Vec3 v1 = new Vec3(o1.position().x, o1.position().y, o1.position().z);
                Vec3 v2 = new Vec3(o2.position().x, o2.position().y, o2.position().z);
                return (int) Math.signum(offset.distanceTo(v1) - offset.distanceTo(v2));
            }
        });

        for (Iterator<ItemFrame> it = entities.iterator(); it.hasNext();)
        {
            if (entities.size() == 1)
                break;
            if (ShopManager.shopFrameMap.containsKey(it.next().getUUID()))
                it.remove();
        }
        return entities.get(0);
    }

    public static <T extends Entity> List<T> getEntitiesWithinAABB(Level world, Class<T> clazz,
            AABB aabb)
    {
        return world.getEntitiesOfClass(clazz, aabb);
    }

    public static AABB getSignAABB(WorldPoint p)
    {
        double x = p.getX();
        double y = p.getY() + 0.5;
        double z = p.getZ();
        double D = 1.4;
        return new AABB(x - D, y - D, z - D, x + D, y + D, z + D);
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
