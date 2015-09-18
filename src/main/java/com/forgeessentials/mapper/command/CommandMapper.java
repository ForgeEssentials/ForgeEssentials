package com.forgeessentials.mapper.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.mapper.MapperUtil;
import com.forgeessentials.mapper.ModuleMapper;
import com.forgeessentials.util.CommandParserArgs;

public class CommandMapper extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "mapper";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/mapper: Manage mapper settings";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.mapper.command";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        // TODO Auto-generated method stub
        int x = (int) Math.floor(arguments.senderPlayer.posX);
        int z = (int) Math.floor(arguments.senderPlayer.posZ);
        int chunkX = MapperUtil.worldToChunk(x);
        int chunkZ = MapperUtil.worldToChunk(z);
        WorldServer world = (WorldServer) arguments.senderPlayer.worldObj;

        Future<BufferedImage> image = ModuleMapper.getInstance().getChunkImageAsync(world, chunkX, chunkZ);
        try
        {
            ImageIO.write(image.get(), "png", new File(ForgeEssentials.getFEDirectory(), "chunk.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }

        BufferedImage img = ModuleMapper.getInstance().getRegionImage(world, MapperUtil.chunkToRegion(chunkX), MapperUtil.chunkToRegion(chunkZ));
        try
        {
            ImageIO.write(img, "png", new File(ForgeEssentials.getFEDirectory(), "region.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
