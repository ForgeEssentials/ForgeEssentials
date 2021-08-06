package com.forgeessentials.signtools;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fe.event.world.SignEditEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

@FEModule(name = "SignTools", parentMod = ForgeEssentials.class)
public class SignToolsModule extends ConfigLoaderBase
{
    public static final String COLORIZE_PERM = "fe.signs.colorize";
    public static final String EDIT_PERM = "fe.signs.edit";
    public static final String USESIGNCOMMANDS_PERM = "fe.signs.usesigncommands";
    private static final String signinteractKey = "signinteract";
    private static final String signeditKey = "signedit";

    private static boolean allowSignCommands, allowSignEdit;

    @SubscribeEvent
    public void onLoad(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        APIRegistry.scripts.addScriptType(signinteractKey);
        APIRegistry.scripts.addScriptType(signeditKey);
    }

    @SubscribeEvent
    public void registerPerms(FEModuleServerInitEvent e)
    {
        PermissionAPI.registerNode(COLORIZE_PERM, DefaultPermissionLevel.ALL, "Permission to colourize signs");
        PermissionAPI.registerNode(EDIT_PERM, DefaultPermissionLevel.ALL, "Permission to edit existing signs");
        PermissionAPI.registerNode(USESIGNCOMMANDS_PERM, DefaultPermissionLevel.ALL, "Permission to use sign commands");
    }

    /**
     * works the same as the old /colorize command
     * 
     * @param e
     */
    @SubscribeEvent
    public void onSignEdit(SignEditEvent e)
    {
        if (APIRegistry.scripts.runEventScripts(signeditKey, e.editor, new SignInfo(e.editor.dimension, e.pos, e.text, e)))
        {
            e.setCanceled(true);
        }

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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getWorld().isRemote)
        {
            return;
        }

        TileEntity te = event.getEntityPlayer().world.getTileEntity(event.getPos());
        if (te instanceof TileEntitySign)
        {
            TileEntitySign sign = ((TileEntitySign) te);
            if (allowSignEdit && event.getEntityPlayer().isSneaking() && event instanceof RightClickBlock)
            {
                if (event.getEntityPlayer().getHeldItemMainhand() != ItemStack.EMPTY)
                {
                    if (PermissionAPI.hasPermission(event.getEntityPlayer(), EDIT_PERM)
                            && PermissionAPI.hasPermission(event.getEntityPlayer(), "fe.protection.use.minecraft.sign")
                            && event.getEntityPlayer().getHeldItemMainhand().getItem().equals(Items.SIGN))
                    {
                        //Convert Formatting back into FE format for easy use
                        for (int i = 0; i < sign.signText.length; i++)
                        {
                            sign.signText[i] = new TextComponentString(sign.signText[i].getFormattedText().replace(ChatOutputHandler.COLOR_FORMAT_CHARACTER, '&'));
                        }

                        event.getEntityPlayer().openEditSign((TileEntitySign) te);
                        event.setCanceled(true);
                    }
                }

            }

            String[] signText = getUnFormatted(sign.signText);

            if (APIRegistry.scripts.runEventScripts(signinteractKey, event.getEntityPlayer(), new SignInfo(event.getEntityPlayer().dimension, event.getPos(), signText, event)))
            {
                event.setCanceled(true);
            }

            /**
             * how to use: First line of the sign MUST BE [command] Second line is the command you want to run Third and fourth
             * lines are arguments to the command.
             */
            if (allowSignCommands && (event instanceof RightClickBlock))
            {
                // if (signText[0].trim() == "[command]")
                if (signText[0].trim().equalsIgnoreCase("[command]"))
                {
                    if (PermissionAPI.hasPermission(event.getEntityPlayer(), USESIGNCOMMANDS_PERM))
                    {
                        String send = signText[1] + " " + signText[2] + " " + signText[3];
                        if (send != null && FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager() != null)
                        {
                            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(event.getEntityPlayer(), send);
                            event.setCanceled(true);
                        }
                    }
                    else
                        ChatOutputHandler.chatError(event.getEntityPlayer(), Translator.translate("You are not allowed to use sign commands!"));
                }
            }

        }

    }

    private String[] getUnFormatted(ITextComponent[] text)
    {
        String[] out = new String[text.length];
        for (int i = 0; i < text.length; i++) {
            out[i] = text[i].getUnformattedText().replace(ChatOutputHandler.COLOR_FORMAT_CHARACTER, '&');
        }
        return out;
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        // this is NOT a permprop because perms are checked against the player anyway
        allowSignCommands = config.getBoolean("allowSignCommands", "Signs", true, "Allow commands to be run when right clicking signs.");
        allowSignEdit = config.getBoolean("allowSignEditing", "Signs", true, "Allow players to edit a sign by right clicking on it with a sign item while sneaking.");
    }
}
