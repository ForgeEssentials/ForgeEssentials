package com.forgeessentials.multiworld.v2.command;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.multiworld.v2.ModuleMultiworldV2;
import com.forgeessentials.multiworld.v2.Multiworld;
import com.forgeessentials.multiworld.v2.MultiworldException;
import com.forgeessentials.multiworld.v2.WorldServerMultiworld;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

/**
 * @author maximustheminer
 */
public class CommandMultiworld extends ForgeEssentialsCommandBuilder
{

    public CommandMultiworld(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "mw";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
        		.then(Commands.literal("list")
        				.then(Commands.literal("worlds")
        						.executes(CommandContext -> execute(CommandContext, "worlds")))
        				.then(Commands.literal("dimensionSettings")
        						.executes(CommandContext -> execute(CommandContext, "dimensionSettings")))
        				.then(Commands.literal("biomeProviders")
        						.executes(CommandContext -> execute(CommandContext, "biomeProviders")))
        				.then(Commands.literal("dimensionTypes")
        						.executes(CommandContext -> execute(CommandContext, "dimensionTypes"))))
        		.then(Commands.literal("info")
        				.then(Commands.argument("world", StringArgumentType.word())
        						.suggests(SUGGEST_dims)
        						.executes(CommandContext -> execute(CommandContext, "info"))))
        		.then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
        		.then(Commands.literal("create")
        				.then(Commands.argument("name", StringArgumentType.word())
        						.then(Commands.argument("biomeProvider", StringArgumentType.word())
        								.suggests(SUGGEST_biomeTypes)
        								.then(Commands.argument("dimensionType", StringArgumentType.word())
        										.suggests(SUGGEST_dimTypes)
        										.then(Commands.argument("dimensionSettings", StringArgumentType.word())
                										.suggests(SUGGEST_dimSettings)
                										.executes(CommandContext -> execute(CommandContext, "create:generic"))
                										.then(Commands.argument("seed", LongArgumentType.longArg())
                												.executes(CommandContext -> execute(CommandContext, "create:seed"))
                												.then(Commands.argument("generatorOptions", StringArgumentType.word())
                														.executes(CommandContext -> execute(CommandContext, "create:gen")))))))))
        		.then(Commands.literal("delete")
        				.then(Commands.argument("world", StringArgumentType.word())
        						.suggests(SUGGEST_dims)
        						.executes(CommandContext -> execute(CommandContext, "delete"))));
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_dims = (ctx, builder) -> {
        return ISuggestionProvider.suggest(ModuleMultiworldV2.getMultiworldManager().getDimensionsNames(), builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_dimTypes = (ctx, builder) -> {
    	Set<String> types = new HashSet<>();
    	for(String name : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getDimensionTypes().keySet()) {
    		types.add(name.replace(':', '_'));
    	}
        return ISuggestionProvider.suggest(types, builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_biomeTypes = (ctx, builder) -> {
        return ISuggestionProvider.suggest(ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getBiomeProviders(), builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_dimSettings = (ctx, builder) -> {
    	Set<String> types = new HashSet<>();
    	for(String name : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getDimensionSettings().keySet()) {
    		types.add(name.replace(':', '_'));
    	}
        return ISuggestionProvider.suggest(types, builder);
    };

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
    	switch(params.split(":")[0]) {
    		case "help":
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Multiworld usage:");
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/mw create: Create a new world");
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/mw info <world>: Show world info");
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/mw delete <world>: Delete a world");
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/mw list [worlds|providers|worldtypes]");
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/mw gamerule <world>: Get and set world gamerules");
    			break;
    		case "worlds":
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Available worlds:");
				for (Multiworld world : ModuleMultiworldV2.getMultiworldManager().getWorlds()) {
					ChatOutputHandler.chatConfirmation(ctx.getSource(), "#" + world.getInternalID() + " "
							+ world.getName() + ": " + world.getBiomeProvider());
				}
				break;
    		case "dimensionSettings":
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Available dimension settings:");
    			for (String type : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getDimensionSettings().keySet()) {
    				ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + type);
				}
				break;
    		case "biomeProviders":
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Available biome providers:");
    			for (String type : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getBiomeProviders()) {
    				ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + type);
				}
				break;
    		case "dimensionTypes":
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Available dimension types:");
    			for (String type : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getDimensionTypes().keySet()) {
    				ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + type);
				}
				break;
    		case "info":
    			String name = StringArgumentType.getString(ctx, "world");
    			Multiworld world = ModuleMultiworldV2.getMultiworldManager().getMultiworld(name);
    			if(world==null){
    				ChatOutputHandler.chatError(ctx.getSource(), "Multiworld " + name + " does not exist!");
    				return Command.SINGLE_SUCCESS;
    			}
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Multiworld %s:", world.getName());
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "  DimID = %d", world.getInternalID());
    			break;
    		case "delete":
    			String name1 = StringArgumentType.getString(ctx, "world");
    			Multiworld world1 = ModuleMultiworldV2.getMultiworldManager().getMultiworld(name1);
    			
    			if(world1==null){
    				ChatOutputHandler.chatError(ctx.getSource(), "Multiworld " + name1 + " does not exist!");
    				return Command.SINGLE_SUCCESS;
    			}
    			if(!(world1.getWorldServer() instanceof WorldServerMultiworld)) { 
    				ChatOutputHandler.chatError(ctx.getSource(), "World " + world1.getName() + " is not a FE multiworld and cannot be deleted!");
    				return Command.SINGLE_SUCCESS;
    	        }
    			ModuleMultiworldV2.getMultiworldManager().deleteWorld(world1); 
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Deleted multiworld " + world1.getName());
    			break;
    		case "create":
        		Long seed = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getSeed();
        		String generatorOptions = "";
        		String dimensionType = StringArgumentType.getString(ctx, "dimensionType").replace('_', ':');
        		String biomeProvider = StringArgumentType.getString(ctx, "biomeProvider");
        		String dimensionSettings = StringArgumentType.getString(ctx, "dimensionSettings").replace('_', ':');
        		String name2 = StringArgumentType.getString(ctx, "name");
    			if(params.split(":")[1].equals("seed")) {
    				seed = LongArgumentType.getLong(ctx, "seed");
    			}
    			if(params.split(":")[1].equals("gen")) {
    				seed = LongArgumentType.getLong(ctx, "seed");
    				generatorOptions = StringArgumentType.getString(ctx, "generatorOptions");
    			}
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Creating a Multiworld named [%s], biomes provided by [%s], with a dimension type of [%s], dimension settings set to [%s], generator options set to [%s] and the seed set to [%s]",name2,biomeProvider,dimensionType,dimensionSettings,generatorOptions,seed);
				Multiworld worldNew = new Multiworld(name2, biomeProvider, dimensionType, dimensionSettings,
						seed, generatorOptions);
				try {
					ModuleMultiworldV2.getMultiworldManager().addWorld(worldNew);
					if (getServerPlayer(ctx.getSource()) != null) {
						worldNew.teleport(getServerPlayer(ctx.getSource()), true);
					}
				} catch (MultiworldException e) {
					throw new CommandException(new StringTextComponent(e.type.error));
				}
				break;
    	}
        return Command.SINGLE_SUCCESS;
    }
}
