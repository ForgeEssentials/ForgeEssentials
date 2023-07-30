package com.forgeessentials.core.misc.commandTools;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.data.v2.DataManager;
import com.google.gson.annotations.Expose;

public class FEAliasesManager
{

	public FEAliasesManager() {
		loadData();
	}
	public AliasesMap aliasMap = new AliasesMap();

    public void loadCommandAliases(final FECommandData commandData)
    {
    	if(aliasMap.getList().containsKey(commandData.getName())) {
    		commandData.setAliases(aliasMap.getList().get(commandData.getName()));
    		return;
    	}
    	aliasMap.addAliases(commandData.getName(), commandData.getAliases());
    }

    private static File getAliasFile()
    {
        return new File(ForgeEssentials.getFEDirectory(), "CommandAliases.json");
    }

    public void loadData()
    {
    	if (!ModuleCommands.newMappings && getAliasFile().exists())
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
		public void addAliases(String name, List<String> alias){
			aliases.put(name, alias);
		}
	}
}
