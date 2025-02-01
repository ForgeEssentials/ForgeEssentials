package com.forgeessentials.commons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class Packet6AuthLogin implements IMessage
{
    /*
    0 = request to get hash from client (hash will be empty!)
    1 = reply from client with hash (empty if client does not have hash)
    2 = request to put hash in client keystore
    3 = reply from client on keystore save (hash will be either SUCCESS or FAILURE)
     */
    public int mode;

    public String hash;

    // dummy ctor
    public Packet6AuthLogin()
    {
    }

    public Packet6AuthLogin(int mode, String hash)
    {
        this.mode = mode;
        this.hash = hash;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        mode = buf.readInt();
        hash = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(mode);
        ByteBufUtils.writeUTF8String(buf, hash);
    }
}