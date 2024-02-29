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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

@Mixin(Commands.class)
public class MixinCommands
{
    /**
     * @author maximuslotro
     * @reason Need to use custom permission handling to give players usable commands Overwrite method to Check if the sender has permission for the commands
     */
	@Inject(method = "sendCommands(Lnet/minecraft/entity/player/ServerPlayerEntity;)V", at = @At("HEAD"), cancellable = true, require = 1)
    public void sendCommands(ServerPlayerEntity p_197051_1_, CallbackInfo callback)
    {
        final Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> map = Maps.newHashMap();
        final RootCommandNode<ISuggestionProvider> rootcommandnode = new RootCommandNode<>();
        map.put(ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher().getRoot(), rootcommandnode);
        fillUsableCommandsNodesFE(ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher().getRoot(), rootcommandnode,
                p_197051_1_.createCommandSourceStack(), map, "", new Boolean(false));
        p_197051_1_.connection.send(new SCommandListPacket(rootcommandnode));
        callback.cancel();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillUsableCommandsNodesFE(CommandNode<CommandSource> p_197052_1_, CommandNode<ISuggestionProvider> p_197052_2_, CommandSource p_197052_3_,
                                         Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> p_197052_4_, String nodeString, boolean dontChangeNode)
    {
        for (CommandNode<CommandSource> commandnode : p_197052_1_.getChildren())
        {

            String newNode = nodeString.isEmpty() ? commandnode.getUsageText() : nodeString + CommandDispatcher.ARGUMENT_SEPARATOR + commandnode.getUsageText();
            newNode = newNode.replace("<", "").replace(">", "");

            if (checkPerms(newNode.replace(' ', '.'), p_197052_3_))
            {
                //System.out.println("Perm true: command." +newNode.replace(' ', '.'));
                
                ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = (ArgumentBuilder) commandnode.createBuilder();
                argumentbuilder.requires((p_197060_0_) -> true);
                if (argumentbuilder.getCommand() != null)
                {
                    argumentbuilder.executes((p_197053_0_) -> 0);
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
                    fillUsableCommandsNodesFE(commandnode, commandnode1, p_197052_3_, p_197052_4_, newNode, new Boolean(dontChangeNode));
                }
            }
            else {
                //System.out.println("Perm false: command." +newNode.replace(' ', '.'));
            }
        }
    }

    private static boolean checkPerms(String commandNode, CommandSource source1)
    {
        if (!APIRegistry.perms.checkUserPermission(UserIdent.get(source1), "command." + commandNode))
        {
            return false;
        }
        return true;
    }
}