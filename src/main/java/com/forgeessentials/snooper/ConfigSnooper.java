package com.forgeessentials.snooper;

import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import net.minecraftforge.common.config.Configuration;

public class ConfigSnooper extends ConfigLoaderBase {

    @Override
    public void load(Configuration config, boolean isReload)
    {
        String cat = "Snooper";

        ModuleSnooper.port = config.get(cat, "port", 25565, "The query port").getInt();
        ModuleSnooper.hostname = config.get(cat, "hostname", "", "The query hostname/IP").getString();

        for (Response response : ResponseRegistry.getAllResponses().values())
        {
            String subCat = cat + "." + response.getName();
            response.allowed = config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").getBoolean(true);
            response.readConfig(subCat, config);
        }
    }

    @Override
    public void save(Configuration config)
    {
        String cat = "Snooper";

        config.get(cat, "port", 25565, "").set(ModuleSnooper.port);
        config.get(cat, "hostname", "", "The query hostname/IP").set(ModuleSnooper.hostname);

        for (Response response : ResponseRegistry.getAllResponses().values())
        {
            String subCat = cat + "." + response.getName();
            config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").set(response.allowed);
            response.writeConfig(subCat, config);
        }
    }

}
