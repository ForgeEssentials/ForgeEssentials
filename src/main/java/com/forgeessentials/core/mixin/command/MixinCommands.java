package com.forgeessentials.core.mixin.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Mixin(Commands.class)
public class MixinCommands
{
    /**
     * @author maximuslotro
     * @reason Need to use custom permission handling to give players usable commands Overwrite method to Check if the sender has permission for the commands
     */
    @Overwrite
    public void sendCommands(ServerPlayerEntity p_197051_1_)
    {
        final Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> map = Maps.newHashMap();
        final RootCommandNode<ISuggestionProvider> rootcommandnode = new RootCommandNode<>();
        map.put(ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher().getRoot(), rootcommandnode);
        fillUsableCommandsNodes(ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher().getRoot(), rootcommandnode,
                p_197051_1_.createCommandSourceStack(), map, "", new Boolean(false));
        p_197051_1_.connection.send(new SCommandListPacket(rootcommandnode));
    }

    private void fillUsableCommandsNodes(CommandNode<CommandSource> p_197052_1_, CommandNode<ISuggestionProvider> p_197052_2_, CommandSource p_197052_3_,
            Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> p_197052_4_, String nodeString, boolean dontChangeNode)
    {
        for (CommandNode<CommandSource> commandnode : p_197052_1_.getChildren())
        {

            String newNode = nodeString.isEmpty() ? commandnode.getUsageText() : nodeString + CommandDispatcher.ARGUMENT_SEPARATOR + commandnode.getUsageText();
            newNode = newNode.replace("<", "").replace(">", "");
            // System.out.println("Checking perm: "+newNode.replace(' ', '.'));

            if (checkPerms(newNode.replace(' ', '.'), p_197052_3_))
            {
                ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = (ArgumentBuilder) commandnode.createBuilder();
                argumentbuilder.requires((p_197060_0_) -> {
                    return true;
                });
                if (argumentbuilder.getCommand() != null)
                {
                    argumentbuilder.executes((p_197053_0_) -> {
                        return 0;
                    });
                }

                if (argumentbuilder instanceof RequiredArgumentBuilder)
                {
                    RequiredArgumentBuilder<ISuggestionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder) argumentbuilder;
                    if (requiredargumentbuilder.getSuggestionsProvider() != null)
                    {
                        requiredargumentbuilder.suggests(SuggestionProviders.safelySwap(requiredargumentbuilder.getSuggestionsProvider()));
                    }
                }

                if (argumentbuilder.getRedirect() != null)
                {
                    argumentbuilder.redirect(p_197052_4_.get(argumentbuilder.getRedirect()));
                }

                CommandNode<ISuggestionProvider> commandnode1 = argumentbuilder.build();
                p_197052_4_.put(commandnode, commandnode1);
                p_197052_2_.addChild(commandnode1);
                if (!commandnode.getChildren().isEmpty())
                {
                    fillUsableCommandsNodes(commandnode, commandnode1, p_197052_3_, p_197052_4_, newNode, new Boolean(dontChangeNode));
                }
            }
        }
    }
    // /**
    // * Check if the sender has permission for the command.
    // *
    // * @param command the command
    // * @param sender the sender
    // * @return {@code true} if the sender has permission
    // */
    // @Redirect(
    // method = "fillUsableCommands",//(Lcom/mojang/brigadier/tree/CommandNode;Lcom/mojang/brigadier/tree/CommandNode;Lnet/minecraft/command/CommandSource;Ljava/util/Map;)V",
    // at = @At(
    // value = "INVOKE",
    // target = "Lcom/mojang/brigadier/tree/CommandNode;canUse(Lnet/minecraft/command/CommandSource;)Z",
    // remap=false
    // ),
    // require = 1,
    // remap=true
    // )
    // public boolean canUse(CommandNode<CommandSource> commandNode,CommandSource sender)
    // {
    // return true;
    // //return checkPerms(commandNode, sender);
    // }

    private static boolean checkPerms(String commandNode, CommandSource source1)
    {
        if (!APIRegistry.perms.checkUserPermission(UserIdent.get(source1), "command." + commandNode))
        {
            System.out.println("Restricted perm: " + commandNode);
            return false;
        }
        return true;
        // ICommandSource source = CommandUtils.GetSource(source1);
        // if (source instanceof DoAsCommandSender) {
        // if (!((DoAsCommandSender) source).getIdent().isPlayer()) {
        // if (((DoAsCommandSender) source).getIdent().isNpc()) {
        // return APIRegistry.perms.hasPermission(((DoAsCommandSender) source).getIdent().getGameProfile(), commandNode, null);
        // }
        // else
        // {
        // return true;
        // }
        // } else {
        // return APIRegistry.perms.hasPermission(((DoAsCommandSender) source).getIdent().getGameProfile(), commandNode,new PlayerContext(((DoAsCommandSender)
        // source).getIdent().getPlayer()));
        // }
        // }
        //
        // if (source1.getEntity() instanceof PlayerEntity)
        // {
        // return APIRegistry.perms.hasPermission(((PlayerEntity) source1.getEntity()).getGameProfile(), commandNode, new PlayerContext((PlayerEntity)source1.getEntity()));
        // } else {
        // UserIdent ident = UserIdent.get(source1);
        // return APIRegistry.perms.hasPermission(ident.getGameProfile(), commandNode, null);
        // }
    }
}