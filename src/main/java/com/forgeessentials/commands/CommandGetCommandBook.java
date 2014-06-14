package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class CommandGetCommandBook extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "getcommandbook";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[]
                { "cmdb", "gcmdb" };
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList pages = new NBTTagList();

        HashMap<String, String> map = new HashMap<String, String>();

        if (sender.inventory.hasItemStack(new ItemStack(Item.writtenBook)))
        {
            int i = 0;
            for (ItemStack e : sender.inventory.mainInventory)
            {
                if (e != null)
                {
                    if (e.hasTagCompound())
                    {
                        if (e.getTagCompound().hasKey("title") && e.getTagCompound().hasKey("author"))
                        {
                            if (e.getTagCompound().getString("title").equals("CommandBook") && e.getTagCompound().getString("author").equals("ForgeEssentials"))
                            {
                                sender.inventory.setInventorySlotContents(i, null);
                            }
                        }
                    }
                }
                i++;
            }
        }

        for (Object cmdObj : MinecraftServer.getServer().getCommandManager().getCommands().values().toArray())
        {
            /*
			 * PAGE FORMAT
			 * =========================
			 * [GOLD] /commandName
			 * [GOLD] aliases
			 * [DARKRED] permission
			 * [Black] usage
			 */

            // Cast to command
            ICommand cmd = (ICommand) cmdObj;

            // Skip commands for which the user has no permission
            if (!cmd.canCommandSenderUseCommand(sender))
            {
                continue;
            }

            // Initialize string for page.
            String text = "";

            // List command aliases.
            if (cmd.getCommandAliases() != null && cmd.getCommandAliases().size() != 0)
            {
                text += EnumChatFormatting.GOLD + joinAliases(cmd.getCommandAliases().toArray()) + "\n\n";
            }
            else
            {
                text += EnumChatFormatting.GOLD + "No aliases.\n\n";
            }

            // Display permission node (If applicable)
            if (cmd instanceof ForgeEssentialsCommandBase)// Was: FEcmdModuleCommands
            {
                text += EnumChatFormatting.DARK_RED + ((ForgeEssentialsCommandBase) cmd).getCommandPerm() + "\n\n";
            }

            // Display usage
            text += EnumChatFormatting.BLACK + cmd.getCommandUsage(sender);

            // Finally post to map
            if (!text.equals(""))
            {
                map.put(EnumChatFormatting.GOLD + "/" + cmd.getCommandName() + "\n" + EnumChatFormatting.RESET, text);
            }
        }

        SortedSet<String> keys = new TreeSet<String>(map.keySet());
        for (String name : keys)
        {
            pages.appendTag(new NBTTagString("", name + map.get(name)));
        }

        tag.setString("author", "ForgeEssentials");
        tag.setString("title", "CommandBook");
        tag.setTag("pages", pages);

        ItemStack is = new ItemStack(Item.writtenBook);
        is.setTagCompound(tag);
        sender.inventory.addItemStackToInventory(is);
    }

    public static String joinAliases(Object[] par0ArrayOfObj)
    {
        StringBuilder var1 = new StringBuilder();

        for (int var2 = 0; var2 < par0ArrayOfObj.length; ++var2)
        {
            String var3 = "/" + par0ArrayOfObj[var2].toString();

            if (var2 > 0)
            {
                var1.append(", ");
            }

            var1.append(var3);
        }

        return var1.toString();
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.GUESTS;
    }

    @Override
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        // TODO Auto-generated method stub
        return "/getcommandbook Get a command book listing all commands.";
    }
}
