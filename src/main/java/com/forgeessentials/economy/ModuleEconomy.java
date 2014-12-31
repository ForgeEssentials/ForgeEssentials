package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
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
import com.forgeessentials.economy.commands.plots.CommandSetPlot;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Call the WalletHandler class when working with Economy
 */
@FEModule(name = "Economy", parentMod = ForgeEssentials.class)
public class ModuleEconomy extends ConfigLoaderBase {

    public static final String CONFIG_CAT = "Economy";
    public static final String PICKUP_XP_PERM = "fe.economy.convertXP";
    public static String currencySingular;
    public static String currencyPlural;
    
    public static int startbudget;

    public static int psfPrice;

    public static List<Offer<ItemStack>> offers = new ArrayList<>();
    
    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        FunctionHelper.netHandler.registerMessage(S4PacketEconomy.class, S4PacketEconomy.class, 4, Side.CLIENT);
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        APIRegistry.wallet = new WalletHandler();
        FMLCommonHandler.instance().bus().register(APIRegistry.wallet);
        new EconEventHandler();
        ForgeEssentials.getConfigManager().registerLoader("ItemTables", new ItemTables());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        FunctionHelper.registerServerCommand(new CommandAddToWallet());
        FunctionHelper.registerServerCommand(new CommandRemoveWallet());
        FunctionHelper.registerServerCommand(new CommandGetWallet());
        FunctionHelper.registerServerCommand(new CommandSetWallet());
        FunctionHelper.registerServerCommand(new CommandPay());
        FunctionHelper.registerServerCommand(new CommandPaidCommand());
        FunctionHelper.registerServerCommand(new CommandSellCommand());
        FunctionHelper.registerServerCommand(new CommandMoney());

        FunctionHelper.registerServerCommand(new CommandBuyPlot());
        FunctionHelper.registerServerCommand(new CommandRemovePlot());
        FunctionHelper.registerServerCommand(new CommandSetPlot());
        FunctionHelper.registerServerCommand(new CommandListPlot());

        PermissionsManager.registerPermission(PICKUP_XP_PERM, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission("fe.economy.plots.set.free", RegisteredPermValue.OP);
    }

    @SubscribeEvent
    public void serverStop(FEModuleServerStopEvent e)
    {
        APIRegistry.wallet.save();
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        currencySingular = config.get(CONFIG_CAT, "currencySingular", "gold", "Name of singular currency unit").getString();
        currencyPlural = config.get(CONFIG_CAT, "currencyPlural", "gold", "Name of plural currency unit").getString();
        startbudget = config.get(CONFIG_CAT, "startbudget", 100, "Starting amount of money for players.").getInt();
        psfPrice = config.get(CONFIG_CAT, "multiplier", 1, "Multiplier for automatic plot valuation.").getInt();
        EconEventHandler.convertXPDrops = config.get(CONFIG_CAT, "convertXPDrops", false, "Allow players picking up XP orbs to gain currency as well.").getBoolean(true);
        EconEventHandler.threshold = config.get(CONFIG_CAT, "threshold", 10, "Amount of XP that can be converted to 1 currency").getInt(10);

    }

    @Override
    public void save(Configuration config)
    {
        config.get(CONFIG_CAT, "currencySingular", "gold").set(currencySingular);
        config.get(CONFIG_CAT, "currencyPlural", "gold").set(currencyPlural);
        config.get(CONFIG_CAT, "startbudget", 100).set(startbudget);
        config.get(CONFIG_CAT, "multiplier", 1).set(psfPrice);
    }

}
