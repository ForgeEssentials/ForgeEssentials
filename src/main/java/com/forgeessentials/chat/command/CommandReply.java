package com.forgeessentials.chat.command;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandReply extends ForgeEssentialsCommandBuilder
{

    public CommandReply(boolean enabled)
    {
        super(enabled);
    }

    public static Map<PlayerEntity, WeakReference<PlayerEntity>> replyMap = new WeakHashMap<>();

    public static void messageSent(PlayerEntity argFrom, PlayerEntity argTo)
    {
        replyMap.put(argTo, new WeakReference<PlayerEntity>(argFrom));
    }

    public static PlayerEntity getReplyTarget(PlayerEntity sender)
    {
        WeakReference<PlayerEntity> replyTarget = replyMap.get(sender);
        if (replyTarget == null)
            return null;
        return replyTarget.get();
    }

    /* ------------------------------------------------------------ */

    @Override
    public String getPrimaryAlias()
    {
        return "reply";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "r" };
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleChat.PERM + ".reply";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(CommandContext -> execute(CommandContext, "blank")
                                )
                        );
    }
    
    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        PlayerEntity target = getReplyTarget(getServerPlayer(ctx.getSource()));
        if (target == null){
            ChatOutputHandler.chatError(ctx.getSource(), "No reply target found");
            return Command.SINGLE_SUCCESS;
        }
        if (target.equals(getServerPlayer(ctx.getSource()))){
            ChatOutputHandler.chatError(ctx.getSource(), "You can't be the recipient");
            return Command.SINGLE_SUCCESS;
        }
        TextComponent message = new StringTextComponent(StringArgumentType.getString(ctx, "message"));
        ModuleChat.tell(ctx.getSource(), message, target.createCommandSourceStack());
        return Command.SINGLE_SUCCESS;
    }
}
