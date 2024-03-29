package com.forgeessentials.commands.server;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandGetCommandBook extends ForgeEssentialsCommandBuilder
{

    public CommandGetCommandBook(boolean enabled)
    {
        super(enabled);
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
    public @NotNull String getPrimaryAlias()
    {
        return "commandbook";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity sender = getServerPlayer(ctx.getSource());
        if (sender.inventory.contains(new ItemStack(Items.WRITTEN_BOOK)))
        {
            for (int i = 0; i < sender.inventory.items.size(); i++)
            {
                ItemStack e = sender.inventory.items.get(i);
                if (e != ItemStack.EMPTY && e.hasTag() && e.getTag().contains("title") && e.getTag().contains("author")
                        && e.getTag().getString("title").equals("CommandBook")
                        && e.getTag().getString("author").equals("ForgeEssentials"))
                {
                    sender.inventory.setItem(i, ItemStack.EMPTY);
                }
            }
        }

        Set<String> pages = new TreeSet<>();
        CommandDispatcher<CommandSource> dis = ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher();
        Map<CommandNode<CommandSource>, String> map = dis.getSmartUsage(dis.getRoot(), ctx.getSource());
        for (CommandNode<CommandSource> cmdObj : map.keySet())
        {
            if (!cmdObj.canUse(sender.createCommandSourceStack()))
                continue;

            String text = "{\"text\":\" "+ChatOutputHandler.COLOR_FORMAT_CHARACTER+"6/"+cmdObj.getName() + '\n' 
            +ChatOutputHandler.COLOR_FORMAT_CHARACTER+"4 command."+cmdObj.getName()+".*" + '\n' 
            		+ChatOutputHandler.COLOR_FORMAT_CHARACTER+"0 "+ cmdObj.getUsageText()+"\"}";
            pages.add(text);
        }

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);

        ListNBT pagesNbt = new ListNBT();
        for (String page : pages)
            pagesNbt.add(StringNBT.valueOf(page));

        is.addTagElement("author", StringNBT.valueOf("ForgeEssentials"));
        is.addTagElement("title", StringNBT.valueOf("CommandBook"));
        is.addTagElement("pages", pagesNbt);

        sender.inventory.add(is);
        return Command.SINGLE_SUCCESS;
    }

}
