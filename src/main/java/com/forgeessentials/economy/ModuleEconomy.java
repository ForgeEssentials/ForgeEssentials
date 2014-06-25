package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.network.FEServerPacketHandler;
import com.forgeessentials.economy.commands.*;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;

/**
 * Call the WalletHandler class when working with Economy
 */
@FEModule(name = "Economy", parentMod = ForgeEssentials.class, configClass = ConfigEconomy.class)
public class ModuleEconomy {
    @FEModule.Config
    public static ConfigEconomy config;

    @FEModule.ModuleDir
    public static File moduleDir;

    public static int startbuget;

    @FEModule.Init
    public void load(FEModuleInitEvent e)
    {
        APIRegistry.wallet = new WalletHandler();
        FMLCommonHandler.instance().bus().register(APIRegistry.wallet);
        FEServerPacketHandler.registerPacket(4, PacketEconomy.class);
    }

    @FEModule.ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        e.registerServerCommand(new CommandAddToWallet());
        e.registerServerCommand(new CommandRemoveWallet());
        e.registerServerCommand(new CommandGetWallet());
        e.registerServerCommand(new CommandSetWallet());
        e.registerServerCommand(new CommandPay());
        e.registerServerCommand(new CommandPaidCommand());
        e.registerServerCommand(new CommandSellCommand());
        e.registerServerCommand(new CommandMoney());
    }
}
