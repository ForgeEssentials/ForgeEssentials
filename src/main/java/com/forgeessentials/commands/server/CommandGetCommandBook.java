package com.forgeessentials.commands.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.PermissionManager;

public class CommandGetCommandBook extends ForgeEssentialsCommandBase
{

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
    public String getPrimaryAlias()
    {
        return "commandbook";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "cmdbook" };
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/commandbook: Get a command book listing all commands.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".commandbook";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {

        if (sender.inventory.hasItemStack(new ItemStack(Items.WRITTEN_BOOK)))
        {
            for (int i = 0; i < sender.inventory.mainInventory.size(); i++)
            {
                ItemStack e = sender.inventory.mainInventory.get(i);
                if (e != ItemStack.EMPTY && e.hasTagCompound() && e.getTagCompound().hasKey("title")
                        && e.getTagCompound().hasKey("author")
                        && e.getTagCompound().getString("title").equals("CommandBook")
                        && e.getTagCompound().getString("author").equals("ForgeEssentials"))
                {
                    sender.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }

        Set<String> pages = new TreeSet<>();
        for (Object cmdObj : server.getCommandManager().getCommands().values())
        {
            ICommand cmd = (ICommand) cmdObj;
            if (!PermissionAPI.hasPermission(sender, PermissionManager.getCommandPermission(cmd)))
                continue;

            Set<String> commands = new HashSet<>();
            commands.add("/" + cmd.getName());

            // Add aliases
            List<?> aliases = cmd.getAliases();
            if (aliases != null && aliases.size() > 0)
            {
                for (Object alias : aliases)
                    commands.add("/" + alias);
            }

            String perm = PermissionManager.getCommandPermission(cmd);
            String text = TextFormatting.GOLD + StringUtils.join(commands, ' ') + '\n' + //
                    (perm != null ? TextFormatting.DARK_RED + perm + "\n\n" : '\n') + TextFormatting.BLACK + cmd.getUsage(sender);
            pages.add(text);
        }

        NBTTagList pagesNbt = new NBTTagList();
        for (String page : pages)
            pagesNbt.appendTag(new NBTTagString(page));

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("author", "ForgeEssentials");
        tag.setString("title", "CommandBook");
        tag.setTag("pages", pagesNbt);

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        is.setTagCompound(tag);
        sender.inventory.addItemStackToInventory(is);
    }

}
