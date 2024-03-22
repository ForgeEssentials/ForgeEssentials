package com.forgeessentials.perftools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandChunkLoaderList extends ForgeEssentialsCommandBuilder
{
    public CommandChunkLoaderList(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "chunkloaderlist";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("player")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "player"))))
                .then(Commands.literal("modname")
                        .then(Commands.argument("modname", StringArgumentType.string()).suggests(SUGGEST_mods)
                                .executes(CommandContext -> execute(CommandContext, "modname"))))
                .executes(CommandContext -> execute(CommandContext, "all"));
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_mods = (ctx, builder) -> {
        List<String> modList = new ArrayList<>();
        for (String id : ModList.get().applyForEachModContainer(ModContainer::getModId).collect(Collectors.toList()))
        {
            modList.add(id);
        }
        return SharedSuggestionProvider.suggest(modList, builder);
    };

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        String key = "*";
        if (!params.equals("all"))
        {
            if (params.equals("player"))
            {
                key = "p:" + EntityArgument.getPlayer(ctx, "player").getDisplayName().getString();
            }

            if (params.equals("modname"))
            {
            	key = "m:" + StringArgumentType.getString(ctx, "modname");
            }
        }
        list(ctx, key);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        list(ctx, "*");
        return Command.SINGLE_SUCCESS;
    }

    private void list(CommandContext<CommandSourceStack> ctx, String key)
    {
        ChatOutputHandler.chatNotification(ctx.getSource(), "Key= " + key);
        for (ServerLevel i : ServerLifecycleHooks.getCurrentServer().getAllLevels())
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), "Dimension: " + i.dimension().location().toString());
            // list(ctx, i.dimension().location().toString(), key);
        }
        ChatOutputHandler.chatWarning(ctx.getSource(), "This command has not been fully ported");
    }

    /*
     * private void list(CommandContext<CommandSource> ctx, String dim, String key) { ServerWorld world = ServerUtil.getWorldFromString(dim);
     * 
     * HashMultimap<String, Ticket> modTickets = HashMultimap.create(); HashMultimap<String, Ticket> playerTickets = HashMultimap.create();
     * 
     * for (Ticket ticket : ForgeChunkManager.getPersistentChunksFor(world).values()) { if (ticket.isPlayerTicket()) { playerTickets.put(ticket.getPlayerName(), ticket); } else {
     * modTickets.put(ticket.getModId(), ticket); } }
     * 
     * if (modTickets.isEmpty() && playerTickets.isEmpty()) { return; }
     * 
     * if (!key.equals("*")) { ChatOutputHandler.chatNotification(ctx.getSource(), TextFormatting.UNDERLINE + "ChunkLoaders for " + key.split(":", 2)[1] + ":"); }
     * 
     * ChatOutputHandler.chatNotification(ctx.getSource(), "Dim " + world.provider.getDimensionType().getName() + ":");
     * 
     * if (key.startsWith("p:") || key.equals("*")) { for (String username : playerTickets.keySet()) { if (key.replace("p:", "").equalsIgnoreCase(username) || key.equals("*")) { if
     * (key.equals("*")) { ChatOutputHandler.chatNotification(sender, username); }
     * 
     * HashSet<ChunkPos> chunks = new HashSet<ChunkPos>();
     * 
     * for (Ticket ticket : playerTickets.get(username)) { for (Object obj : ticket.getChunkList()) { chunks.add((ChunkPos) obj); } }
     * 
     * for (ChunkPos coords : chunks) { ChatOutputHandler.chatNotification(sender, coords.x + " : " + coords.z); } } } }
     * 
     * if (key.startsWith("m:") || key.equals("*")) { for (String modID : modTickets.keySet()) { if (key.equals("*")) { ChatOutputHandler.chatNotification(sender, modID); }
     * HashSet<ChunkPos> chunks = new HashSet<ChunkPos>();
     * 
     * for (Ticket ticket : playerTickets.get(modID)) { for (Object obj : ticket.getChunkList()) { chunks.add((ChunkPos) obj); } }
     * 
     * for (ChunkPos coords : chunks) { ChatOutputHandler.chatNotification(ctx.getSource(), coords.x + " : " + coords.z); } } } }
     */
    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    /*
     * @Override public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) { if (args.length == 1) { ArrayList<String>
     * options = new ArrayList<String>(); for (String s : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList(). getAvailablePlayerDat()) { options.add(s); } for
     * (ModContainer mod : Loader.instance().getActiveModList()) { options.add(mod.getName()); }
     * 
     * return getListOfStringsMatchingLastWord(args, options); } return null; }
     */
    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

}
