package com.forgeessentials.commands.item;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandBind extends FEcmdModuleCommands
{
    public static final String color = EnumChatFormatting.RESET + "" + EnumChatFormatting.AQUA;

    @Override
    public String getCommandName()
    {
        return "bind";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/bind <left|right|clear> <command[args]> Bind a command to an object.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 0 || !(args[0].equalsIgnoreCase("left") || args[0].equalsIgnoreCase("right") || args[0].equalsIgnoreCase("clear")))
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
        else if (sender.inventory.getCurrentItem() == null)
        {
            throw new TranslatedCommandException("You are not holding a valid item.");
        }
        else
        {
            ItemStack is = sender.inventory.getCurrentItem();
            if (!is.hasTagCompound())
                is.setTagCompound(new NBTTagCompound());
            if (args[0].equalsIgnoreCase("clear"))
            {
                is.getTagCompound().removeTag("FEbinding");
                NBTTagCompound display = is.getTagCompound().getCompoundTag("display");
                display.setTag("Lore", new NBTTagList());
            }
            else
            {
                StringBuilder cmd = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                {
                    cmd.append(args[i] + " ");
                }

                NBTTagCompound nbt = is.getTagCompound().getCompoundTag("FEbinding");
                nbt.setString(args[0].toLowerCase(), cmd.toString().trim());

                NBTTagCompound display = is.getTagCompound().getCompoundTag("display");
                NBTTagList list = display.getTagList("Lore", 9);
                if (list.tagCount() != 0)
                {
                    System.out.println("NOT 0");
                    for (int j = 0; j < list.tagCount(); ++j)
                    {
                        if (list.getCompoundTagAt(j).getString("FEbinding").startsWith(color + args[0].toLowerCase()))
                        {
                            System.out.println("Match found");
                            list.removeTag(j);
                        }
                    }
                }
                list.appendTag(new NBTTagString(color + args[0].toLowerCase() + "> " + cmd));
                display.setTag("Lore", list);

                is.getTagCompound().setTag("display", display);
                is.getTagCompound().setTag("FEbinding", nbt);
            }
            ChatOutputHandler.chatConfirmation(sender, "Command bound to object.");
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "left", "right", "clear");
        }
        return null;
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!(event.entityPlayer instanceof EntityPlayerMP))
            return;
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (stack == null || !stack.getTagCompound().hasKey("FEbinding"))
            return;

        NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("FEbinding");

        String cmd;
        switch (event.action)
        {
        case LEFT_CLICK_BLOCK:
            cmd = nbt.getString("left");
            break;
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            cmd = nbt.getString("right");
            break;
        default:
            return;
        }

        if (cmd.isEmpty())
            return;

        MinecraftServer.getServer().getCommandManager().executeCommand(event.entityPlayer, cmd);
        event.setCanceled(true);
    }
    
}