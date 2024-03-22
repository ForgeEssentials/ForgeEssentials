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

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer sender = getServerPlayer(ctx.getSource());
        if (sender.getInventory().contains(new ItemStack(Items.WRITTEN_BOOK)))
        {
            for (int i = 0; i < sender.getInventory().items.size(); i++)
            {
                ItemStack e = sender.getInventory().items.get(i);
                if (e != ItemStack.EMPTY && e.hasTag() && e.getTag().contains("title") && e.getTag().contains("author")
                        && e.getTag().getString("title").equals("CommandBook")
                        && e.getTag().getString("author").equals("ForgeEssentials"))
                {
                    sender.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }

        Set<String> pages = new TreeSet<>();
        CommandDispatcher<CommandSourceStack> dis = ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher();
        Map<CommandNode<CommandSourceStack>, String> map = dis.getSmartUsage(dis.getRoot(), ctx.getSource());
        for (CommandNode<CommandSourceStack> cmdObj : map.keySet())
        {
            if (!cmdObj.canUse(sender.createCommandSourceStack()))
                continue;

            String text = "{\"text\":\" "+ChatOutputHandler.COLOR_FORMAT_CHARACTER+"6/"+cmdObj.getName() + '\n' 
            +ChatOutputHandler.COLOR_FORMAT_CHARACTER+"4 command."+cmdObj.getName()+".*" + '\n' 
            		+ChatOutputHandler.COLOR_FORMAT_CHARACTER+"0 "+ cmdObj.getUsageText()+"\"}";
            pages.add(text);
        }

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);

        ListTag pagesNbt = new ListTag();
        for (String page : pages)
            pagesNbt.add(StringTag.valueOf(page));

        is.addTagElement("author", StringTag.valueOf("ForgeEssentials"));
        is.addTagElement("title", StringTag.valueOf("CommandBook"));
        is.addTagElement("pages", pagesNbt);

        sender.getInventory().add(is);
        return Command.SINGLE_SUCCESS;
    }

}
