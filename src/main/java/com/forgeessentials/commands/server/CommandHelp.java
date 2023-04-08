package com.forgeessentials.commands.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.compat.HelpFixer;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

public class CommandHelp extends ForgeEssentialsCommandBuilder
{
    CommandDispatcher<CommandSource> dispatcher;
    public CommandHelp(boolean enabled, CommandDispatcher<CommandSource> disp)
    {
        this(enabled);
        dispatcher = disp;
    }

    public CommandHelp(boolean enabled)
    {
        super(enabled);

    }

    public static List<String> messages=new ArrayList<>();

    public static Integer entriesPerPage=8;

    public static Integer commandColor=2;

    public static Integer subCommandColor=7;
    
    public HelpFixer fixer;

    @Override
    public String getPrimaryAlias()
    {
        return "help";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "?" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.commands.help";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .executes(CommandContext -> execute(CommandContext, "empty"))
                .then(Commands.argument("page", IntegerArgumentType.integer(0, 1000))
                        .executes(CommandContext -> execute(CommandContext, "page")
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("empty"))
        {
            showHelpPage(ctx);
        }
        else
        {
            int page = IntegerArgumentType.getInteger(ctx, "page");
            showHelpPage(ctx, page);
        }
        return Command.SINGLE_SUCCESS;
    }
/*
    public void sendCommandUsageMessage(CommandSource sender, ICommand command, TextFormatting color)
    {
        ITextComponent chatMsg = new TranslationTextComponent(command.getUsage(sender));
        chatMsg.getStyle().withColor(color);
        chatMsg.getStyle().withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/" + command.getName() + " "));
        ChatOutputHandler.sendMessage(sender, chatMsg);
    }*/

    public void showHelpPage(CommandContext<CommandSource> ctx) throws CommandException
    {
        if (messages==null||messages.size()==0)
            showHelpPage(ctx, 1);
        for (int i = 0; i < messages.size(); i++)
            ChatOutputHandler.chatConfirmation(ctx.getSource(), ScriptArguments.processSafe(messages.get(i), ctx.getSource()));
    }

    public void showHelpPage(CommandContext<CommandSource> ctx, int page) throws CommandException
    {
        List<String> scmds = new ArrayList<String>();
        Map<CommandNode<CommandSource>, String> map = dispatcher.getSmartUsage(dispatcher.getRoot(), ctx.getSource());
        for(String s : map.values()) {
            String scmd = "/" + s;
            scmds.add(scmd);
        }
        //TODO add worldedit command filtering
        Collections.sort(scmds);
        int amountperpage = entriesPerPage;
        int totalcount = scmds.size();
        int totalpages = (int)Math.ceil(totalcount / (float)amountperpage);
        
        if (page > totalpages) {
            page = totalpages;
        }
        
        TextFormatting commandcolour = TextFormatting.getById(commandColor);
        TextFormatting subcommandcolour = TextFormatting.getById(subCommandColor);
        
        ChatOutputHandler.sendMessage(ctx.getSource(), "#####################################################", TextFormatting.WHITE);
        
        for (int n = 0; n < ((amountperpage * page)); n++) {
            if (n >= ((amountperpage * page) - amountperpage)) {
                if (scmds.size() < n+1) {
                    break;
                }
                
                String commandline = scmds.get(n);
                String[] cmdlspl = commandline.split(" ");
                String acmd = cmdlspl[0];
                String csuffix = commandline.replaceAll(acmd, "");
                
                TextComponent tc = new StringTextComponent("");

                TextComponent tc0 = new StringTextComponent(acmd);
                tc0.withStyle(commandcolour);
                tc.append(tc0);

                TextComponent tc1 = new StringTextComponent(csuffix);
                tc1.withStyle(subcommandcolour);
                tc.append(tc1);
                
                ChatOutputHandler.sendMessage(ctx.getSource(), tc);
            }
        }
        ChatOutputHandler.sendMessage(ctx.getSource(), " Page " + page + " / " + totalpages + ", /help <page>", TextFormatting.YELLOW);
    }
}
