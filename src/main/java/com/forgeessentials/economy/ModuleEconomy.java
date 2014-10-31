package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.economy.commands.CommandAddToWallet;
import com.forgeessentials.economy.commands.CommandGetWallet;
import com.forgeessentials.economy.commands.CommandMoney;
import com.forgeessentials.economy.commands.CommandPaidCommand;
import com.forgeessentials.economy.commands.CommandPay;
import com.forgeessentials.economy.commands.CommandRemoveWallet;
import com.forgeessentials.economy.commands.CommandSellCommand;
import com.forgeessentials.economy.commands.CommandSetWallet;
import com.forgeessentials.economy.commands.plots.CommandBuyPlot;
import com.forgeessentials.economy.commands.plots.CommandListPlot;
import com.forgeessentials.economy.commands.plots.CommandRemovePlot;
import com.forgeessentials.economy.commands.plots.CommandSellPlot;
import com.forgeessentials.economy.commands.plots.CommandSetPlot;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

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

    public static int startbudget;

    public static int psfPrice;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        APIRegistry.wallet = new WalletHandler();
        FMLCommonHandler.instance().bus().register(APIRegistry.wallet);
        FunctionHelper.netHandler.registerMessage(S4PacketEconomy.class, S4PacketEconomy.class, 4, Side.CLIENT);

    }

    @SubscribeEvent
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
        e.registerServerCommand(new CommandBuyPlot());
        e.registerServerCommand(new CommandRemovePlot());
        e.registerServerCommand(new CommandSellPlot());
        e.registerServerCommand(new CommandSetPlot());
        e.registerServerCommand(new CommandListPlot());
        PlotManager.load();
    }

    @SubscribeEvent
    public void serverStop(FEModuleServerStopEvent e)
    {
        PlotManager.save();
    }
}
