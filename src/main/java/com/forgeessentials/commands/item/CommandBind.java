package com.forgeessentials.commands.item;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
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
                ItemStack is = arguments.senderPlayer.inventory.getSelected();
                if (is == ItemStack.EMPTY)
                    throw new TranslatedCommandException("You are not holding a valid item.");
                CompoundNBT tag = is.getTag();
                if (tag != null)
                    tag.remove(TAG_NAME);
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

        ItemStack is = arguments.senderPlayer.inventory.getSelected();
        if (is == ItemStack.EMPTY)
            throw new TranslatedCommandException("You are not holding a valid item.");
        CompoundNBT tag = ItemUtil.getTagCompound(is);
        CompoundNBT bindTag = ItemUtil.getCompoundTag(tag, TAG_NAME);
        CompoundNBT display = tag.getCompound("display");

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
            bindTag.remove(side);
            display.put("Lore", new ListNBT());
            arguments.confirm("Cleared " + side + " bound command from item");
        }
        else
        {
            String command = arguments.toString();
            bindTag.putString(side, command);

            String loreStart = LORE_TEXT_TAG + side + "> ";
            StringNBT loreTag = StringNBT.valueOf(loreStart + command);

            ListNBT lore = display.getList("Lore", 9);
            for (int i = 0; i < lore.size(); ++i)
            {
                if (lore.getString(i).startsWith(loreStart))
                {
                    lore.set(i, loreTag);
                    arguments.confirm("Bound command to item");
                    return;
                }
            }
            lore.add(loreTag);
            display.put("Lore", lore);
            tag.put("display", display);
        }
        arguments.confirm("Bound command to item");
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!(event.getEntity() instanceof ServerPlayerEntity))
            return;
        ItemStack stack = event.getPlayer().getMainHandItem();
        if (stack == ItemStack.EMPTY || stack.getTag() == null || !stack.getTag().contains(TAG_NAME))
            return;
        CompoundNBT nbt = stack.getTag().getCompound(TAG_NAME);

        String command;
        if (event instanceof LeftClickBlock || event instanceof LeftClickEmpty)
            command = nbt.getString("left");
        else if (event instanceof RightClickBlock || event instanceof RightClickEmpty)
            command = nbt.getString("right");
        else
            return;
        if (command == null || command.isEmpty())
            return;
        
        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(event.getPlayer().createCommandSourceStack(), command);
        event.setCanceled(true);
    }

}