package com.forgeessentials.core.misc.commandTools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.serverNetwork.utils.ConnectionData.ConnectedClientData;
import com.forgeessentials.serverNetwork.utils.ConnectionData.LocalClientData;
import com.forgeessentials.serverNetwork.utils.ConnectionData.LocalServerData;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.google.gson.annotations.Expose;

import net.minecraftforge.common.ForgeConfigSpec;

public class FEAliasesManager
{

	public FEAliasesManager() {
		loadData();
	}
	public AliasesMap aliasMap = new AliasesMap();

    public void loadCommandAliases(final FECommandData commandData)
    {
    	if(aliasMap.getList().containsKey(commandData.getName())) {
    		
    	}
        final ForgeConfigSpec.ConfigValue<List<? extends String>> aliases;
        String commandCat = "Command-" + commandData.getData().getName();
        // LoggingHandler.felog.info(commandCat);

        // load from command config names
//        dataA.getSpecBuilder().push(commandCat);
//        aliases = dataA.getSpecBuilder().defineList("aliases", (commandData.getData().getAliases()),
//                ConfigBase.stringValidator);
//        dataA.getSpecBuilder().pop();
//
//        commandAlises.put(commandData.getData().getName(), aliases);
        
        // LoggingHandler.felog.info(commandData.getData().getName());
        // load aliases and test for newMappings
//        List<String> aliasesProperty = new ArrayList<>(commandAlises.get(commandData.getData().getName()).get());
//        if (ModuleCommands.newMappings)
//        {
//            aliasesProperty.clear();
//            for (String alias : commandData.getData().getAliases())
//            {
//                aliasesProperty.add(String.valueOf(alias));
//            }
//
//            final ForgeConfigSpec.ConfigValue<List<? extends String>> string = commandAlises
//                    .get(commandData.getData().getName());
//            string.set(aliasesProperty);
//            commandAlises.put(commandData.getData().getName(), string);
//        }
//        LoggingHandler.felog.info(aliasesProperty.toString());
//        // set aliases
//        commandData.getData().setAliases(aliasesProperty);
    }

    private static File getAliasFile()
    {
        return new File(ForgeEssentials.getFEDirectory(), "CommandAliases.json");
    }

    public void loadData()
    {
    	if (!ModuleCommands.newMappings)
        {
    		aliasMap = DataManager.load(AliasesMap.class, getAliasFile());
        }
    }

    public void saveData()
    {
    	DataManager.save(aliasMap, getAliasFile());
    }

	protected class AliasesMap {
		@Expose(serialize = true, deserialize = true)
		private Map<String, List<String>> aliases = new HashMap<>();
		
		public Map<String, List<String>> getList(){
			return aliases;
		}
	}
}
