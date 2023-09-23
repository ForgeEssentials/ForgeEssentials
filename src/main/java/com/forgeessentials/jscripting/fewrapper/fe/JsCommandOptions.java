package com.forgeessentials.jscripting.fewrapper.fe;

import java.util.List;

import com.forgeessentials.jscripting.fewrapper.fe.command.JsCommandNodeWrapper;

public class JsCommandOptions
{

    public String name;

    /**
     * @tsd.optional
     */
    public String usage;

    /**
     * @tsd.optional
     */
    public Boolean opOnly = true;

	public Boolean executesMethod = false;

	/**
     * @tsd.optional
     */
	public String executionParams;

    /**
     * Don't EVER USE THIS! INTERNAL USE ONLY! <br>
     * @tsd.optional
     */
    public List<JsCommandNodeWrapper> listsSubNodes;

    /**
     * Don't implement this directly, instead use subNode[anyLetterNumber] (ex. subNode4, subNodeXYZ)
     * @tsd.optional 
     */
    public Object subNode;
    
    /**
     * @tsd.type CommandCallback
     */
    public Object processCommand;

}
