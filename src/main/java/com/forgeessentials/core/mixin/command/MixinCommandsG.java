package com.forgeessentials.core.mixin.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;

@Mixin(Commands.class)
public class MixinCommandsG<S>
{
    @Redirect(method = "performPrefixedCommand", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;parse(Ljava/lang/String;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;", remap = false))
    public ParseResults<S> performPrefixedCommand(CommandDispatcher instance, String command, S source)
    {
        if (source instanceof CommandSourceStack && ((CommandSourceStack) source).getEntity() != null)
        {
            source = (S) new CommandSourceStack(((CommandSourceStack) source).getEntity(), ((CommandSourceStack) source).getPosition(),
                    ((CommandSourceStack) source).getRotation(),
                    ((CommandSourceStack) source).getLevel(), 4, ((CommandSourceStack) source).getTextName(),
                    ((CommandSourceStack) source).getDisplayName(), ((CommandSourceStack) source).getServer(), ((CommandSourceStack) source).getEntity());
        }
        return instance.parse(command, source);
    }
}