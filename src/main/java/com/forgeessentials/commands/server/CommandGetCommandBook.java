package com.forgeessentials.commands.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.PermissionManager;

public class CommandGetCommandBook extends BaseCommand
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
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {

        if (sender.inventory.contains(new ItemStack(Items.WRITTEN_BOOK)))
        {
            for (int i = 0; i < sender.inventory.items.size(); i++)
            {
                ItemStack e = sender.inventory.items.get(i);
                if (e != ItemStack.EMPTY && e.hasTag() && e.getTag().contains("title")
                        && e.getTag().contains("author")
                        && e.getTag().getString("title").equals("CommandBook")
                        && e.getTag().getString("author").equals("ForgeEssentials"))
                {
                    sender.inventory.setItem(i, ItemStack.EMPTY);
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

        ListNBT pagesNbt = new ListNBT();
        for (String page : pages)
            pagesNbt.appendTag(new StringNBT(page));

        CompoundNBT tag = new CompoundNBT();
        tag.putString("author", "ForgeEssentials");
        tag.putString("title", "CommandBook");
        tag.put("pages", pagesNbt);

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        is.setTagCompound(tag);
        sender.inventory.add(is);
    }

}
