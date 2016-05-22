package com.forgeessentials.scripting;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.sun.org.apache.xpath.internal.Arg;

import akka.pattern.Patterns;

public class ScriptParser
{
	public static int loopSize = 100;
	public static int maxOperationTime = 1000;
	
    public static interface ScriptMethod
    {

        public boolean process(ICommandSender sender, String[] args);

        public String getHelp();

    }

    public static interface ScriptArgument
    {

        public String process(ICommandSender sender) throws ScriptException;

        public String getHelp();

    }

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("@([\\w*]+)\\.?");

    //Java is pass by reference!  No need to return anything if if the reference is not changed
    public static String processArguments(ICommandSender sender, String actionArgs, List<String> args)
    {
        Matcher matcher = ARGUMENT_PATTERN.matcher(actionArgs);
        while (matcher.find())
        {
            String modifier = matcher.group(1).toLowerCase();

            ScriptArgument argument = ScriptArguments.get(modifier);
            if (argument != null)
            {
                actionArgs = matcher.replaceFirst(Matcher.quoteReplacement(argument.process(sender)));
                matcher.reset(actionArgs);
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
                    List<String> newArgs = new ArrayList<>(args);
                    //newArgs.remove(i);
                    //for (int j = idx; j < args.size(); j++)
                        //newArgs.add(i + j - idx, args.get(j));
                    for (int j = 0; j < idx; j++)
                    	newArgs.remove(j);
                    actionArgs = matcher.replaceFirst(Matcher.quoteReplacement(StringUtils.join(newArgs," ")));
                    matcher.reset(actionArgs);
                    //actionArgs = newArgs.toArray(new String[newArgs.size()]);
                    //actionArgs[actionArgs.length - 1] += rest;
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
                    actionArgs = matcher.replaceFirst(Matcher.quoteReplacement(args.get(idx)));
                    matcher.reset(actionArgs);
                }
                catch (NumberFormatException e)
                {
                    throw new SyntaxException("Unknown argument modifier \"%s\"", modifier);
                }
            }
        }
        return actionArgs;
        
    }
    //==|(
    private static final Pattern CONDITION_PATTERN = Pattern.compile("([^!=<>]*)(==|!=|>=|<=|>|<)([^!=<>]*)");
    private static final Pattern TRAILING_WHITESPACE_PATTERN = Pattern.compile("\\s*((?:\\S*(?:\\s*\\S*)*?))\\s*$");
    public static Boolean parseCondition(String conditionalString)
    {
    	Matcher condM = CONDITION_PATTERN.matcher(conditionalString);
    	if (condM.find())
    	{
    		Matcher twp = TRAILING_WHITESPACE_PATTERN.matcher(condM.group(1));
    		twp.matches();
	    	String var0 = twp.group(1);
	    	String op = condM.group(2);
	    	twp.reset(condM.group(3));
	    	twp.matches();
	    	String var1 = twp.group(1);
	    	Double n0 = ExpressionParser.computeExpression(var0);
	    	Double n1 = ExpressionParser.computeExpression(var1);
	    	if (n0 == null || n1 == null) //Not a number, treat as a string comparison	    	
	    		if (op.equals("=="))
	    			return var0.equals(var1);
	    		else if (op.equals("!="))
	    			return !var0.equals(var1);
	    		else
	    			return null;	    	
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
	    			return null; //Should never return
	    	}
	    	
    	}
    	return null;
    	
    }
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$(\\w)\\.?");
    public static String processVariables(String args, Map<String,String> variableMap)
    {
    	if (variableMap != null)
    	{
			Matcher m = VARIABLE_PATTERN.matcher(args);
			while (m.find())
			{
				String var = m.group(1);
				
				String value = null;
				if (variableMap.containsKey(var))
					value = variableMap.get(var);
				if (value == null)
					value = "null";
				args = m.replaceFirst(Matcher.quoteReplacement(value));
				m.reset(args);
				//args[i] = args[i].replaceFirst("\\Q" + Matcher.quoteReplacement(m.group()) + "\\E",Matcher.quoteReplacement(value));
			}
    	}
    	
    	return args;
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

    
    
    
    

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{([^}]*)\\}");
    public static String[] parseExpressions(String args)
    {    	
    	//System.out.println(args);
    	Matcher m = EXPRESSION_PATTERN.matcher(args);
    	while (m.find())
    	{
    		Double out = ExpressionParser.computeExpression(m.group(1));
    		if (out != null)
    		{    			
    			args = m.replaceFirst(out.toString());
				m.reset(args);
    		}
    		else
    			throw new SyntaxException("Expression: " + m.group(1).replaceAll("\\s*", "") + ", contains invalid tokens!");
    		//TODO: Order of Operations Parsing
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
        String arguments =  args.length > 1 ? args[1] : ""; //Keep Arguments as a single unit until all processing is done   
        arguments = processArguments(sender, arguments, argumentValues);
        arguments = processVariables(arguments, variableMap);
        args = parseExpressions(arguments);
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
        	//TODO: Change if to use expressions
	        else if (cmd.equals("if"))
	        {
	        	String labelTrue = args[args.length-2];
	        	String labelFalse = args[args.length-1];
	        	
	        	//simple if processing
	        	String cond = StringUtils.join(Arrays.copyOf(args,args.length-2)," ");
	        	Boolean b = parseCondition(cond);
	        	if (b == null)
	        		throw new SyntaxException("Invalid conditional syntax:" + cond + "Required syntax is: <expr> <op> <expr>, where valid operators are '==', and '!-', plus ('>=', '<=', '>', and '<') for Numeric Comparisons only.");
	        	else if (b)
	        	{
	        		gotoLabel[0] = labelTrue;
	        	}
	        	else
	        	{
	        		gotoLabel[0] = labelFalse;
	        	}
	        	return true;
	        	
	        }        	
        }
        if (cmd.equals("set"))
        {
        	String expr = StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," ");
        	Double d = ExpressionParser.computeExpression(expr);
        	if (d != null)
        		expr = d.toString();
        	variableMap.put(args[0], expr);
        	return true;
        }
        else if (args.length >= 2)
        {
	        if (cmd.equals("loadp"))
	        {
	        	UserIdent ident;
	        	if (sender instanceof EntityPlayerMP)
	        		ident = UserIdent.get((EntityPlayerMP) sender);
	        	else
	        		ident = UserIdent.getServer(null, "$" + sender.getCommandSenderName().toLowerCase());
	        	variableMap.put(args[0],APIRegistry.perms.getUserPermissionProperty(ident, "fe.vars." + args[1]));
	        	return true;
	        }
	        else if (cmd.equals("loadg"))
	        {
	        	variableMap.put(args[0],APIRegistry.perms.getGlobalPermissionProperty("fe.vars." + args[1]));
	        	return true;
	        }
	        else if (cmd.equals("storep"))
	        {
	        	UserIdent ident;
	        	if (sender instanceof EntityPlayerMP)
	        		ident = UserIdent.get((EntityPlayerMP) sender);
	        	else
	        		ident = UserIdent.getServer(null, "$" + sender.getCommandSenderName().toLowerCase());
	        	
	        	APIRegistry.perms.setPlayerPermissionProperty(ident, "fe.vars." + args[1],variableMap.get(args[0]));        	
	        	return true;
	        }
	        else if (cmd.equals("storeg"))
	        {
	        	APIRegistry.perms.setGroupPermissionProperty("_ALL_","fe.vars." + args[1],variableMap.get(args[0]));        	
	        	return true;
	        }
        }
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
                return method.process(sender, args) | canFail;
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
