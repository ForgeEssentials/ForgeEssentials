package com.forgeessentials.jscripting.fewrapper.fe;

import java.util.UUID;

import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.jscripting.wrapper.JsWrapper;

public class JsPlayerInfo extends JsWrapper<PlayerInfo>
{
    public JsPlayerInfo(PlayerInfo that)
    {
        super(that);
    }

    public JsPlayerInfo(UUID that)
    {
        super(PlayerInfo.get(that));
    }

    public JsUserIdent getUserIdent()
    {
        return new JsUserIdent(that.ident);
    }

    public void removeTimeout(String name)
    {
        that.removeTimeout(name);
    }

}
