package com.forgeessentials.jscripting.fewrapper.fe;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.script.ScriptException;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.command.CommandJScriptCommand;
import com.forgeessentials.jscripting.wrapper.mc.JsICommandSender;
import com.forgeessentials.util.PlayerInfo;

/**
 * @tsd.interface FEServer
 */
public class JsFEServer
{

    private ScriptInstance script;

    private JsICommandSender server;

    public JsFEServer(ScriptInstance script)
    {
        this.script = script;
    }

    /**
     * Registers a new command in the game. <br>
     * The processCommand and tabComplete handler can be the same, if the processCommand handler properly checks for args.isTabCompletion.
     *
     * @tsd.def registerCommand(options: CommandOptions): void;
     */
    public void registerCommand(Object options) throws ScriptException
    {
        JsCommandOptions opt = script.getProperties(new JsCommandOptions(), options, JsCommandOptions.class);
        script.registerScriptCommand(new CommandJScriptCommand(script, opt));
    }

    /**
     * Returns the total number of unique players that have connected to this server
     */
    public int getUniquePlayerCount()
    {
        return APIRegistry.perms.getServerZone().getKnownPlayers().size();
    }
    /**
     * Returns the list of players who have ever connected.
     */
    public Set<UserIdent> getAllPlayers()
    {
        return APIRegistry.perms.getServerZone().getKnownPlayers();
    }
    /**
     * Returns the amount of time this player was active on the server in seconds
     */
    public long getTimePlayed(UUID playerId)
    {
        PlayerInfo pi = PlayerInfo.get(playerId);
        return pi == null ? 0 : pi.getTimePlayed() / 1000;
    }

    public Date getLastLogout(UUID playerId)
    {
        PlayerInfo pi = PlayerInfo.get(playerId);
        return pi == null ? null : pi.getLastLogout();
    }

    public Date getLastLogin(UUID playerId)
    {
        PlayerInfo pi = PlayerInfo.get(playerId);
        return pi == null ? null : pi.getLastLogin();
    }


}
