package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.ForgeEssentials.api.FEChatFormatCodes;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandBind extends FEcmdModuleCommands
{
    public static final String  color = FEChatFormatCodes.RESET + "" + FEChatFormatCodes.AQUA;
    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par1)
    {
        return false;
    }
    
    @Override
    public String getCommandName()
    {
        return "bind";
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0 || !(args[0].equalsIgnoreCase("left") || args[0].equalsIgnoreCase("right") || args[0].equalsIgnoreCase("clear")))
        {
            OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
        }
        else if (sender.inventory.getCurrentItem() == null)
        {
            OutputHandler.chatError(sender, Localization.get("message.error.noItemPlayer"));
        }
        else
        {
            ItemStack is = sender.inventory.getCurrentItem();
            if (!is.hasTagCompound()) is.stackTagCompound = new NBTTagCompound();
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
                    cmd.append(args[i] + " ");
                
                NBTTagCompound nbt = is.getTagCompound().getCompoundTag("FEbinding");
                nbt.setString(args[0].toLowerCase(), cmd.toString().trim());
                
                NBTTagCompound display = is.getTagCompound().getCompoundTag("display");
                NBTTagList list = display.getTagList("Lore");
                if (list.tagCount() != 0)
                {
                    System.out.println("NOT 0");
                    for (int j = 0; j < list.tagCount(); ++j)
                    {
                        if (((NBTTagString)list.tagAt(j)).data.startsWith(color + args[0].toLowerCase()))
                        {
                            System.out.println("Macht found");
                            list.removeTag(j);
                        }
                    }
                }
                list.appendTag(new NBTTagString("list", color + args[0].toLowerCase() + "> " + cmd));
                display.setTag("Lore", list);
                
                is.getTagCompound().setCompoundTag("display", display);
                is.getTagCompound().setCompoundTag("FEbinding", nbt);
            }
            OutputHandler.chatConfirmation(sender, Localization.get("command.bind.bound"));
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "left", "right", "clear");
        return null;
    }

    @Override
    public String getCommandPerm()
    {
        return "ForgeEssentials.BasicCommands." + getCommandName();
    }
}