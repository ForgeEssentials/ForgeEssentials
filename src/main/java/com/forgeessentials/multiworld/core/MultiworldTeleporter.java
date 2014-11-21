package com.forgeessentials.multiworld.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import com.forgeessentials.util.FunctionHelper;

public class MultiworldTeleporter extends Teleporter {

    protected final WorldServer world;

    public MultiworldTeleporter(WorldServer world)
    {
        super(world);
        this.world = world;
    }

    /**
     * Teleport the player to the dimension
     */
    public void teleport(EntityPlayerMP player)
    {
        teleport(player, false, true);
    }

    /**
     * Teleport the player to the dimension
     */
    public void teleport(EntityPlayerMP player, boolean showDepartMessage, boolean showWelcomeMessage)
    {
        if (player.worldObj.provider.dimensionId != world.provider.dimensionId)
        {
            if (showDepartMessage)
                displayDepartMessage(player);

            // ChunkCoordinates spawn = world.getSpawnPoint();
            int x = (int) player.posX;
            int z = (int) player.posZ;
            int y = FunctionHelper.placeInWorld(world, x, (int) player.posY, z);

            // Set position and velocity before teleporting
            player.motionX = player.motionY = player.motionZ = 0;
            player.setPosition(x + 0.5d, y + 0.5d, z + 0.5d);

            // Set Dimension
            player.mcServer.getConfigurationManager().transferPlayerToDimension(player, world.provider.dimensionId, this);
            player.setPositionAndUpdate(x + 0.5d, y + 0.5d, z + 0.5d);

            if (showWelcomeMessage)
                displayWelcomeMessage(player);
        }
    }

    @Override
    public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
    {
        return false;
    }

    @Override
    public void removeStalePortalLocations(long totalWorldTime)
    {
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float rotationYaw)
    {
    }

    public void displayDepartMessage(EntityPlayerMP player)
    {
        String msg = player.worldObj.provider.getDepartMessage();
        if (msg == null)
            msg = "Leaving the Overworld.";
        if (player.dimension > 1 || player.dimension < -1)
            msg += " (#" + player.dimension + ")";
        player.addChatMessage(new ChatComponentText(msg));
    }

    public void displayWelcomeMessage(EntityPlayerMP player)
    {
        String msg = player.worldObj.provider.getWelcomeMessage();
        if (msg == null)
            msg = "Entering the Overworld.";
        if (player.dimension > 1 || player.dimension < -1)
            msg += " (#" + player.dimension + ")";
        player.addChatMessage(new ChatComponentText(msg));
    }

}
