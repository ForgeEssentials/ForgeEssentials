package com.forgeessentials.commands.item;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.electronwill.nightconfig.core.io.CharsWrapper.Builder;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.VirtualChest;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * Opens a configurable virtual chest
 */
public class CommandVirtualchest extends BaseCommand implements ConfigurableCommand
{
    public CommandVirtualchest(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    public static int size = 54;
    static ForgeConfigSpec.IntValue FEsize;
    
    public static String name = "Vault 13";
    static ForgeConfigSpec.ConfigValue<String> FEname;

    @Override
    public String getPrimaryAlias()
    {
        return "virtualchest";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "vchest" };
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".virtualchest";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .executes(CommandContext -> execute(CommandContext)
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = getServerPlayer(ctx.getSource());
        if (player.containerMenu != player.inventoryMenu)
        {
            player.doCloseContainer();
        }
        player.nextContainerCounter();

        VirtualChest chest = new VirtualChest(player);
        player.displayGUIChest(chest);
    }
    
    
    @Override
    public void loadConfig(ForgeConfigSpec.Builder BUILDER, String category)
    {
    	BUILDER.push(category);
    	FEsize = BUILDER.comment("1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).").defineInRange("VirtualChestRows", 6, 1, 6);
    	FEname = BUILDER.comment("Don't use special stuff....").define("VirtualChestName", "Vault 13");
    	BUILDER.pop();
    }

    @Override
    public void loadData()
    {
        /* do nothing */
    }

	@Override
	public void bakeConfig(boolean reload) {
		size = FEsize.get() * 9;
		name = FEname.get();
		
	}
}
