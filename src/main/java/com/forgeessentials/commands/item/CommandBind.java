package com.forgeessentials.commands.item;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandBind extends ForgeEssentialsCommandBuilder
{

    public CommandBind(boolean enabled)
    {
        super(enabled);
    }

    private static final String TAG_NAME = "FEbinding";

    public static final String LORE_TEXT_TAG = ChatFormatting.RESET.toString() + ChatFormatting.AQUA;

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "bind";
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("left")
                        .then(Commands.literal("command")
                                .then(Commands.argument("command", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "left-set"))))
                        .then(Commands.literal("none").executes(CommandContext -> execute(CommandContext, "left-none")))
                        .executes(CommandContext -> execute(CommandContext, "left-help")))
                .then(Commands.literal("right")
                        .then(Commands.literal("command")
                                .then(Commands.argument("command", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "right-set"))))
                        .then(Commands.literal("none")
                                .executes(CommandContext -> execute(CommandContext, "right-none")))
                        .executes(CommandContext -> execute(CommandContext, "right-help")))
                .then(Commands.literal("clear").executes(CommandContext -> execute(CommandContext, "clear-all")))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/bind <left|right> <command...>: Bind command to an item");
            return Command.SINGLE_SUCCESS;
        }

        String side = params.split("-")[0];
        String option = params.split("-")[1];
        // If sub-command is "clear"
        if (side.equals("clear"))
        {
            ItemStack is = ((Player) ctx.getSource().getEntity()).getMainHandItem();
            if (is == ItemStack.EMPTY)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "You are not holding a valid item.");
                return Command.SINGLE_SUCCESS;
            }
            CompoundTag tag = is.getTag();
            if (tag != null)
                tag.remove(TAG_NAME);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cleared bound commands from item");
            return Command.SINGLE_SUCCESS;
        }

        if (option.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("/bind " + side + " <command...>: Bind command to an item"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("/bind " + side + " none: Clear bound command"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack is = ((Player) ctx.getSource().getEntity()).getMainHandItem();
        if (is == ItemStack.EMPTY)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You are not holding a valid item.");
            return Command.SINGLE_SUCCESS;
        }
        CompoundTag tag = ItemUtil.getTagCompound(is);
        CompoundTag bindTag = ItemUtil.getCompoundTag(tag, TAG_NAME);
        CompoundTag display = tag.getCompound("display");

        if (option.equals("none"))
        {
            bindTag.remove(side);
            display.put("Lore", new ListTag());
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Cleared " + side + " bound command from item"));
            return Command.SINGLE_SUCCESS;
        }
        else
        {
            String command = StringArgumentType.getString(ctx, "command");
            bindTag.putString(side, command);

            String loreStart = LORE_TEXT_TAG + side + "> ";
            StringTag loreTag = StringTag.valueOf(loreStart + command);

            ListTag lore = display.getList("Lore", 9);
            for (int i = 0; i < lore.size(); ++i)
            {
                if (lore.getString(i).startsWith(loreStart))
                {
                    lore.set(i, loreTag);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Bound command to item");
                }
            }
            lore.add(loreTag);
            display.put("Lore", lore);
            tag.put("display", display);
            // is.setTag(tag);
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Bound command to item");
        return Command.SINGLE_SUCCESS;
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!(event.getEntity() instanceof ServerPlayer))
            return;
        ItemStack stack = event.getPlayer().getMainHandItem();
        if (stack == ItemStack.EMPTY || stack.getTag() == null || !stack.getTag().contains(TAG_NAME))
            return;
        CompoundTag nbt = stack.getTag().getCompound(TAG_NAME);

        String command;
        if (event instanceof LeftClickBlock)
            command = nbt.getString("left");
        else if (event instanceof RightClickBlock || event instanceof RightClickItem)
            command = nbt.getString("right");
        else
            return;
        if (command == null || command.isEmpty())
            return;

        ServerLifecycleHooks.getCurrentServer().getCommands()
                .performCommand(event.getPlayer().createCommandSourceStack(), command);
        event.setCanceled(true);
    }
}