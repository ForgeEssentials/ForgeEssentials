package com.forgeessentials.scripting;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

public class ScriptParser
{
	public static int loopSize = 100;
	public static int maxOperationTime = 1000;
	
    public static interface ScriptMethod
    {

        public boolean process(ICommandSender sender, String[] args, Map<String,String> variableMap);

        public String getHelp();

    }

    public static interface ScriptArgument
    {

        public String process(ICommandSender sender) throws ScriptException;

        public String getHelp();

    }

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("@([\\w*]+)(.*)");

    //Java is pass by reference!  No need to return anything if if the reference is not changed
    public static void processArguments(ICommandSender sender, String[] actionArgs, List<String> args)
    {
        for (int i = 0; i < actionArgs.length; i++)
        {
            Matcher matcher = ARGUMENT_PATTERN.matcher(actionArgs[i]);
            if (!matcher.matches())
                continue;
            String modifier = matcher.group(1).toLowerCase();
            String rest = matcher.group(2);

            ScriptArgument argument = ScriptArguments.get(modifier);
            if (argument != null)
            {
                actionArgs[i] = argument.process(sender) + rest;
            }
            else if (modifier.endsWith("*"))
            {
                try
                {
                    int idx = 0;
                    if (modifier.length() > 1)
                        idx = Integer.parseInt(modifier.substring(0, modifier.length() - 1));
                    if (args == null || idx >= args.size())
                        throw new SyntaxException("Missing argument @%d", idx);
                    List<String> newArgs = new ArrayList<>(Arrays.asList(actionArgs));
                    newArgs.remove(i);
                    for (int j = idx; j < args.size(); j++)
                        newArgs.add(i + j - idx, args.get(j));
                    actionArgs = newArgs.toArray(new String[newArgs.size()]);
                    actionArgs[actionArgs.length - 1] += rest;
                }
                catch (NumberFormatException e)
                {
                    throw new SyntaxException("Unknown argument modifier \"%s\"", modifier);
                }
            }
            else
            {
                try
                {
                    int idx = Integer.parseInt(modifier);
                    if (args == null || idx >= args.size())
                        throw new SyntaxException("Missing argument @%d", idx);
                    actionArgs[i] = args.get(idx) + rest;
                }
                catch (NumberFormatException e)
                {
                    throw new SyntaxException("Unknown argument modifier \"%s\"", modifier);
                }
            }
        }
    }
    private static final Pattern CONDITION_PATTERN = Pattern.compile("'([^']*)'[\\s]*([!=<>]+)[\\s]*'([^']*)'[\\s]*([\\S]*)[\\s]*([\\S]*)");
    public static boolean parseCondition(String conditionalString, String[] actionsout)
    {
    	Matcher condM = CONDITION_PATTERN.matcher(conditionalString);
    	if (condM.find())
    	{
	    	String var0 = condM.group(1);
	    	String op = condM.group(2);
	    	String var1 = condM.group(3);
	    	if (actionsout != null && actionsout.length >= 2)
	    	{
	    		actionsout[0] = condM.group(4);
	    		actionsout[1] = condM.group(5);
	    	}
	    	Double n0 = toNumber(var0);
	    	Double n1 = toNumber(var1);
	    	System.out.println(var0);
	    	System.out.println(var1);
	    	System.out.println(actionsout[0]);
	    	System.out.println(actionsout[1]);
	    	if (n0 == null || n1 == null) //Not a number, treat as a string comparison	    	
	    		if (op.equals("=="))
	    			return var0.equals(var1);
	    		else if (op.equals("!="))
	    			return !var0.equals(var1);
	    		else
	    			throw new SyntaxException("Invalid Operator for String Comparison! Allowed operators are '==' and '!='");	    	
	    	else
	    	{
	    		double num0 = n0;
	    		double num1 = n1;
	    		if (op.equals("=="))
	    			return num0 == num1;	    		
	    		else if (op.equals("!="))	    		
	    			return num0 != num1;	    		
	    		else if (op.equals(">="))	    		
	    			return num0 >= num1;	    		
	    		else if (op.equals("<="))	    		
	    			return num0 <= num1;	    		
	    		else if (op.equals(">"))	    		
	    			return num0 > num1;	    		
	    		else if (op.equals("<"))	    		
	    			return num0 < num1;	    		
	    		else
	    			throw new SyntaxException("Invalid Operator for Numeric Comparison! Allowed operators are '==', '!-', '>=', '<=', '>', and '<'.");
	    	}
	    	
    	}
    	throw new SyntaxException("Invalid conditional syntax! Required syntax is: 'value' == 'value' labelTrue labelFalse");
    }
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$([\\w*]+)");
    public static void processVariables(String[] args, Map<String,String> variableMap)
    {
    	if (variableMap != null)
    	{
    		for (int i = 0; i < args.length; i++)
    		{
    			Matcher m = VARIABLE_PATTERN.matcher(args[i]);
    			while (m.find())
    			{
    				String var = m.group(1);
    				if (variableMap.containsKey(var))
    				{
    					args[i] = args[i].replaceFirst("\\Q" + m.group() + "\\E", variableMap.get(var));
    				}
    				else
    				{
    					args[i] = args[i].replaceFirst("\\Q" + m.group() + "\\E",var);
    				}
    			}
    		}
    	}
    }
    public static void run(List<String> script)
    {
        run(script, null);
    }

