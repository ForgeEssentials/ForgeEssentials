package com.forgeessentials.mapper.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.codec.binary.Base64;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.mapper.ModuleMapper;
import com.forgeessentials.mapper.remote.QueryRegionHandler.Request;

@FERemoteHandler(id = "mapper.query.region")
public class QueryRegionHandler extends GenericRemoteHandler<Request>
{

    public static final String PERM = PERM_REMOTE + ".mapper.query.region";

    public QueryRegionHandler()
    {
        super(PERM, Request.class);
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.ALL, "Allows querying region tiles");
    }

    @Override
    public synchronized RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<Request> request)
    {
        if (request.data == null)
            error("missing data");
        WorldServer world = DimensionManager.getWorld(request.data.dim);
        if (world == null)
            error("Invalid dimension");
        try
        {
            File file = ModuleMapper.getInstance().getRegionFileAsync(world, request.data.x, request.data.z).get();
            if (!file.exists())
                error("Error getting tile");
            int length = (int) file.length();
            byte[] data = new byte[length];
            try (FileInputStream is = new FileInputStream(file))
            {
                is.read(data);
            }
            return new RemoteResponse<String>(request, Base64.encodeBase64String(data));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            error("Error getting tile");
            return null;
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            error("Error getting tile");
            return null;
        }
    }

    public static class Request
    {

        public int dim;

        public int x;

        public int z;

    }

}
