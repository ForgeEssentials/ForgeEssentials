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
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

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
    public String getCommandName()
    {
        return "getcommandbook";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "cmdb", "gcmdb" };
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {

        if (sender.inventory.hasItemStack(new ItemStack(Items.WRITTEN_BOOK)))
        {
            for (int i = 0; i < sender.inventory.mainInventory.length; i++)
            {
                ItemStack e = sender.inventory.mainInventory[i];
                if (e != null && e.hasTagCompound() && e.getTagCompound().hasKey("title") && e.getTagCompound().hasKey("author")
                        && e.getTagCompound().getString("title").equals("CommandBook") && e.getTagCompound().getString("author").equals("ForgeEssentials"))
                {
                    sender.inventory.setInventorySlotContents(i, null);
                }
            }
        }

        Set<String> pages = new TreeSet<String>();
        for (Object cmdObj : server.getCommandManager().getCommands().values())
        {
            ICommand cmd = (ICommand) cmdObj;
            if (!PermissionManager.checkPermission(sender, cmd))
                continue;

            Set<String> commands = new HashSet<>();
            commands.add("/" + cmd.getCommandName());

            // Add aliases
            List<?> aliases = cmd.getCommandAliases();
            if (aliases != null && aliases.size() > 0)
            {
                for (Object alias : aliases)
                    commands.add("/" + alias);
            }

            String perm = PermissionManager.getCommandPermission(cmd);
            String text = TextFormatting.GOLD + StringUtils.join(commands, ' ') + '\n' + //
                    (perm != null ? TextFormatting.DARK_RED + perm + "\n\n" : '\n') + TextFormatting.BLACK + cmd.getCommandUsage(sender);
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

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/getcommandbook Get a command book listing all commands.";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + "." + getCommandName();
    }

}
