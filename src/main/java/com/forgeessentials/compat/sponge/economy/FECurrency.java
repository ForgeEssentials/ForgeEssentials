package com.forgeessentials.compat.sponge.economy;

import java.math.BigDecimal;

import org.spongepowered.api.service.economy.Currency;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.economy.ModuleEconomy;

import net.kyori.adventure.text.Component;

public class FECurrency implements Currency
{
    @Override
    public Component displayName()
    {
        return Component.text(APIRegistry.perms.getGlobalPermissionProperty(ModuleEconomy.PERM_CURRENCY_SINGULAR));
    }

    @Override
    public Component pluralDisplayName()
    {
        return Component.text(APIRegistry.perms.getGlobalPermissionProperty(ModuleEconomy.PERM_CURRENCY));
    }

    /**
     * we don't support this
     * 
     * @return
     */
    @Override
    public Component symbol()
    {
        return Component.empty();
    }

    @Override
    public Component format(BigDecimal amount, int numFractionDigits)
    {
        return Component.text(APIRegistry.economy.currency(amount.longValue()));
    }

    @Override
    public int defaultFractionDigits()
    {
        return 0;
    }

    @Override
    public boolean isDefault()
    {
        return false;
    }

}
