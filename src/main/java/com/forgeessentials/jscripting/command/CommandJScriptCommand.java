package com.forgeessentials.jscripting.command;

import javax.script.ScriptException;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.fewrapper.fe.JsCommandArgs;
import com.forgeessentials.jscripting.fewrapper.fe.JsCommandOptions;
import com.forgeessentials.jscripting.fewrapper.fe.command.JsArgumentType;
import com.forgeessentials.jscripting.fewrapper.fe.command.JsCommandNodeArgument;
import com.forgeessentials.jscripting.fewrapper.fe.command.JsCommandNodeLiteral;
import com.forgeessentials.jscripting.fewrapper.fe.command.JsCommandNodeWrapper;
import com.forgeessentials.jscripting.fewrapper.fe.command.JsNodeType;
import com.forgeessentials.util.CommandContextParcer;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandJScriptCommand extends ForgeEssentialsCommandBuilder
{

	public final ScriptInstance script;

	private JsCommandOptions options;

	public CommandJScriptCommand(ScriptInstance script, JsCommandOptions options) {
		super(true, options.name, options.opOnly ? DefaultPermissionLevel.OP : DefaultPermissionLevel.ALL);
		Preconditions.checkNotNull(script);
		Preconditions.checkNotNull(options);
		Preconditions.checkNotNull(options.name);
		Preconditions.checkNotNull(options.processCommand);
		if (options.usage == null) {
			options.usage = "/" + options.name + ": scripted command - no description";
		}
		this.script = script;
		this.options = options;
	}

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return options.name;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return options.opOnly ? DefaultPermissionLevel.OP : DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
    	try {
    		if(options.listsSubNodes==null) {
        		if(options.executionParams==null) {
    				throw new ScriptException("CommandTreeBase standalone must allow execution!");
    			}
    			if(!options.executesMethod) {
    				throw new ScriptException("CommandTreeBase standalone must specify an execution parameter!");
    			}
        		return baseBuilder.executes(context -> execute(context, options.executionParams));
        	}
    		boolean flag=false;
    		for(JsCommandNodeWrapper node : options.listsSubNodes) {
    			boolean type = recursiveBuilding(baseBuilder, node);
    			if(flag && type) {throw new ScriptException("Cant have two argument nodes on the same branch!");}
    			if(!flag && type) {flag=true;}
        	}
    		//recursiveBuilding(baseBuilder, options.subNodes);
    		if(options.executesMethod) {
    			if(options.executionParams==null) {
    				throw new ScriptException("CommandTreeBase standalone must allow execution!");
    			}
    			return baseBuilder.executes(context -> execute(context, options.executionParams));
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (FECommandParsingException e) {
			System.out.println(e.error);
			e.printStackTrace();
		}
        return null;
    }

    private boolean recursiveBuilding(ArgumentBuilder<CommandSourceStack, ?> parentNode, JsCommandNodeWrapper node) throws ScriptException, FECommandParsingException{
		ArgumentBuilder<CommandSourceStack, ?> newNode;
		boolean execution;
		String params;
		boolean type;
		if((JsNodeType.valueOf(node.type))==JsNodeType.LITERAL) {
			JsCommandNodeLiteral nodeObject = script.getProperties(new JsCommandNodeLiteral(), node.containedNode, JsCommandNodeLiteral.class);
			newNode = Commands.literal(nodeObject.literal);
			execution = nodeObject.executesMethod;
			params = nodeObject.executionParams;
			type=false;
		}
		else if((JsNodeType.valueOf(node.type))==JsNodeType.ARGUMENT){
			JsCommandNodeArgument nodeObject = script.getProperties(new JsCommandNodeArgument(), node.containedNode, JsCommandNodeArgument.class);
			newNode = Commands.argument(nodeObject.argumentName, JsArgumentType.getType(JsArgumentType.valueOf(nodeObject.argumentType)));
			execution = nodeObject.executesMethod;
			params = nodeObject.executionParams;
			type=true;
		}
		else {
			throw new ScriptException("Invalid JsNodeType! "+node.type);
		}

		if(node.listsChildNodes==null){
			if(params==null) {
				throw new ScriptException("CommandTree ends must allow execution!");
			}
			if(!execution) {
				throw new ScriptException("CommandTree ends must specify an execution parameter!");
			}
			newNode.executes(context -> execute(context, params));
			parentNode.then(newNode);
			return type;
		}

		if(execution) {
			if(params==null) {
				throw new ScriptException("insertExecution true must specify an execution parameter!");
			}
			newNode.executes(context -> execute(context, params));
		}

		if(node.listsChildNodes!=null) {
			boolean flag1=false;
			for(JsCommandNodeWrapper childNode : node.listsChildNodes) {
    			boolean thing = recursiveBuilding(newNode, childNode);
    			if(flag1 && thing) {throw new ScriptException("Cant have two argument nodes on the same branch!");}
    			if(!flag1 && thing) {flag1=true;}
        	}
		}

		parentNode.then(newNode);
		return type;
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        try
        {
        	script.call(options.processCommand, options.processCommand, new JsCommandArgs(new CommandContextParcer(ctx, params)));
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
            ChatOutputHandler.chatError(ctx.getSource(), "Script error: method not found: " + e.getMessage());
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
            ChatOutputHandler.chatError(ctx.getSource(), e.getMessage());
        }
        catch (FECommandParsingException e) {
        	e.printStackTrace();
			ChatOutputHandler.chatError(ctx.getSource(), e.error);
		}
        return Command.SINGLE_SUCCESS;
    }
}