    public static void run(List<String> script, ICommandSender sender)
    {
        run(script, sender, null);
    }

    public static boolean run(List<String> script, ICommandSender sender, List<String> args)
    {
    	String[] gotoLabel = new String[1];
    	HashMap<String,String> variableMap = new HashMap<String,String>(); 
    	HashMap<String,Integer> labels = new HashMap<String,Integer>();
    	gotoLabel[0] = null;
    	int line = 0;
    	long time = System.currentTimeMillis();
    	String lastGoto = "";
    	int looppos = 0;
        while (line < script.size())
        {
            if (!run(script.get(line), sender, args, gotoLabel, variableMap, labels, line))
                return false;
            if (labels.containsKey(gotoLabel[0]))
            {
            	if (lastGoto.equals(gotoLabel[0]))
            		looppos++;
            	else
            	{
            		looppos = 0;
            		lastGoto = gotoLabel[0];
            	}
            	line = labels.get(gotoLabel[0]);
            	gotoLabel[0] = null;
            }
            line++;
            if (looppos > loopSize && loopSize > -1)
            	throw new ScriptErrorException("Program has been in the same loop too long (Greater than " + loopSize +  " iterations) and was automatically killed!");            	
            
            
            if (System.currentTimeMillis() - time > maxOperationTime)
            	throw new ScriptErrorException("Program has been running for longer than " + (float) maxOperationTime / 1000 +  " seconds and was automatically killed!");
            
        }
        if (gotoLabel[0] != null)
        	throw new ScriptErrorException("End of program reached before label matching: " + gotoLabel[0] + " was found!");
        
        return true;
    }

