package com.forgeessentials.signtools;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fe.event.world.SignEditEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.output.ChatOutputHandler;

@FEModule(name = "SignTools", parentMod = ForgeEssentials.class)
public class SignToolsModule extends ConfigLoaderBase
{

    public static final String COLORIZE_PERM = "fe.signs.colorize";
    public static final String EDIT_PERM = "fe.signs.edit";

    private static boolean allowSignCommands, allowSignEdit;

    @SubscribeEvent
    public void onLoad(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void registerPerms(FEModuleServerInitEvent e)
    {
        PermissionAPI.registerNode(COLORIZE_PERM, DefaultPermissionLevel.ALL, "Permission to colourize signs");
        PermissionAPI.registerNode(EDIT_PERM, DefaultPermissionLevel.ALL, "Permission to edit existing signs");
    }

    /**
     * works the same as the old /colorize command
     * 
     * @param e
     */
    @SubscribeEvent
    public void onSignEdit(SignEditEvent e)
    {
        if (!PermissionAPI.hasPermission(e.editor, COLORIZE_PERM))
        {
            return;
        }
        else
        {
            for (int i = 0; i < e.text.length; i++)
            {
                if (e.text[i].contains("&"))
                {
                    TextComponentString text = new TextComponentString(ChatOutputHandler.formatColors(e.text[i]));
                    ChatOutputHandler.applyFormatting(text.getStyle(), ChatOutputHandler.enumChatFormattings("0123456789AaBbCcDdEeFfKkLlMmNnOoRr"));
                    e.formatted[i] = text;
                }
            }
        }
    }

    /**
     * how to use: First line of the sign MUST BE [command] Second line is the command you want to run Third and fourth
     * lines are arguments to the command.
     */

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getWorld().isRemote)
        {
            return;
        }

        TileEntity te = event.getEntityPlayer().world.getTileEntity(event.getPos());
        if (te != null && te instanceof TileEntitySign)
        {
            if (allowSignEdit && event.getEntityPlayer().isSneaking())
            {
                if(event.getEntityPlayer().getHeldItemMainhand()!= ItemStack.EMPTY)
                {
                    if (PermissionAPI.hasPermission(event.getEntityPlayer(), EDIT_PERM)
                            && PermissionAPI.hasPermission(event.getEntityPlayer(), "fe.protection.use.minecraft.sign")
                            && event.getEntityPlayer().getHeldItemMainhand().getItem().equals(Items.SIGN))
                    {
                        event.getEntityPlayer().openEditSign((TileEntitySign) te);
                        event.setCanceled(true);
                    }
                }

            }



            if (!allowSignCommands)
            {
                return;
            }

            ITextComponent[] signText = ((TileEntitySign) te).signText;
            if (!signText[0].getUnformattedText().equals("[command]"))
            {
                return;
            }

                else
                {
                    String send = signText[1].getUnformattedText() + " " + signText[2].getUnformattedText() + " " + signText[3].getUnformattedText();
                    if (send != null && FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager() != null)
                    {
                        FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(event.getEntityPlayer(), send);
                        event.setCanceled(true);
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
