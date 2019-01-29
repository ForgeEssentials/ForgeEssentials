package com.forgeessentials.signtools;

import net.minecraft.init.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fe.event.world.SignEditEvent;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "SignTools", parentMod = ForgeEssentials.class)
public class SignToolsModule extends ConfigLoaderBase
{

    public static final String COLORIZE_PERM = "fe.signs.colorize";
    
    private static boolean allowSignCommands, allowSignEdit;

    @SubscribeEvent
    public void onLoad(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void registerPerms(FEModuleServerInitEvent e)
    {
        PermissionManager.registerPermission(COLORIZE_PERM, PermissionLevel.TRUE);
    }

    /**
     * works the same as the old /colorize command
     * 
     * @param e
     */
    @SubscribeEvent
    public void onSignEdit(SignEditEvent e)
    {
        if (!PermissionManager.checkPermission(e.editor, COLORIZE_PERM))
        {
            return;
        }
        else
        {
            for (int i = 0; i < e.text.length; i++)
            {
                if (e.text[i].contains("&"))
                {
                    e.text[i] = ChatOutputHandler.formatColors(e.text[i]);
                }
            }

        }

    }

    /**
     * how to use: First line of the sign MUST BE [command] Second line is the command you want to run Third and fourth
     * lines are arguments to the command.
     */

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        if (e.world.isRemote) {
            return;
        }

        if (!allowSignCommands || !e.action.equals(Action.RIGHT_CLICK_BLOCK))
        {
            return;
        }

        TileEntity te = e.entityPlayer.worldObj.getTileEntity(e.x, e.y, e.z);
        if (te != null && te instanceof TileEntitySign)
        {
            if (allowSignEdit && e.entityPlayer.isSneaking())
            {
                if(e.entityPlayer.getCurrentEquippedItem()!= null)
                {
                    if (e.entityPlayer.getCurrentEquippedItem().getItem().equals(Items.sign) &&
                            PermissionManager.checkPermission(e.entityPlayer, "fe.protection.use.minecraft.sign"))
                    {
                        e.entityPlayer.func_146100_a(te);
                        e.setCanceled(true);
                    }
                }

            }

            String[] signText = ((TileEntitySign) te).signText;
            if (!signText[0].equals("[command]"))
            {
                return;
            }

            else
            {
                String send = StringUtils.join(ServerUtil.dropFirst(signText), " ");
                if (send != null)
                {
                    MinecraftServer.getServer().getCommandManager().executeCommand(e.entityPlayer, send);
                    e.setCanceled(true);
                }
            }

        }

    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        // this is NOT a permprop because perms are checked against the player anyway
        allowSignCommands = config.getBoolean("allowSignCommands", "Signs", true, "Allow commands to be run when right clicking signs.");
        allowSignEdit = config.getBoolean("allowSignEditing", "Signs", true, "Allow players to edit a sign by right clicking on it with a sign item while sneaking.");
    }
}
