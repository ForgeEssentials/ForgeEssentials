package com.forgeessentials.scripting;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.scripting.ScriptParser.MissingPermissionException;
import com.forgeessentials.scripting.ScriptParser.MissingPlayerException;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.scripting.ScriptParser.ScriptMethod;
import com.forgeessentials.scripting.ScriptParser.SyntaxException;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.WorldSettings;

public final class ScriptMethods
{

    public static Map<String, ScriptMethod> scriptMethods = new HashMap<>();

    public static void add(String name, ScriptMethod argument)
    {
        if (scriptMethods.containsKey(name))
            throw new RuntimeException(String.format("Script method name @%s already registered", name));
        scriptMethods.put(name, argument);
    }

    public static ScriptMethod get(String name)
    {
        return scriptMethods.get(name);
    }

    public static Map<String, ScriptMethod> getAll()
    {
        return ImmutableMap.copyOf(scriptMethods);
    }

    private static void registerAll()
    {
        try
        {
            for (Field field : ScriptMethods.class.getDeclaredFields())
                if (ScriptMethod.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers()))
                    add(field.getName().toLowerCase(), (ScriptMethod) field.get(null));
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static final ScriptMethod echo = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.sendMessage(sender, ChatOutputHandler.formatColors(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send message without any special formatting to the player";
        }
    };

    public static final ScriptMethod confirm = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.chatConfirmation(sender, StringUtils.join(args, " "));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send confirmation message to the player";
        }
    };

    public static final ScriptMethod notify = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.chatNotification(sender, StringUtils.join(args, " "));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send notification message to the player";
        }
    };

    public static final ScriptMethod warn = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.chatWarning(sender, StringUtils.join(args, " "));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send warning message to the player";
        }
    };

    public static final ScriptMethod error = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.chatError(sender, StringUtils.join(args, " "));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send error message to the player";
        }
    };

    public static final ScriptMethod fail = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.chatError(sender, StringUtils.join(args, " "));
            return false;
        }

        @Override
        public String getHelp()
        {
            return "Send error message to the player and fail script execution";
        }
    };

    public static final ScriptMethod echoall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.broadcast(ChatOutputHandler.formatColors(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send message without any special formatting to all players";
        }
    };

    public static final ScriptMethod confirmall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.broadcast(ChatOutputHandler.confirmation(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send confirmation message to all players";
        }
    };

    public static final ScriptMethod notifyall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.broadcast(ChatOutputHandler.notification(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send notification message to all players";
        }
    };

    public static final ScriptMethod warnall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.broadcast(ChatOutputHandler.warning(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send warning message to all players";
        }
    };

    public static final ScriptMethod errorall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.broadcast(ChatOutputHandler.error(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send error message to all players";
        }
    };

    public static final ScriptMethod failall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            ChatOutputHandler.broadcast(ChatOutputHandler.error(StringUtils.join(args, " ")));
            return false;
        }

        @Override
        public String getHelp()
        {
            return "Send error message to all players and fail script execution";
        }
    };

    public static final ScriptMethod permset = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args) throws CommandException
        {
            CommandParserArgs arguments = new CommandParserArgs(null, args, MinecraftServer.getServer());
            PermissionCommandParser.parseMain(arguments);
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Modify permissions (like /p command)";
        }
    };

    protected static boolean getPermcheckResult(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
            throw new SyntaxException("Missing argument for permchecksilent");
        UserIdent ident;
        if (sender instanceof EntityPlayerMP)
        	ident = UserIdent.get((EntityPlayerMP) sender);
        else
        	ident = UserIdent.getServer("", "" + sender.getName().toLowerCase());
        String permission = args[0];
        String value = args.length > 1 ? args[1] : Zone.PERMISSION_TRUE;
        boolean result;
        if (value.equals(Zone.PERMISSION_TRUE))
            result = APIRegistry.perms.checkUserPermission(ident, permission);
        else if (value.equals(Zone.PERMISSION_FALSE))
            result = !APIRegistry.perms.checkUserPermission(ident, permission);
        else
            result = APIRegistry.perms.getUserPermissionProperty(ident, permission).equals(value);
        return result;
    }

    public static final ScriptMethod permchecksilent = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            return getPermcheckResult(sender, args);
        }

        @Override
        public String getHelp()
        {
            return "`permchecksilent <perm> [value] [error message...]`  \nPermission check (without error message). "
                    + "Use `true` or `false` as value for normal permission checks (default value is `true`)."
                    + "Other values will cause a permission-property check.";
        }
    };

    public static final ScriptMethod permcheck = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (getPermcheckResult(sender, args))
                return true;
            if (args.length > 2)
                throw new MissingPermissionException(args[0], StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " "));
            else
                throw new MissingPermissionException(args[0]);
        }

        @Override
        public String getHelp()
        {
            return "`permcheck <perm> [value] [error message...]`  \nPermission check (with error message). "
                    + "Use `true` or `false` as value for normal permission checks (default value is `true`)."
                    + "Other values will cause a permission-property check.";
        }
    };

    public static final ScriptMethod teleport = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args) throws CommandException
        {
            if (args.length < 1)
                throw new SyntaxException("Missing player argument for teleport");
            if (args.length < 2)
                throw new SyntaxException("Missing target argument for teleport");
            UserIdent player = UserIdent.get(args[1], sender);
            if (!player.hasPlayer())
                return false;
            if (args.length == 2)
            {
                UserIdent target = UserIdent.get(args[2], sender);
                if (!target.hasPlayer())
                    return false;
                TeleportHelper.teleport(player.getPlayerMP(), new WarpPoint(target.getPlayerMP()));
            }
            else if (args.length == 4)
            {
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                EntityPlayerMP p = player.getPlayerMP();
                TeleportHelper.teleport(p, new WarpPoint(p.dimension, x, y, z, p.cameraPitch, p.cameraYaw));
            }
            else if (args.length == 5)
            {
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                int dim = Integer.parseInt(args[4]);
                if (!DimensionManager.isDimensionRegistered(dim))
                    return false;
                EntityPlayerMP p = player.getPlayerMP();
                TeleportHelper.teleport(p, new WarpPoint(dim, x, y, z, p.cameraPitch, p.cameraYaw));
            }
            else
                throw new SyntaxException("Incorrect number of arguments");
            return true;
        }

        @Override
        public String getHelp()
        {
            return "`teleport <player> <to-player>`  \n`teleport <player> <x> <y> <z> [dim]`";
        }
    };

    public static final ScriptMethod pay = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            if (args.length < 1)
                throw new SyntaxException("Missing amount for pay command");
            if (args.length > 2)
                throw new SyntaxException("Too many arguments");
            long amount = Long.parseLong(args[0]);
            Wallet src = APIRegistry.economy.getWallet(UserIdent.get((EntityPlayerMP) sender));
            Wallet dst = null;
            if (args.length == 2)
            {
                UserIdent dstIdent = UserIdent.get(args[1], sender);
                if (!dstIdent.hasUuid())
                    throw new ScriptException("Player %s not found", args[1]);
                dst = APIRegistry.economy.getWallet(dstIdent);
            }
            if (!src.withdraw(amount))
            {
                ChatOutputHandler.chatError(sender, Translator.translate("You can't afford that!"));
                return false;
            }
            if (dst != null)
                dst.add(amount);
            return true;
        }

        @Override
        public String getHelp()
        {
            return "`pay <amount> [to-player]`  \nMake the player pay some amount of money and fail, if he can't afford it";
        }
    };

    public static final ScriptMethod checkTimeout = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (!(sender instanceof EntityPlayer))
                throw new MissingPlayerException();
            if (args.length < 1)
                throw new SyntaxException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
            PlayerInfo pi = PlayerInfo.get((EntityPlayer) sender);
            if (!pi.checkTimeout(args[0]))
            {
                if (args.length > 1)
                {
                    String msg = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
                    String timeout = ChatOutputHandler.formatTimeDurationReadable(pi.getRemainingTimeout(args[0]) / 1000, true);
                    ChatOutputHandler.chatError(sender, String.format(msg, timeout));
                }
                return false;
            }
            return true;
        }

        @Override
        public String getHelp()
        {
            return "`checkTimeout <id> [error msg...]`  \nCheck, if a timeout finished. Use \u0025s in error message to print the remaining time.";
        }
    };

    public static final ScriptMethod startTimeout = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (!(sender instanceof EntityPlayer))
                throw new MissingPlayerException();
            if (args.length < 2)
                throw new SyntaxException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
            PlayerInfo pi = PlayerInfo.get((EntityPlayer) sender);
            pi.startTimeout(args[0], Long.parseLong(args[1]));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "`startTimeout <id> <t>`  \nStart a timeout";
        }
    };

    public static final ScriptMethod timeout = new ScriptMethod() {
        @Override
        public boolean process(final ICommandSender sender, String[] args)
        {
            final String commandline = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
            long timeout = Long.parseLong(args[0]);
            TaskRegistry.schedule(new Runnable() {
                @Override
                public void run()
                {
                    TaskRegistry.runLater(new Runnable() {
                        @Override
                        public void run()
                        {
                            MinecraftServer.getServer().getCommandManager().executeCommand(sender, commandline);
                        }
                    });
                }
            }, timeout);
            return true;
        }

        @Override
        public String getHelp()
        {
            return "`timeout <delay> <command...>`  \nMake another command run after a delay (in ms).  \nCan be used with pattern commands to make more complex timed scripts.";
        }
    };

    public static final ScriptMethod random = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            return ForgeEssentials.rnd.nextInt(100) < Integer.parseInt(args[0]);
        }

        @Override
        public String getHelp()
        {
            return "`random <success-chance-in-percent>`  \nThis method will randomly success or fail.  \nIf it fails, the rest of the script will not be executed any more.";
        }
    };
	
	public static final ScriptMethod expCheck = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				int experienceLevel = Integer.parseInt(args[0]);
				String operator = args.length >= 2 ? args[1] : "EQ";
				if (operator.toUpperCase().equals("EQ") || operator.equals("=="))
				{
					return ((EntityPlayerMP) sender).experienceLevel == experienceLevel;
				}
				else if (operator.toUpperCase().equals("LT") || operator.equals("<"))
				{
					return ((EntityPlayerMP) sender).experienceLevel < experienceLevel;
				}
				else if (operator.toUpperCase().equals("LTE") || operator.equals("<="))
				{
					return ((EntityPlayerMP) sender).experienceLevel <= experienceLevel;
				}
				else if (operator.toUpperCase().equals("GT") || operator.equals(">"))
				{
					return ((EntityPlayerMP) sender).experienceLevel > experienceLevel;
				}
				else if (operator.toUpperCase().equals("GTE") || operator.equals(">="))
				{
					return ((EntityPlayerMP) sender).experienceLevel >= experienceLevel;
				}
				else
					return false;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`expCheck <expLevel> [operator -- LT, LTE, GT, GTE, or EQ -- case insensitive]`  \nThis method will check the expLevel of a player and compare it to a given value, where LT refers to the player expLevel being less than the inputed value.\nIf no operator is specified, equals (EQ) will be used as default.\nYou can also use ==,<,<=,>,>= in place of EQ,LT,LTE,GT,GTE.";
        }
    };
	public static void expAddInternal(EntityPlayerMP ep, int expDiff)
	{
		if (expDiff > 0)
		{
			for (int i = 0; i < expDiff; i++)
			{
				ep.experienceTotal += ep.xpBarCap();
				ep.experienceLevel ++;
			}
		}
		else if (expDiff < 0)
		{
			for (int i = 0; i > expDiff; i--)
			{
				if (ep.experienceLevel > 0)
				{
					ep.experienceLevel --;
					ep.experienceTotal -= ep.xpBarCap();
				}
			}
		}
	}
	public static final ScriptMethod expSet = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				int experienceLevel = Integer.parseInt(args[0]);				
				int oldExperienceLevel = ((EntityPlayerMP) sender).experienceLevel;
				int expDiff = experienceLevel - oldExperienceLevel;
				
				expAddInternal(((EntityPlayerMP) sender), expDiff);
				/*if (oldExperienceLevel > experienceLevel)
				{
					for (int i = oldExperienceLevel; i > experienceLevel; i--)
					{
						((EntityPlayerMP) sender).experienceLevel --;
						((EntityPlayerMP) sender).experienceTotal -= ((EntityPlayerMP) sender).xpBarCap();						
					}
				}
				else if (oldExperienceLevel < experienceLevel)
				{
					for (int i = oldExperienceLevel; i < experienceLevel; i++)
					{
						((EntityPlayerMP) sender).experienceTotal += ((EntityPlayerMP) sender).xpBarCap();
						((EntityPlayerMP) sender).experienceLevel ++;
					}
				}*/
				
				return true;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`expSet <expLevel>`  \nThis method will set the command sender's expLevel to the specified value.";
        }
    };
	
	public static final ScriptMethod expAdd = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				int expDiff = Integer.parseInt(args[0]);
				
				expAddInternal(((EntityPlayerMP) sender), expDiff);			
				
				return true;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`expAdd <expLevel>`  \nThis method will add the specified signed value to the command sender's expLevel.";
        }
    };
	
	public static final ScriptMethod hungerCheck = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				int hunger = Integer.parseInt(args[0]);
				String operator = args.length >= 2 ? args[1] : "EQ";
				if (operator.toUpperCase().equals("EQ") || operator.equals("=="))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getFoodLevel() == hunger;
				}
				else if (operator.toUpperCase().equals("LT") || operator.equals("<"))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getFoodLevel() < hunger;
				}
				else if (operator.toUpperCase().equals("LTE") || operator.equals("<="))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getFoodLevel() <= hunger;
				}
				else if (operator.toUpperCase().equals("GT") || operator.equals(">"))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getFoodLevel() > hunger;
				}
				else if (operator.toUpperCase().equals("GTE") || operator.equals(">="))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getFoodLevel() >= hunger;
				}
				else
					return false;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`hungerCheck <hunger> [operator -- LT, LTE, GT, GTE, or EQ -- case insensitive]`  \nThis method will check the hunger of a player and compare it to a given value, where LT refers to the player hunger being less than the inputed value.\nIf no operator is specified, equals (EQ) will be used as default.\nYou can also use ==,<,<=,>,>= in place of EQ,LT,LTE,GT,GTE.";
        }
    };
    private static Field foodLevel;
    private static Field foodSaturationLevel;
    public static void initHungerRelection()
    {
    	try {
			foodLevel = FoodStats.class.getDeclaredField("field_75127_a");
			foodSaturationLevel = FoodStats.class.getDeclaredField("field_75125_b");
			foodLevel.setAccessible(true);
			foodSaturationLevel.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	public static final ScriptMethod hungerSet = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {        	
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				//int hungerneeded = Integer.parseInt(args[0]) - ((EntityPlayerMP) sender).getFoodStats().getFoodLevel();
				//((EntityPlayerMP) sender).getFoodStats().addStats(hungerneeded, 0);
				
				try {
					if (foodLevel == null)
		        	{
		                initHungerRelection();
		        	}
					foodLevel.set(((EntityPlayerMP) sender).getFoodStats(), Integer.parseInt(args[0]));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return true;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`hungerSet <hunger>`  \nThis method will set the command sender's hunger to the specified value.";
        }
    };
	
	public static final ScriptMethod hungerAdd = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				//int hungerdiff = Integer.parseInt(args[0]);
				
				//((EntityPlayerMP) sender).getFoodStats().addStats(hungerdiff, 0);
				
				try {
					if (foodLevel == null)
		        	{
		                initHungerRelection();
		        	}
					foodLevel.set(((EntityPlayerMP) sender).getFoodStats(),((EntityPlayerMP) sender).getFoodStats().getFoodLevel() + Integer.parseInt(args[0]));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`hungerAdd <hunger>`  \nThis method will add the specified signed value to the command sender's hunger.";
        }
    };
	
	public static final ScriptMethod saturationCheck = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				float saturation = Float.parseFloat(args[0]);
				String operator = args.length >= 2 ? args[1] : "EQ";
				if (operator.toUpperCase().equals("EQ") || operator.equals("=="))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getSaturationLevel() == saturation;
				}
				else if (operator.toUpperCase().equals("LT") || operator.equals("<"))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getSaturationLevel() < saturation;
				}
				else if (operator.toUpperCase().equals("LTE") || operator.equals("<="))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getSaturationLevel() <= saturation;
				}
				else if (operator.toUpperCase().equals("GT") || operator.equals(">"))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getSaturationLevel() > saturation;
				}
				else if (operator.toUpperCase().equals("GTE") || operator.equals(">="))
				{
					return ((EntityPlayerMP) sender).getFoodStats().getSaturationLevel() >= saturation;
				}
				else
					return false;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`saturationCheck <saturation> [operator -- LT, LTE, GT, GTE, or EQ -- case insensitive]`  \nThis method will check the saturation of a player and compare it to a given value, where LT refers to the player saturation being less than the inputed value.\nIf no operator is specified, equals (EQ) will be used as default.\nYou can also use ==,<,<=,>,>= in place of EQ,LT,LTE,GT,GTE.";
        }
    };
	
	public static final ScriptMethod saturationSet = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				//float saturationneeded = Float.parseFloat(args[0]) - ((EntityPlayerMP) sender).getFoodStats().getSaturationLevel();
				//((EntityPlayerMP) sender).getFoodStats().addStats(0, saturationneeded);
				
				try {
					if (foodSaturationLevel == null)
		        	{
		                initHungerRelection();
		        	}
					foodSaturationLevel.set(((EntityPlayerMP) sender).getFoodStats(),Float.parseFloat(args[0]));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return true;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`saturationSet <saturation>`  \nThis method will set the command sender's saturation to the specified value.";
        }
    };
	
	public static final ScriptMethod saturationAdd = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				//float saturationdiff = Float.parseFloat(args[0]);
				
				//((EntityPlayerMP) sender).getFoodStats().addStats(0, saturationdiff);

				try {
					if (foodSaturationLevel == null)
		        	{
		                initHungerRelection();
		        	}
					foodSaturationLevel.set(((EntityPlayerMP) sender).getFoodStats(),((EntityPlayerMP) sender).getFoodStats().getSaturationLevel() + Float.parseFloat(args[0]));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return true;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`saturationAdd <saturation>`  \nThis method will add the specified signed value to the command sender's saturation.";
        }
    };
	
	public static final ScriptMethod healthCheck = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				float health = Float.parseFloat(args[0]);
				String operator = args.length >= 2 ? args[1] : "EQ";
				if (operator.toUpperCase().equals("EQ") || operator.equals("=="))
				{
					return ((EntityPlayerMP) sender).getHealth() == health;
				}
				else if (operator.toUpperCase().equals("LT") || operator.equals("<"))
				{
					return ((EntityPlayerMP) sender).getHealth() < health;
				}
				else if (operator.toUpperCase().equals("LTE") || operator.equals("<="))
				{
					return ((EntityPlayerMP) sender).getHealth() <= health;
				}
				else if (operator.toUpperCase().equals("GT") || operator.equals(">"))
				{
					return ((EntityPlayerMP) sender).getHealth() > health;
				}
				else if (operator.toUpperCase().equals("GTE") || operator.equals(">="))
				{
					return ((EntityPlayerMP) sender).getHealth() >= health;
				}
				else
					return false;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`healthCheck <health> [operator -- LT, LTE, GT, GTE, or EQ -- case insensitive]`  \nThis method will check the health of a player and compare it to a given value, where LT refers to the player health being less than the inputed value.\nIf no operator is specified, equals (EQ) will be used as default.\nYou can also use ==,<,<=,>,>= in place of EQ,LT,LTE,GT,GTE.";
        }
    };
	
	public static final ScriptMethod healthSet = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				float health = Float.parseFloat(args[0]);
				((EntityPlayerMP) sender).setHealth(health);
				
				return true;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`healthSet <health>`  \nThis method will set the command sender's health to the specified value.";
        }
    };
	
	public static final ScriptMethod healthAdd = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				float health = Float.parseFloat(args[0]) + ((EntityPlayerMP) sender).getHealth();
				((EntityPlayerMP) sender).setHealth(health);
				return true;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`healthAdd <health>`  \nThis method will add the specified signed value to the command sender's health.";
        }
    };
	
	public static final ScriptMethod gmCheck = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				int gm = Integer.parseInt(args[0]);
				return ((EntityPlayerMP) sender).theItemInWorldManager.getGameType().getID() == gm;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`gmCheck <gamemode>`  \nThis method will check if the gamemode of the player is equal to the given value.";
        }
    };
	
	public static final ScriptMethod gmSet = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length >= 1)
			{
				if (!(sender instanceof EntityPlayerMP))
					throw new MissingPlayerException();
				
				int gm = Integer.parseInt(args[0]);
				((EntityPlayerMP) sender).setGameType(WorldSettings.GameType.getByID(gm));
				return true;
			}
			else
				return false;
        }

        @Override
        public String getHelp()
        {
            return "`gmSet <gamemode>`  \nThis method will set the command sender's gamemode to the specified value.";
        }
    };
    static
    {
        registerAll();
    }

}
