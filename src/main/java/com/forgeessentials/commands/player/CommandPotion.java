package com.forgeessentials.commands.player;

import java.util.HashMap;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.PotionArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

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
    public String getPrimaryAlias()
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".potion";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP, "Use potions on others");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("me")
                        .then(Commands.argument("potionID", PotionArgument.effect())
                                .then(Commands.argument("duration", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .then(Commands.argument("amplifier", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                .executes(CommandContext -> execute(CommandContext, "me-amp")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "me-dur")
                                                )
                                        )
                                )
                        )
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("potionID", PotionArgument.effect())
                                .then(Commands.argument("duration", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .then(Commands.argument("amplifier", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                .executes(CommandContext -> execute(CommandContext, "player-amp")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "player-dur")
                                                )
                                        )
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity sender = getServerPlayer(ctx.getSource());
        String[] arg = params.split("-");
        Effect ID;
        int dur = 0;
        int ampl = 0;

        if (arg[1].equalsIgnoreCase("amp"))
        {
            ampl = IntegerArgumentType.getInteger(ctx, "amplifier");
        }


        ID = PotionArgument.getEffect(ctx, "potionID");
        dur = IntegerArgumentType.getInteger(ctx, "duration") * 20;

        EffectInstance eff = new EffectInstance(ID, dur, ampl, false, true, true);
        if (arg[0].equalsIgnoreCase("me"))
        {
            sender.addEffect(eff);
        }
        else if (sender == EntityArgument.getPlayer(ctx, "player"))
        {
            sender.addEffect(eff);
        }
        else if (PermissionAPI.hasPermission(sender, getPermissionNode() + ".others"))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");

            if (!player.hasDisconnected())
            {
                player.addEffect(eff);
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", player.getDisplayName().getString());
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        String[] arg = params.split("-");
        Effect ID;
        int dur = 0;
        int ampl = 0;

        if (arg[1].equalsIgnoreCase("amp"))
        {
            ampl = IntegerArgumentType.getInteger(ctx, "amplifier");
        }
        if (arg[0].equalsIgnoreCase("me"))
        {
            throw new TranslatedCommandException("Cant use console as the target");
        }

        ID = PotionArgument.getEffect(ctx, "potionID");
        dur = IntegerArgumentType.getInteger(ctx, "duration") * 20;
        EffectInstance eff = new EffectInstance(ID, dur, ampl, false, true, true);

        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");

        if (!player.hasDisconnected())
        {
            player.addEffect(eff);
        }
        else
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", player.getDisplayName().getString());
        return Command.SINGLE_SUCCESS;
    }

}