    public static Double toNumber(String num)
    {
    	try
    	{
    		return Double.parseDouble(num);
    	} catch (NumberFormatException e)
    	{
    		return null;
    	}
    }    
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\{([^}]*)\\}");
    private static final Pattern OPERATION_PATTERN = Pattern.compile("(\\d+\\.?\\d*)\\s*([+\\-*/%])\\s*(.+)");
    private static final Pattern PRIMARY_PATTERN = Pattern.compile("\\d+\\.?\\d*");
    public static double computeExpression(String expr)
    {
    	Matcher m = PRIMARY_PATTERN.matcher(expr);
    	if (m.matches())
    	{
    		return Double.parseDouble(m.group());
    	}
    	else
    	{
    		m = OPERATION_PATTERN.matcher(expr);
    		if (m.matches())
    		{
    			double d = Double.parseDouble(m.group(1));
    			String op = m.group(2);
    			String expr2 = m.group(3);
    			if (op.equals("+"))
    				d+=computeExpression(expr2);
    			else if (op.equals("-"))
    				d-=computeExpression(expr2);
    			else if (op.equals("*"))
    				d*=computeExpression(expr2);
    			else if (op.equals("/"))
    				d/=computeExpression(expr2);
    			else if (op.equals("%"))
    				d%=computeExpression(expr2);
    			return d;
    		}
    		return 0;
    	}
    }
    public static String[] processExpressions(String args)
    {    	
    	Matcher m = EXPRESSION_PATTERN.matcher(args);
    	while (m.find())
    	{
    		String expr = m.group(1);    		
    		args = args.replaceFirst("\\Q" + m.group() + "\\E", Double.toString(computeExpression(expr)));
    	}
    	return args.split(" ");
    }
    public static boolean run(String action, ICommandSender sender, List<String> argumentValues)
    {
    	return run(action,sender,argumentValues,null, null,null, 0);
    }
    public static boolean run(String action, ICommandSender sender, List<String> argumentValues, String[] gotoLabel, Map<String,String> variableMap, HashMap<String,Integer> labels, int line)
    {
        String[] args = action.split(" ", 2);
        String cmd = args[0].toLowerCase();
        String arguments =  args.length > 1 ? args[1] : "";
        args = args.length > 1 ? args[1].split(" ") : new String[0];        
        /*args = */processArguments(sender, args, argumentValues); //Java is pass by reference, assignment is not required.
        processVariables(args, variableMap); //Same here
        args = processExpressions(StringUtils.join(args," ")); //This needs to reassign the value because expressions can contain whitespace
        //TODO: Process Expressions
        if (gotoLabel != null) //Disable gotoProcessing if gotoLabel == null
        {    
        	if (cmd.equals("label"))
        	{
        		labels.put(args[0].toLowerCase(), line);
        		return true;
        	}
        	if (gotoLabel[0] != null)
        		return true;
	        else if (cmd.equals("goto"))
	        {
	        	gotoLabel[0] = args[0].toLowerCase();
	        	return true;
	        }
	        else if (cmd.equals("if"))
	        {
	        	String[] output = new String[2];
	        	//simple if processing
	        	if (parseCondition(StringUtils.join(args," ").toLowerCase(),output))
	        	{
	        		gotoLabel[0] = output[0];
	        	}
	        	else
	        	{
	        		gotoLabel[0] = output[1];
	        	}
	        	return true;
	        	
	        }        	
        }
        /*if (cmd.equals("set"))
        {
        	variableMap.put(args[0], StringUtils.join(Arrays.copyOfRange(args, 1,args.length)," "));
        	return true;
        }*/
        if (cmd.isEmpty())
            throw new SyntaxException("Could not handle script action \"%s\"", action);

        char c = cmd.charAt(0);
        switch (c)
        {
        case '/':
        case '$':
        case '?':
        case '*':
        {
            ICommandSender cmdSender = sender;
            if (cmd.equals("p") || cmd.equals("feperm"))
                cmdSender = MinecraftServer.getServer();

            boolean ignoreErrors = false;
            modifierLoop: while (true)
            {
                cmd = cmd.substring(1);
                switch (c)
                {
                case '$':
                    if (!(cmdSender instanceof DoAsCommandSender))
                        cmdSender = new DoAsCommandSender(APIRegistry.IDENT_SERVER, sender);
                    ((DoAsCommandSender) cmdSender).setIdent(APIRegistry.IDENT_SERVER);
                    break;
                case '?':
                    ignoreErrors = true;
                    break;
                case '*':
                    if (sender instanceof EntityPlayer)
                    {
                        if (!(cmdSender instanceof DoAsCommandSender))
                            cmdSender = new DoAsCommandSender(UserIdent.get((EntityPlayer) sender), sender);
                        ((DoAsCommandSender) cmdSender).setHideChatMessages(true);
                    }
                    else if (sender == null || sender instanceof MinecraftServer)
                    {
                        if (!(cmdSender instanceof DoAsCommandSender))
                            cmdSender = new DoAsCommandSender(APIRegistry.IDENT_SERVER, sender);
                        ((DoAsCommandSender) cmdSender).setHideChatMessages(true);
                    }
                    break;
                case '/':
                    break modifierLoop;
                default:
                    throw new SyntaxException("Could not handle script action \"%s\"", action);
                }
                c = cmd.charAt(0);
            }
            ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmd);
            try
            {
                mcCommand.processCommand(cmdSender, args);
            }
            catch (CommandException e)
            {
                if (!ignoreErrors)
                    throw e;
                LoggingHandler.felog.info(String.format("Silent script command /%s %s failed: %s", cmd, StringUtils.join(args, " "), e.getMessage()));
            }
            return true;
        }
        default:        	
            boolean canFail = false;
            if (cmd.length() > 1 && cmd.charAt(0) == '?')
            {
                canFail = true;
                cmd = cmd.substring(1);
            }
            ScriptMethod method = ScriptMethods.get(cmd);
            if (method == null)
                throw new SyntaxException("Unknown script method \"%s\"", cmd);
            try
            {
                return method.process(sender, args, variableMap) | canFail;
            }
            catch (NumberFormatException e)
            {
                throw new CommandException("Invalid number format: " + e.getMessage());
            }
        }
    }

    public static class ScriptException extends RuntimeException
    {

        public ScriptException()
        {
            super();
        }

        public ScriptException(String message)
        {
            super(message);
        }

        public ScriptException(String message, Object... args)
        {
            super(String.format(message, args));
        }

    }

    public static class SyntaxException extends ScriptException
    {

        public SyntaxException(String message, Object... args)
        {
            super(message, args);
        }

    }

    public static class ScriptErrorException extends ScriptException
    {

        public ScriptErrorException()
        {
            super();
        }

        public ScriptErrorException(String message)
        {
            super(message);
        }

        public ScriptErrorException(String message, Object... args)
        {
            super(message, args);
        }

    }

    public static class MissingPlayerException extends ScriptErrorException
    {

        public MissingPlayerException()
        {
            super("Missing player for @player argument");
        }

    }

    public static class MissingPermissionException extends ScriptErrorException
    {

        public final String permission;

        public MissingPermissionException(String permission)
        {
            super();
            this.permission = permission;
        }

        public MissingPermissionException(String permission, String message)
        {
            super(message);
            this.permission = permission;
        }

        public MissingPermissionException(String permission, String message, Object... args)
        {
            super(message, args);
            this.permission = permission;
        }

    }

}
