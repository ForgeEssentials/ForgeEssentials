package com.forgeessentials.snooper.response;

import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.util.FunctionHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class PlayerInv extends Response {

    @Override
    public String getName()
    {
        return "PlayerInv";
    }

    @Override
    public void readConfig(String category, Configuration config)
    {
        // Don't need that here
    }

    @Override
    public void writeConfig(String category, Configuration config)
    {
        // Don't need that here
    }

    @Override
    public JsonElement getResponce(JsonObject input) throws JsonParseException
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

        JsonObject playerData = new JsonObject();
        JsonArray inv = new JsonArray();

        for (ItemStack stack : player.inventory.mainInventory)
        {
            if (stack != null)
            {
                inv.add(FunctionHelper.toJSON(stack, true));
            }
        }
        playerData.add("Inventory", inv);

        inv = new JsonArray();

        for (int i = 0; i < 3; i++)
        {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null)
            {
                inv.add(FunctionHelper.toJSON(stack, true));
            }
        }
        playerData.add("Armor", inv);

        JsonObject out = new JsonObject();
        out.add(getName(), playerData);
        return out;
    }
}
