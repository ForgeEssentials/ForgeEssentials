package com.forgeessentials.compat.sponge.economy;

import java.math.BigDecimal;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.economy.ModuleEconomy;

public class FECurrency implements Currency
{
    @Override
    public Text getDisplayName()
    {
        return Text.builder(APIRegistry.perms.getGlobalPermissionProperty(ModuleEconomy.PERM_CURRENCY_SINGULAR)).build();
    }

    @Override
    public Text getPluralDisplayName()
    {
        return Text.builder(APIRegistry.perms.getGlobalPermissionProperty(ModuleEconomy.PERM_CURRENCY)).build();
    }

    /**
     * we don't support this
     * @return
     */
    @Override
    public Text getSymbol()
    {
        return Text.EMPTY;
    }

    @Override
    public Text format(BigDecimal amount, int numFractionDigits)
    {
        return Text.builder(APIRegistry.economy.currency(amount.longValue())).build();
    }

    @Override
    public int getDefaultFractionDigits()
    {
        return 0;
    }

    @Override
    public boolean isDefault()
    {
        return false;
    }

    @Override
    public String getId()
    {
        return "fe:currency";
    }

    @Override
    public String getName()
    {
        return "fecurrency";
    }
}
