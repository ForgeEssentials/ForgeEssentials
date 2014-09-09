package com.forgeessentials.snooper.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.WarpPoint;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PlayerInfoResponse extends Response
{
    private boolean sendHome;
    private boolean sendPotions;
    private boolean sendXP;
    private boolean sendArmorAndHealth;
    private boolean sendFood;
    private boolean sendCapabilities;
    private boolean sendMoney;
    private boolean sendPosition;
    
    @Override
    public JsonElement getResponse(JsonObject input) throws JsonParseException
    {
        if (!input.has("username"))
        {
            JsonObject out = new JsonObject();
            out.addProperty(getName(), "This responce needs a username!");
            return out;
        }

        EntityPlayerMP player = server.getConfigurationManager().func_152612_a(input.get("username").getAsString());
        if (player == null)
        {
            JsonObject out = new JsonObject();
            out.addProperty(getName(), input.get("username").getAsString() + " not online!");
            return out;
        }
        
        Data data = new Data();

        PlayerInfo pi = PlayerInfo.getPlayerInfo(player.getPersistentID());
        if (pi != null && sendHome)
        {
            data.home = pi.getHome();
            data.back = pi.getLastTeleportOrigin();
        }

        if (sendArmorAndHealth)
        {
            data.armor = player.inventory.getTotalArmorValue();
            data.health = player.getHealth();
        }
        
        if (sendMoney)
        {
            data.money = APIRegistry.wallet.getWallet(player.getPersistentID());
        }
            
        if (sendPosition)
        {
            data.position = new WarpPoint(player);
        }
        
        data.ping = player.ping;
        data.gameMode = player.theItemInWorldManager.getGameType().getName();

        if (!player.getActivePotionEffects().isEmpty() && sendPotions)
        {
            @SuppressWarnings("unchecked")
            Collection<PotionEffect> effects = player.getActivePotionEffects();
            data.potions = new ArrayList<String>(effects.size());
            for (PotionEffect effect : effects)
            {
                String name = StatCollector.translateToLocal(Potion.potionTypes[effect.getPotionID()].getName());
                switch(effect.getAmplifier())
                {
                    case 1: 
                        name += " II";
                        break;
                    case 2: 
                        name += " III";
                        break;
                    case 3: 
                        name += " IV";
                        break;
                }
                data.potions.add(name);
            }
        }

        if (sendXP)
        {
            data.xp = new XP();
            data.xp.lvl = player.experienceLevel;
            data.xp.bar = player.experience;
        }

        if (sendFood)
        {
            data.foodStats = new FoodStats();
            data.foodStats.food = player.getFoodStats().getFoodLevel();
            data.foodStats.saturation = player.getFoodStats().getSaturationLevel();
        }

        if (sendCapabilities)
        {
            data.capabilities = new Capabilities();
            data.capabilities.allowEdit = player.capabilities.allowEdit;
            data.capabilities.allowFlying = player.capabilities.allowFlying;
            data.capabilities.isFlying = player.capabilities.isFlying;
            data.capabilities.disableDamage = player.capabilities.disableDamage;
        }

        data.group = APIRegistry.perms.getPrimaryGroup(player).getName();
        data.firstJoin = pi.getFirstJoin();
        data.timePlayed = pi.getTimePlayed();

        return GSON.toJsonTree(data);

    }

    @Override
    public String getName()
    {
        return "PlayerInfoResonce";
    }

    @Override
    public void readConfig(String category, Configuration config)
    {
        sendHome = config.get(category, "sendHome", true).getBoolean(true);
        sendPotions = config.get(category, "sendPotions", true).getBoolean(true);
        sendXP = config.get(category, "sendXP", true).getBoolean(true);
        sendArmorAndHealth = config.get(category, "sendArmorAndHealth", true).getBoolean(true);
        sendFood = config.get(category, "sendFood", true).getBoolean(true);
        sendCapabilities = config.get(category, "sendCapabilities", true).getBoolean(true);
        sendPosition = config.get(category, "sendPosition", true).getBoolean(true);
        sendMoney = config.get(category, "sendMoney", true).getBoolean(true);
    }

    @Override
    public void writeConfig(String category, Configuration config)
    {
        config.get(category, "sendHome", true).set(sendHome);
        config.get(category, "sendPotions", true).set(sendPotions);
        config.get(category, "sendXP", true).set(sendXP);
        config.get(category, "sendArmorAndHealth", true).set(sendArmorAndHealth);
        config.get(category, "sendFood", true).set(sendFood);
        config.get(category, "sendCapabilities", true).set(sendCapabilities);
        config.get(category, "sendPosition", true).set(sendPosition);
        config.get(category, "sendMoney", true).set(sendMoney);
    }

    private static final class Data
    {
        Capabilities capabilities;
        FoodStats foodStats;
        XP xp;
        long firstJoin;
        int armor, money, timePlayed, ping;
        float health;
        String gameMode, group;
        WarpPoint home, back, position;
        List<String> potions;
    }

    private static final class Capabilities
    {
        boolean allowEdit, allowFlying, isFlying, disableDamage;
    }

    private static final class FoodStats
    {
        int food;
        float saturation;
    }

    private static final class XP
    {
        int lvl;
        float bar;
    }
}
