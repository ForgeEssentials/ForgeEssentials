package com.forgeessentials.jscripting.command;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandJScriptCommand extends ForgeEssentialsCommandBuilder
{

    // public final ScriptInstance script;

    // private JsCommandOptions options;

    // private static final Pattern ARGUMENT_PATTERN =
    // Pattern.compile("@(\\w+)(.*)");
    /*
     * public CommandJScriptCommand(ScriptInstance script, JsCommandOptions options) { Preconditions.checkNotNull(script); Preconditions.checkNotNull(options);
     * Preconditions.checkNotNull(options.name); Preconditions.checkNotNull(options.processCommand); if (options.usage == null) options.usage = "/" + options.name +
     * ": scripted command - no description"; if (options.permission == null) options.permission = ModuleJScripting.PERM + ".command." + options.name; this.script = script;
     * this.options = options; }
     */
    public CommandJScriptCommand(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return null;// options.name;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return null;// options.opOnly ? DefaultPermissionLevel.OP : DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return null;// options.permission;
    }

    /*
     * @Override public void parse() throws CommandException { try { if (arguments.isTabCompletion) { if (options.tabComplete != null) //script.call(options.tabComplete,
     * options.tabComplete, new JsCommandArgs(arguments)); } else { //script.call(options.processCommand, options.processCommand, new JsCommandArgs(arguments)); } } catch
     * (NoSuchMethodException e) { e.printStackTrace(); throw new TranslatedCommandException("Script error: method not found: " + e.getMessage()); } catch (ScriptException e) {
     * e.printStackTrace(); throw new TranslatedCommandException(e.getMessage()); } }
     * 
     * public static void processArguments(CommandParserArgs args) { for (int i = 0; i < args.size(); i++) { Matcher matcher = ARGUMENT_PATTERN.matcher(args.get(i)); if
     * (!matcher.matches()) { continue; }
     * 
     * String modifier = matcher.group(1).toLowerCase(); String rest = matcher.group(2);
     * 
     * ScriptArgument argument = ScriptArguments.get(modifier); if (argument != null) { args.args.set(i, argument.process(args.sender) + rest); } else { try { int idx =
     * Integer.parseInt(modifier); if (idx >= args.size()) throw new SyntaxException("Missing argument @%d", idx); args.args.set(i, args.get(idx) + rest); } catch
     * (NumberFormatException e) { throw new SyntaxException("Unknown argument modifier \"%s\"", modifier); } } } }
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return null;
    }
}
