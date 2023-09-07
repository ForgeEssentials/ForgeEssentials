package com.forgeessentials.signtools;

import java.util.ArrayList;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.world.SignEditEvent;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FEModule(name = "SignTools", parentMod = ForgeEssentials.class, version=ForgeEssentials.CURRENT_MODULE_VERSION)
public class SignToolsModule extends ConfigLoaderBase
{
    private static ForgeConfigSpec SIGN_CONFIG;
    private static final ConfigData data = new ConfigData("SignTools", SIGN_CONFIG, new ForgeConfigSpec.Builder());

    public static final String COLORIZE_PERM = "fe.signs.colorize";
    public static final String EDIT_PERM = "fe.signs.edit";
    private static final String signinteractKey = "signinteract";
    private static final String signeditKey = "signedit";

    private static boolean allowSignCommands, allowSignEdit;

    @SubscribeEvent
    public void registerPerms(FEModuleServerStartingEvent e)
    {
        APIRegistry.scripts.addScriptType(signinteractKey);
        APIRegistry.scripts.addScriptType(signeditKey);
        APIRegistry.perms.registerNode(COLORIZE_PERM, DefaultPermissionLevel.ALL, "Permission to colourize signs");
        APIRegistry.perms.registerNode(EDIT_PERM, DefaultPermissionLevel.ALL, "Permission to edit existing signs");
    }

    /**
     * works the same as the old /colorize command
     * 
     * @param e
     */
    @SubscribeEvent
    public void onSignEdit(SignEditEvent e)
    {
        if (APIRegistry.scripts.runEventScripts(signeditKey, e.editor.createCommandSourceStack(),
                new SignInfo(e.editor.getLevel().dimension().location().toString(), e.pos, e.text, e)))
        {
            e.setCanceled(true);
        }

        if (!APIRegistry.perms.checkPermission(e.editor, COLORIZE_PERM))
        {
            return;
        }
        else
        {
            for (int i = 0; i < e.text.length; i++)
            {
                if (e.text[i].contains("&"))
                {
                	e.setCanceled(true);
                    StringTextComponent text = new StringTextComponent(ChatOutputHandler.formatColors(e.text[i]));
                    e.formatted[i] = text;
                }
            }
        }
    }

    /**
     * how to use: First line of the sign MUST BE [command] Second line is the command you want to run Third and fourth lines are arguments to the command.
     */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        World w = event.getWorld();
        if (w.isClientSide)
        {
            return;
        }
        TileEntity te = w.getBlockEntity(event.getPos());
        if (te instanceof SignTileEntity)
        {
            SignTileEntity sign = ((SignTileEntity) te);
            if (allowSignEdit && event.getPlayer().isCrouching() && event instanceof RightClickBlock)
            {
                if (event.getPlayer().getMainHandItem() == ItemStack.EMPTY)
                {
                    if (APIRegistry.perms.checkPermission(event.getPlayer(), EDIT_PERM)
                            && APIRegistry.perms.checkPermission(event.getPlayer(), "fe.protection.use.minecraft.sign"))
                    {
                        // Convert Formatting back into FE format for easy use
                        ITextComponent[] imessage = ItemUtil.getText(sign);
                        String[] signText = getFormatted(imessage);
                        ITextComponent[] newMessage = new ITextComponent[4];
                        for (int i = 0; i < signText.length; i++)
                        {
                        	newMessage[i] = new StringTextComponent(signText[i]);
                        }
                        ItemUtil.setText(sign, newMessage);
                        sign.setChanged();
                        w.sendBlockUpdated(event.getPos(), w.getBlockState(event.getPos()), w.getBlockState(event.getPos()), 3);

                        ((ServerPlayerEntity) event.getPlayer()).connection.send(sign.getUpdatePacket());
                        ((ServerPlayerEntity) event.getPlayer()).openTextEdit(sign);
                        event.setCanceled(true);
                    }
                }
                return;
            }
            ITextComponent[] imessage = ItemUtil.getText(sign);
            String[] signText = getFormatted(imessage);

            if (APIRegistry.scripts.runEventScripts(signinteractKey, event.getPlayer().createCommandSourceStack(),
                    new SignInfo(event.getPlayer().level.dimension().toString(), event.getPos(), signText, event)))
            {
                event.setCanceled(true);
            }

            if (allowSignCommands && (event instanceof RightClickBlock))
            {
                if (signText[0].equals("[command]"))
                {
                    String send = signText[1] + " " + signText[2] + " " + signText[3];
                    if (send != null && ServerLifecycleHooks.getCurrentServer().getCommands() != null)
                    {
                        ServerLifecycleHooks.getCurrentServer().getCommands()
                                .performCommand(event.getPlayer().createCommandSourceStack(), send);
                        event.setCanceled(true);
                    }
                }
            }

        }

    }

    private String[] getFormatted(ITextComponent[] text)
    {
        String[] out = new String[text.length];
        for (int i = 0; i < text.length; i++)
        {
            out[i] = text[i].getString().replace(ChatOutputHandler.COLOR_FORMAT_CHARACTER, '&');
        }
        return out;
    }

    static ForgeConfigSpec.BooleanValue FEallowSignCommands;
    static ForgeConfigSpec.BooleanValue FEallowSignEdit;

    // this is NOT a permprop because perms are checked against the player anyway

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push("Sign");
        FEallowSignCommands = BUILDER.comment("Allow commands to be run when right clicking signs.")
                .define("allowSignCommands", true);
        FEallowSignEdit = BUILDER
                .comment("Allow players to edit a sign by right clicking on it with a sign item while sneaking.")
                .define("allowSignEditing", true);
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        allowSignCommands = FEallowSignCommands.get();
        allowSignEdit = FEallowSignEdit.get();
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }

    public boolean SIGNS(Item item)
    {
        ArrayList<Item> signs = new ArrayList<>(4);
        signs.add(Items.ACACIA_SIGN);
        signs.add(Items.BIRCH_SIGN);
        signs.add(Items.CRIMSON_SIGN);
        signs.add(Items.DARK_OAK_SIGN);
        signs.add(Items.JUNGLE_SIGN);
        signs.add(Items.OAK_SIGN);
        signs.add(Items.SPRUCE_SIGN);
        signs.add(Items.WARPED_SIGN);
        return signs.contains(item);
    }
}
