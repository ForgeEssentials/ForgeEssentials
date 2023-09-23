package com.forgeessentials.jscripting.command;

import javax.script.ScriptException;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.fewrapper.fe.JsCommandArgs;
import com.forgeessentials.jscripting.fewrapper.fe.JsCommandOptions;
import com.forgeessentials.jscripting.fewrapper.fe.command.JsArgumentType;
import com.forgeessentials.jscripting.fewrapper.fe.command.JsCommandNode;
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

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
    	if(options.subNodes==null) {
    		return baseBuilder.executes(context -> execute(context, "noDefinedSubNodes"));
    	}

    	try {
    		//for(Object node : options.subNodes) {
    		//	recursiveBuilding(baseBuilder, node);
        	//}
    		recursiveBuilding(baseBuilder, options.subNodes);
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (FECommandParsingException e) {
			System.out.println(e.error);
			e.printStackTrace();
		}
        return baseBuilder;
    }

    private void recursiveBuilding(ArgumentBuilder<CommandSource, ?> parentNode, Object node) throws ScriptException, FECommandParsingException{
		JsCommandNodeWrapper wrapper = script.getProperties(new JsCommandNodeWrapper(), node, JsCommandNodeWrapper.class);
		JsCommandNode nodeObject;
		ArgumentBuilder<CommandSource, ?> newNode;
		if((JsNodeType.valueOf(wrapper.type))==JsNodeType.LITERAL) {
			nodeObject = script.getProperties(new JsCommandNodeLiteral(), wrapper.containedNode, JsCommandNodeLiteral.class);
			newNode = Commands.literal(((JsCommandNodeLiteral) nodeObject).literal);
		}
		else if((JsNodeType.valueOf(wrapper.type))==JsNodeType.ARGUMENT){
			nodeObject = script.getProperties(new JsCommandNodeArgument(), wrapper.containedNode, JsCommandNodeArgument.class);
			newNode = Commands.argument(((JsCommandNodeArgument)nodeObject).argumentName, JsArgumentType.getType(((JsCommandNodeArgument)nodeObject).argumentType));
		}
		else {
			throw new ScriptException("Invalid JsNodeType! "+wrapper.type);
		}

		if(nodeObject.childTree==null){
			if(!nodeObject.insertExecution||nodeObject.executionParams==null) {
				throw new ScriptException("CommandTree ends must specify an execution parameters!");
			}
			newNode.executes(context -> execute(context, nodeObject.executionParams));
			parentNode.then(newNode);
			return;
		}

		if(nodeObject.insertExecution) {
			if(nodeObject.executionParams==null) {
				throw new ScriptException("insertExecution true must specify an execution parameter!");
			}
			newNode.executes(context -> execute(context, nodeObject.executionParams));
		}

		if(nodeObject.childTree!=null) {
			for(Object childNode : nodeObject.childTree) {
    			recursiveBuilding(newNode, childNode);
        	}
		}

		parentNode.then(newNode);
		return;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
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
