package com.forgeessentials.core.preloader.asm.mixins.command;

import net.minecraft.command.CommandHandler;

import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Mixin(CommandHandler.class)
public abstract class MixinCommandHandler_01
{
    
//    @Shadow
//    private final Map commandMap = new HashMap();
//
//    @Shadow
//    public Set commandSet = new HashSet();
//
//    // patch method
//    @Overwrite
//    public List getPossibleCommands(ICommandSender p_71558_1_, String p_71558_2_)
//    {
//        String[] astring = p_71558_2_.split(" ", -1);
//        String s1 = astring[0];
//
//        if (astring.length == 1)
//        {
//            List arraylist = new ArrayList();
//            Iterator iterator = commandMap.entrySet().iterator();
//
//            while (iterator.hasNext())
//            {
//                Entry entry = (Entry) iterator.next();
//
//                if (CommandBase.doesStringStartWith(s1, (String) entry.getKey()) && PermissionManager.checkPermission(p_71558_1_, (ICommand) entry.getValue()))
//                {
//                    arraylist.add(entry.getKey());
//                }
//            }
//
//            return arraylist;
//        }
//        else
//        {
//            if (astring.length > 1)
//            {
//                ICommand icommand = (ICommand) commandMap.get(s1);
//
//                if (icommand != null)
//                {
//                    return icommand.addTabCompletionOptions(p_71558_1_, ServerUtil.dropFirst(astring));
//                }
//            }
//
//            return null;
//        }
//    }
//
//    @Overwrite
//    public List getPossibleCommands(ICommandSender p_71557_1_)
//    {
//        ArrayList arraylist = new ArrayList();
//        Iterator iterator = this.commandSet.iterator();
//
//        while (iterator.hasNext())
//        {
//            ICommand icommand = (ICommand) iterator.next();
//
//            if (PermissionManager.checkPermission(p_71557_1_, icommand))
//            {
//                arraylist.add(icommand);
//            }
//        }
//
//        return arraylist;
//    }

}
