package com.forgeessentials.commands.item;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.ItemUtil;

public class CommandBind extends ParserCommandBase
{

    private static final String TAG_NAME = "FEbinding";

    public static final String LORE_TEXT_TAG = TextFormatting.RESET.toString() + TextFormatting.AQUA;

    public CommandBind()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "bind";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/bind <left|right>: Bind a command to an item";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".bind";
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/bind <left|right> <command...>: Bind command to an item");
            return;
        }

        arguments.tabComplete("left", "right", "clear");
        String side = arguments.remove().toLowerCase();

        // If sub-command is "clear"
        if (side.equals("clear"))
        {
            if (!arguments.isTabCompletion)
            {
                ItemStack is = arguments.senderPlayer.inventory.getCurrentItem();
                if (is == null)
                    throw new TranslatedCommandException("You are not holding a valid item.");
                NBTTagCompound tag = is.getTagCompound();
                if (tag != null)
                    tag.removeTag(TAG_NAME);
                arguments.confirm("Cleared bound commands from item");
            }
            return;
        }

        // Get correct side
        boolean isLeft = side.equals("left");
        if (!isLeft && !side.equals("right"))
            throw new TranslatedCommandException("Side must be either left or right");

        if (arguments.isEmpty())
        {
            arguments.confirm("/bind " + side + " <command...>: Bind command to an item");
            arguments.confirm("/bind " + side + " none: Clear bound command");
            return;
        }

        ItemStack is = arguments.senderPlayer.inventory.getCurrentItem();
        if (is == null)
            throw new TranslatedCommandException("You are not holding a valid item.");
        NBTTagCompound tag = ItemUtil.getTagCompound(is);
        NBTTagCompound bindTag = ItemUtil.getCompoundTag(tag, TAG_NAME);
        NBTTagCompound display = tag.getCompoundTag("display");

        if (arguments.isTabCompletion)
        {
            arguments.tabCompletion = arguments.server.getTabCompletions(arguments.sender,
                    arguments.toString().startsWith("/") ? arguments.toString() : "/" + arguments.toString(), arguments.sender.getPosition(), false);
            if ("none".startsWith(arguments.peek()))
                arguments.tabCompletion.add(0, "none");
            return;
        }
        if (arguments.peek().equals("none"))
        {
            bindTag.removeTag(side);
            display.setTag("Lore", new NBTTagList());
            arguments.confirm("Cleared " + side + " bound command from item");
        }
        else
        {
            String command = arguments.toString();
            bindTag.setString(side, command);

            String loreStart = LORE_TEXT_TAG + side + "> ";
            NBTTagString loreTag = new NBTTagString(loreStart + command);

            NBTTagList lore = display.getTagList("Lore", 9);
            for (int i = 0; i < lore.tagCount(); ++i)
            {
                if (lore.getStringTagAt(i).startsWith(loreStart))
                {
                    lore.set(i, loreTag);
                    arguments.confirm("Bound command to item");
                    return;
                }
            }
            lore.appendTag(loreTag);
            display.setTag("Lore", lore);
            tag.setTag("display", display);
        }
        arguments.confirm("Bound command to item");
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP))
            return;
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        if (stack == null || stack.getTagCompound() == null || !stack.getTagCompound().hasKey(TAG_NAME))
            return;
        NBTTagCompound nbt = stack.getTagCompound().getCompoundTag(TAG_NAME);

        String command;
        if (event instanceof LeftClickBlock || event instanceof LeftClickEmpty)
            command = nbt.getString("left");
        else if (event instanceof RightClickBlock || event instanceof RightClickEmpty)
            command = nbt.getString("right");
        else
            return;
        if (command == null || command.isEmpty())
            return;

        FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(event.getEntityPlayer(), command);
        event.setCanceled(true);
    }

}