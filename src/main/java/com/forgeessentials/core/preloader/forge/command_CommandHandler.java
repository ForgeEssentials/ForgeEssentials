package com.forgeessentials.core.preloader.forge;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class command_CommandHandler
{
    // patch method
    public static List getPossibleCommands(Map commandMap, ICommandSender p_71558_1_, String p_71558_2_)
    {
        String[] astring = p_71558_2_.split(" ", -1);
        String s1 = astring[0];

        if (astring.length == 1)
        {
            List arraylist = new ArrayList();
            Iterator iterator = commandMap.entrySet().iterator();

            while (iterator.hasNext())
            {
                Entry entry = (Entry)iterator.next();

                if (CommandBase.doesStringStartWith(s1, (String) entry.getKey()) && net.minecraftforge.server.CommandHandlerForge.canUse(((ICommand)entry.getValue()), p_71558_1_))
                {
                    arraylist.add(entry.getKey());
                }
            }

            return arraylist;
        }
        else
        {
            if (astring.length > 1)
            {
                ICommand icommand = (ICommand)commandMap.get(s1);

                if (icommand != null)
                {
                    return icommand.addTabCompletionOptions(p_71558_1_, dropFirstString(astring));
                }
            }

            return null;
        }
    }

    // patch method
    public static List getPossibleCommands(Map commandSet, ICommandSender p_71557_1_)
    {
        ArrayList arraylist = new ArrayList();

        for (Object o : commandSet.keySet()) {
            ICommand icommand = (ICommand) commandSet.get(o);

            if (icommand != null) {

                if (net.minecraftforge.server.CommandHandlerForge.canUse(icommand, p_71557_1_)) {
                    arraylist.add(icommand);
                }
            }
        }

        return arraylist;
    }

    // in case we can't use the one in FunctionHelper
    public static String[] dropFirstString(String[] par0ArrayOfStr)
    {
        String[] var1 = new String[par0ArrayOfStr.length - 1];

        for (int var2 = 1; var2 < par0ArrayOfStr.length; ++var2)
        {
            var1[var2 - 1] = par0ArrayOfStr[var2];
        }

        return var1;
    }
}
