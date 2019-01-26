package com.forgeessentials.permissions.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.permissions.core.ZonePersistenceProvider;
import com.forgeessentials.util.ServerUtil;

public class SingleFileProvider extends ZonePersistenceProvider
{

    private File file;

    public SingleFileProvider()
    {
        file = new File(ServerUtil.getWorldPath(), "FEData/permissions.json");
    }

    public SingleFileProvider(File file)
    {
        this.file = file;
    }

    @Override
    public ServerZone load()
    {
        try (BufferedReader in = Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8")))
        {
            ServerZone serverZone = DataManager.getGson().fromJson(in, ServerZone.class);
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
        try (BufferedWriter out = Files.newBufferedWriter(file.toPath(), Charset.forName("UTF-8")))
        {
            out.write(DataManager.getGson().toJson(serverZone));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
