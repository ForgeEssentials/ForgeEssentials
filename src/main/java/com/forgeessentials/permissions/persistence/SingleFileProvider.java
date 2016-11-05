package com.forgeessentials.permissions.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.permissions.core.ZonePersistenceProvider;
import com.forgeessentials.util.data.DataUtils;
import com.forgeessentials.util.Utils;

public class SingleFileProvider extends ZonePersistenceProvider
{

    private File file;

    public SingleFileProvider()
    {
        file = new File(Utils.getWorldPath(), "FEData/permissions.json");
    }

    public SingleFileProvider(File file)
    {
        this.file = file;
    }

    @Override
    public ServerZone load()
    {
        try (BufferedReader in = new BufferedReader(new FileReader(file)))
        {
            ServerZone serverZone = DataUtils.getGson().fromJson(in, ServerZone.class);
            if (serverZone == null)
                return null;
            serverZone.afterLoad();
            readUserGroupPermissions(serverZone);
            return serverZone;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(ServerZone serverZone)
    {
        writeUserGroupPermissions(serverZone);
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file)))
        {
            out.write(DataUtils.getGson().toJson(serverZone));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
