package com.forgeessentials.commands.player;

import java.util.Collection;
import java.util.HashMap;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPotion extends ForgeEssentialsCommandBuilder
{
    public CommandPotion(boolean enabled)
    {
        super(enabled);
    }

    public static HashMap<String, Integer> names;

    static
    {
        names = new HashMap<>();
        names.put("speed", 1);
        names.put("slowness", 2);
        names.put("haste", 3);
        names.put("mining_fatigue", 4);
        names.put("strength", 5);
        names.put("instant_health", 6);
        names.put("instant_damage", 7);
        names.put("jump_boost", 8);
        names.put("nausea", 9);
        names.put("regeneration", 10);
        names.put("resistance", 11);
        names.put("fire_resistance", 12);
        names.put("water_breathing", 13);
        names.put("invisibility", 14);
        names.put("blindness", 15);
        names.put("night_vision", 16);
        names.put("hunger", 17);
        names.put("weakness", 18);
        names.put("poison", 19);
        names.put("wither", 20);
        names.put("health_boost", 21);
        names.put("absorption", 22);
        names.put("saturation", 23);
        names.put("glowing", 24);
        names.put("levitation", 25);
        names.put("luck", 26);
        names.put("unluck", 27);
        names.put("slow_falling", 28);
        names.put("conduit_power", 29);
        names.put("dolphins_grace", 30);
        names.put("bad_omen", 31);
        names.put("hero_of_the_village", 32);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "potion";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(ModuleCommands.PERM + ".potion.others", DefaultPermissionLevel.OP,
                "Use potions on others");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("clear")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("effect", MobEffectArgument.effect())
                                        .executes(CommandContext -> execute(CommandContext, "clear-effect")))
                                .executes(CommandContext -> execute(CommandContext, "clear-target"))))
                .then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("effect", MobEffectArgument.effect()).then(Commands
                                .argument("seconds", IntegerArgumentType.integer(1, 1000000))
                                .then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255))
                                        .then(Commands.argument("hideParticles", BoolArgumentType.bool())
                                                .executes(CommandContext -> execute(CommandContext, "give-part")))
                                        .executes(CommandContext -> execute(CommandContext, "give-amp")))
                                .executes(CommandContext -> execute(CommandContext, "give-sec")))
                                .executes(CommandContext -> execute(CommandContext, "give-target")))));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("clear-target"))
        {
            for (Entity entity : EntityArgument.getEntities(ctx, "targets"))
            {
                if (entity instanceof LivingEntity)
                {
                    if (entity instanceof Player
                            && ((Player) entity).equals(getServerPlayer(ctx.getSource())))
                    {
                        ((LivingEntity) entity).removeAllEffects();
                    }
                    else
                    {
                        if (!hasPermission(getServerPlayer(ctx.getSource()).createCommandSourceStack(), ModuleCommands.PERM + ".potion.others"))
                        {
                            ChatOutputHandler.chatWarning(ctx.getSource(),
                                    Translator.format("You dont have permission to remove effects from %s",
                                            entity.getDisplayName().getString()));
                            continue;
                        }
                        ((LivingEntity) entity).removeAllEffects();
                    }
                }
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Removed all effects from all target(s)");
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("clear-effect"))
        {
            for (Entity entity : EntityArgument.getEntities(ctx, "targets"))
            {
                if (entity instanceof LivingEntity)
                {
                    if (entity instanceof Player
                            && ((Player) entity).equals(getServerPlayer(ctx.getSource())))
                    {
                        ((LivingEntity) entity).removeEffect(MobEffectArgument.getEffect(ctx, "effect"));
                    }
                    else
                    {
                        if (!hasPermission(getServerPlayer(ctx.getSource()).createCommandSourceStack(), ModuleCommands.PERM + ".potion.others"))
                        {
                            ChatOutputHandler.chatWarning(ctx.getSource(),
                                    Translator.format("You dont have permission to remove effects from %s",
                                            entity.getDisplayName().getString()));
                            continue;
                        }
                        ((LivingEntity) entity).removeEffect(MobEffectArgument.getEffect(ctx, "effect"));
                    }
                }
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Removed effect %s from all target(s)",
                            MobEffectArgument.getEffect(ctx, "effect").getDisplayName().getString()));
            return Command.SINGLE_SUCCESS;
        }
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        MobEffect ID = MobEffectArgument.getEffect(ctx, "effect");
        Integer dur = null;
        int ampl = 0;
        boolean hideParticals = true;

        if (params.equals("give-sec"))
        {
            dur = IntegerArgumentType.getInteger(ctx, "seconds");
        }
        if (params.equals("give-amp"))
        {
            dur = IntegerArgumentType.getInteger(ctx, "seconds");
            ampl = IntegerArgumentType.getInteger(ctx, "amplifier");
        }
        if (params.equals("give-part"))
        {
            dur = IntegerArgumentType.getInteger(ctx, "seconds");
            ampl = IntegerArgumentType.getInteger(ctx, "amplifier");
            hideParticals = BoolArgumentType.getBool(ctx, "hideParticles");
        }

        if (dur != null)
        {
            if (!ID.isInstantenous())
            {
                dur = dur * 20;
            }
        }
        else if (ID.isInstantenous())
        {
            dur = 1;
        }
        else
        {
            dur = 600;
        }

        for (Entity entity : targets)
        {
            if (entity instanceof LivingEntity)
            {
                if (entity instanceof Player
                        && ((Player) entity).equals(getServerPlayer(ctx.getSource())))
                {
                    MobEffectInstance effectinstance = new MobEffectInstance(ID, dur, ampl, false, !hideParticals);
                    ((LivingEntity) entity).addEffect(effectinstance);
                }
                else
                {
                    if (!hasPermission(getServerPlayer(ctx.getSource()).createCommandSourceStack(), ModuleCommands.PERM + ".potion.others"))
                    {
                        ChatOutputHandler.chatWarning(ctx.getSource(), Translator.format(
                                "You dont have permission to give effects to %s", entity.getDisplayName().getString()));
                        continue;
                    }
                    MobEffectInstance effectinstance = new MobEffectInstance(ID, dur, ampl, false, !hideParticals);
                    ((LivingEntity) entity).addEffect(effectinstance);
                }
            }
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                Translator.format("Added effect %s to all target(s)", ID.getDisplayName().getString()));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("clear-target"))
        {
            for (Entity entity : EntityArgument.getEntities(ctx, "targets"))
            {
                if (entity instanceof LivingEntity)
                {
                    ((LivingEntity) entity).removeAllEffects();
                }
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Removed all effects from all target(s)");
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("clear-effect"))
        {
            for (Entity entity : EntityArgument.getEntities(ctx, "targets"))
            {
                if (entity instanceof LivingEntity)
                    ((LivingEntity) entity).removeEffect(MobEffectArgument.getEffect(ctx, "effect"));
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Removed effect %s from all target(s)",
                            MobEffectArgument.getEffect(ctx, "effect").getDisplayName().getString()));
            return Command.SINGLE_SUCCESS;
        }
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        MobEffect ID = MobEffectArgument.getEffect(ctx, "effect");
        Integer dur = null;
        int ampl = 0;
        boolean hideParticals = true;

        if (params.equals("give-sec"))
        {
            dur = IntegerArgumentType.getInteger(ctx, "seconds");
        }
        if (params.equals("give-amp"))
        {
            dur = IntegerArgumentType.getInteger(ctx, "seconds");
            ampl = IntegerArgumentType.getInteger(ctx, "amplifier");
        }
        if (params.equals("give-part"))
        {
            dur = IntegerArgumentType.getInteger(ctx, "seconds");
            ampl = IntegerArgumentType.getInteger(ctx, "amplifier");
            hideParticals = BoolArgumentType.getBool(ctx, "hideParticles");
        }

        if (dur != null)
        {
            if (!ID.isInstantenous())
            {
                dur = dur * 20;
            }
        }
        else if (ID.isInstantenous())
        {
            dur = 1;
        }
        else
        {
            dur = 600;
        }

        for (Entity entity : targets)
        {
            if (entity instanceof LivingEntity)
            {
                MobEffectInstance effectinstance = new MobEffectInstance(ID, dur, ampl, false, hideParticals);
                ((LivingEntity) entity).addEffect(effectinstance);
            }
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                Translator.format("Added effect %s to all target(s)", ID.getDisplayName().getString()));
        return Command.SINGLE_SUCCESS;
    }

}
