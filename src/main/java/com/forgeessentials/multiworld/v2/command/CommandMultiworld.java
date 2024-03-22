package com.forgeessentials.multiworld.v2.command;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.multiworld.v2.ModuleMultiworldV2;
import com.forgeessentials.multiworld.v2.Multiworld;
import com.forgeessentials.multiworld.v2.utils.MultiworldException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerException.QuestionerStillActiveException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
        		.then(Commands.literal("list")
        				.then(Commands.literal("CurrentMultiworlds")
        						.executes(CommandContext -> execute(CommandContext, "worlds")))
        				.then(Commands.literal("AvailableDimensionSettings")
        						.executes(CommandContext -> execute(CommandContext, "dimensionSettings")))
        				.then(Commands.literal("AvailableBiomeProviders")
        						.executes(CommandContext -> execute(CommandContext, "biomeProviders")))
        				.then(Commands.literal("AvailableChunkGenerators")
        						.executes(CommandContext -> execute(CommandContext, "chunkGenerators")))
        				.then(Commands.literal("AvailableDimensionTypes")
        						.executes(CommandContext -> execute(CommandContext, "dimensionTypes"))))
        		
        		.then(Commands.literal("info")
        				.then(Commands.argument("world", StringArgumentType.word())
        						.suggests(SUGGEST_dims)
        						.executes(CommandContext -> execute(CommandContext, "info"))))
        		.then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
        		.then(Commands.literal("create")
        				.then(Commands.argument("name", StringArgumentType.word())
        						.then(Commands.literal("presets")
        								.then(Commands.literal("Overworld")
        										.executes(CommandContext -> execute(CommandContext, "create:custom:overworld")))
        								.then(Commands.literal("Nether")
        										.executes(CommandContext -> execute(CommandContext, "create:custom:nether")))
        								.then(Commands.literal("End")
        										.executes(CommandContext -> execute(CommandContext, "create:custom:end")))
        								.then(Commands.literal("Superflat")
        										.executes(CommandContext -> execute(CommandContext, "create:custom:superflat")))
        								.then(Commands.literal("FloatingIslands")
        										.executes(CommandContext -> execute(CommandContext, "create:custom:floating")))
        								.then(Commands.literal("Amplified")
        										.executes(CommandContext -> execute(CommandContext, "create:custom:amplified")))
        								.then(Commands.literal("LargeBiomes")
        										.executes(CommandContext -> execute(CommandContext, "create:custom:largeBiome")))
        								)
                				.then(Commands.literal("custom")
                						.then(Commands.argument("chunkGenerator", StringArgumentType.word())
                								.suggests(SUGGEST_chunkgens)
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
                																		.executes(CommandContext -> execute(CommandContext, "create:gen"))
                																		)
                																)
                														)
                												)
                										)
                								)
                						)
        						)

        				)
        		.then(Commands.literal("delete")
        				.then(Commands.argument("world", StringArgumentType.word())
        						.suggests(SUGGEST_dims)
        						.executes(CommandContext -> execute(CommandContext, "delete"))));
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_dims = (ctx, builder) -> {
        return SharedSuggestionProvider.suggest(ModuleMultiworldV2.getMultiworldManager().getDimensionsNames(), builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_dimTypes = (ctx, builder) -> {
    	Set<String> types = new HashSet<>();
    	for(String name : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getDimensionTypes().keySet()) {
    		types.add(name.replace(':', '+'));
    	}
        return SharedSuggestionProvider.suggest(types, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_chunkgens = (ctx, builder) -> {
    	Set<String> types = new HashSet<>();
    	for(String name : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getChunkGenerators()) {
    		types.add(name.replace(':', '+'));
    	}
        return SharedSuggestionProvider.suggest(types, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_biomeTypes = (ctx, builder) -> {
    	Set<String> types = new HashSet<>();
    	for(String name : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getBiomeProviders()) {
    		types.add(name.replace(':', '+'));
    	}
        return SharedSuggestionProvider.suggest(types, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_dimSettings = (ctx, builder) -> {
    	Set<String> types = new HashSet<>();
    	for(String name : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getDimensionSettings().keySet()) {
    		types.add(name.replace(':', '+'));
    	}
        return SharedSuggestionProvider.suggest(types, builder);
    };

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
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
					ChatOutputHandler.chatConfirmation(ctx.getSource(), "#" + world.getInternalName() + " "
							+ world.getName());
				}
				break;
    		case "dimensionSettings":
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Available dimension settings:");
    			for (String type : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getDimensionSettings().keySet()) {
    				ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + type);
				}
				break;
    		case "chunkGenerators":
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Available chunk generators:");
    			for (String type : ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getChunkGenerators()) {
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
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "  DimensionID = %d", world.getInternalID());
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "  DimensionKey = %s", world.getInternalName());
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "  BiomeProvider = %s", world.getBiomeProvider());
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "  ChunkGenerator = %s", world.getChunkGenerator());
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "  DimensionType = %s", world.getDimensionType());
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "  DimensionSettings = %s", world.getDimensionSetting());
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "  Seed = %d", world.getSeed());
    			break;
    		case "delete":
    			String nameToDelete = StringArgumentType.getString(ctx, "world");
    			Multiworld worldToDelete = ModuleMultiworldV2.getMultiworldManager().getMultiworld(nameToDelete);
    			
    			if(worldToDelete==null){
    				ChatOutputHandler.chatError(ctx.getSource(), "Multiworld " + nameToDelete + " does not exist!");
    				return Command.SINGLE_SUCCESS;
    			}
    			if(!ModuleMultiworldV2.isMultiWorld(worldToDelete.getWorldServer())) { 
    				ChatOutputHandler.chatError(ctx.getSource(), "World " + worldToDelete.getName() + " is not a FE multiworld and cannot be deleted!");
    				return Command.SINGLE_SUCCESS;
    	        }
    			try {
					Questioner.addChecked(getServerPlayer(ctx.getSource()),
	                        Translator.format("Delete multiworld %s? This can cause the server to crash in in some cases. The safest way to delete multiworlds is to"
	                        		+ "delete the world folder from world/dimensions/forgeessentials/worldXX and to "
	                        		+ "dlete the world entry [Data/WorldGenSettings/dimensions/forgeessentials:worldXX] from the level.dat file", nameToDelete),
	                        new QuestionerCallback() {
	                            @Override
	                            public void respond(Boolean response)
	                            {
	                                if (response == null)
	                                    ChatOutputHandler.chatError(ctx.getSource(), "Delete request timed out");
	                                else if (!response)
	                                    ChatOutputHandler.chatError(ctx.getSource(), "Declined deleating multiworld");
									else {
										ModuleMultiworldV2.getMultiworldManager().deleteWorld(worldToDelete); 
										ChatOutputHandler.chatConfirmation(ctx.getSource(), "Deleted multiworld " + nameToDelete);
									}
	                            }
	                        }, 20);
				} catch (QuestionerStillActiveException e) {
					ChatOutputHandler.chatError(ctx.getSource(),
                            "Cannot ask question because player is still answering a question. Please wait a moment");
				}
    			break;
    		case "create":
    			if(getServerPlayer(ctx.getSource())==null) {
    				return Command.SINGLE_SUCCESS;
    			}
    			String worldName = StringArgumentType.getString(ctx, "name");
        		Long seed = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getSeed();
        		String biomeProvider = "";
        		String dimensionSettings = "";
        		String chunkgenerator = "";
        		String dimensionType = "";
        		String generatorOptions = "";
        		if(params.split(":")[1].equals("custom")) {
        			if(params.split(":")[2].equals("overworld")) {
        				biomeProvider = "minecraft:overworld";
                		dimensionSettings = "minecraft:overworld";
                		chunkgenerator = "minecraft:noise";
                		dimensionType = "minecraft:overworld";
        			}
        			else if(params.split(":")[2].equals("nether")) {
        				biomeProvider = "minecraft:nether";
                		dimensionSettings = "minecraft:nether";
                		chunkgenerator = "minecraft:noise";
                		dimensionType = "minecraft:the_nether";
        			}
        			else if(params.split(":")[2].equals("end")) {
        				biomeProvider = "minecraft:end";
                		dimensionSettings = "minecraft:end";
                		chunkgenerator = "minecraft:noise";
                		dimensionType = "minecraft:the_end";
        			}
        			else if(params.split(":")[2].equals("superflat")) {
        				biomeProvider = "minecraft:single";
                		dimensionSettings = "minecraft:overworld";
                		chunkgenerator = "minecraft:flat";
                		dimensionType = "minecraft:overworld";
        			}
        			else if(params.split(":")[2].equals("floating")) {
        				biomeProvider = "minecraft:overworld";
                		dimensionSettings = "minecraft:floating_islands";
                		chunkgenerator = "minecraft:noise";
                		dimensionType = "minecraft:overworld";
        			}
        			else if(params.split(":")[2].equals("amplified")) {
        				biomeProvider = "minecraft:overworld";
                		dimensionSettings = "minecraft:amplified";
                		chunkgenerator = "minecraft:noise";
                		dimensionType = "minecraft:overworld";
        			}
        			else if(params.split(":")[2].equals("largeBiome")) {
        				biomeProvider = "minecraft:overworld_large";
                		dimensionSettings = "minecraft:overworld";
                		chunkgenerator = "minecraft:noise";
                		dimensionType = "minecraft:overworld";
        			}
        			
    			}
        		else {
        			dimensionType = StringArgumentType.getString(ctx, "dimensionType").replace('+', ':');
    				if (!StringUtils.startsWith(dimensionType, "minecraft")) {
    					ChatOutputHandler.chatWarning(ctx.getSource(),
    							"You selected a non-vanilla dimensionType, your player's games will think they are in minecraft:overworld when joining the dimension!"
    									+ " This could cause issues with client mods thinking they are in the overworld, while the server will think not. Please refrain from using multiworlds in this configuration!");
    					ChatOutputHandler.chatError(ctx.getSource(),
    							"This is a unsupported and error-prone option! World issues arising while having a dimension using non-vanilla dimensionTypes will be ignored!");
    				}
            		biomeProvider = StringArgumentType.getString(ctx, "biomeProvider").replace('+', ':');
            		dimensionSettings = StringArgumentType.getString(ctx, "dimensionSettings").replace('+', ':');
            		chunkgenerator = StringArgumentType.getString(ctx, "chunkGenerator").replace('+', ':');
    			}
        		if(params.split(":")[1].equals("seed")) {
    				seed = LongArgumentType.getLong(ctx, "seed");
    			}
        		if(params.split(":")[1].equals("gen")) {
    				seed = LongArgumentType.getLong(ctx, "seed");
    				generatorOptions = StringArgumentType.getString(ctx, "generatorOptions");
    			}
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Creating a Multiworld named [%s], chunkGenerator provided by [%s], biomes provided by [%s], with a dimension type of [%s], dimension settings set to [%s], generator options set to [%s] and the seed set to [%s]",worldName,chunkgenerator,biomeProvider,dimensionType,dimensionSettings,generatorOptions,seed);
				Multiworld worldNew = new Multiworld(worldName, biomeProvider, chunkgenerator, dimensionType, dimensionSettings,
						seed, generatorOptions);
				try {
					Questioner.addChecked(getServerPlayer(ctx.getSource()),
	                        Translator.format("Create new multiworld %s?", worldName),
	                        new QuestionerCallback() {
	                            @Override
	                            public void respond(Boolean response)
	                            {
	                                if (response == null)
	                                    ChatOutputHandler.chatError(ctx.getSource(), "Create request timed out");
	                                else if (!response)
	                                    ChatOutputHandler.chatError(ctx.getSource(), "Declined making new multiworld");
	                                else
	                                	try {
	                    					ModuleMultiworldV2.getMultiworldManager().addWorld(worldNew);
	                    					if (getServerPlayer(ctx.getSource()) != null) {
	                    						worldNew.teleport(getServerPlayer(ctx.getSource()), true);
	                    					}
	                    				} catch (MultiworldException e) {
	                    					ChatOutputHandler.chatError(ctx.getSource(), e.type.error);
	                    					throw new CommandRuntimeException(new TextComponent(e.type.error));
	                    				}
	                            }
	                        }, 20);
				} catch (QuestionerStillActiveException e) {
					ChatOutputHandler.chatError(ctx.getSource(),
                            "Cannot ask question because player is still answering a question. Please wait a moment");
				}
				break;
    	}
        return Command.SINGLE_SUCCESS;
    }
}
