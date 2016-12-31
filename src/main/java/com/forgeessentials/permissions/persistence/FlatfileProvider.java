package com.forgeessentials.permissions.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.permissions.core.ZonePersistenceProvider;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.LoggingHandler;

public class FlatfileProvider extends ZonePersistenceProvider
{

    public static final String PERMISSION_FILE_EXT = ".txt";

    private File basePath;

    public static final FileFilter permissionFilter = new FileFilters.Extension(PERMISSION_FILE_EXT);

    public static final FileFilter directoryFilter = new FileFilters.Directory();

    public static class SortedPermisssionProperties extends Properties
    {
        private static final long serialVersionUID = 1L;

        @Override
        public synchronized Enumeration<Object> keys()
        {
            TreeSet<Object> keys = new TreeSet<Object>(Zone.permissionComparator);
            keys.addAll(super.keySet());
            return Collections.enumeration(keys);
        }
    }

    public static final String COMMENT_INFO = "\nDO NOT MODIFY OR REMOVE fe.internal PERMISSIONS UNLESS YOU KNOW WHAT YOU DO!"
            + "\nAfter you modified permissions in this file, remember to directly run \"/feperm reload\", or the changes get overwritten next time permissions are saved by the server.";

    // ------------------------------------------------------------

    public FlatfileProvider()
    {
        this.basePath = new File(ServerUtil.getWorldPath(), "FEData/permissions");
    }

    public FlatfileProvider(File path)
    {
        this.basePath = path;
    }

    // ------------------------------------------------------------
    // -- Saving
    // ------------------------------------------------------------

    public static void deleteDirectory(File dir)
    {
        try
        {
            if (dir.exists())
                FileUtils.deleteDirectory(dir);
        }
        catch (IOException e)
        {
            /* do nothing */
        }
    }

    @Override
    public void save(ServerZone serverZone)
    {
        File path = basePath;
        deleteDirectory(path);

        writeUserGroupPermissions(serverZone);

        saveServerZone(path, serverZone);
        saveZonePermissions(path, serverZone);
        for (WorldZone worldZone : serverZone.getWorldZones().values())
        {
            File worldPath = new File(path, worldZone.getName());
            saveWorldZone(worldPath, worldZone);
            saveZonePermissions(worldPath, worldZone);
            for (AreaZone areaZone : worldZone.getAreaZones())
            {
                File areaPath = new File(worldPath, areaZone.getName());
                saveAreaZone(areaPath, areaZone);
                saveZonePermissions(areaPath, areaZone);
            }
        }
    }

    public static void saveServerZone(File path, ServerZone serverZone)
    {
        // Store zone information
        path.mkdirs();

        Properties p = new Properties();

        p.setProperty("id", Integer.toString(serverZone.getId()));
        p.setProperty("maxZoneId", Integer.toString(serverZone.getMaxZoneID()));

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(path, "server.xml"))))
        {
            p.storeToXML(os, "Data of server");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveWorldZone(File path, WorldZone worldZone)
    {
        path.mkdirs();

        Properties p = new Properties();

        p.setProperty("id", Integer.toString(worldZone.getId()));
        p.setProperty("dimId", Integer.toString(worldZone.getDimensionID()));

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(path, "world.xml"))))
        {
            p.storeToXML(os, "Data of world " + worldZone.getName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveAreaZone(File path, AreaZone areaZone)
    {
        path.mkdirs();

        Properties p = new Properties();

        p.setProperty("id", Integer.toString(areaZone.getId()));
        p.setProperty("name", areaZone.getName());
        p.setProperty("x1", Integer.toString(areaZone.getArea().getLowPoint().getX()));
        p.setProperty("y1", Integer.toString(areaZone.getArea().getLowPoint().getY()));
        p.setProperty("z1", Integer.toString(areaZone.getArea().getLowPoint().getZ()));
        p.setProperty("x2", Integer.toString(areaZone.getArea().getHighPoint().getX()));
        p.setProperty("y2", Integer.toString(areaZone.getArea().getHighPoint().getY()));
        p.setProperty("z2", Integer.toString(areaZone.getArea().getHighPoint().getZ()));
        p.setProperty("shape", areaZone.getShape().toString());

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(path, "area.xml"))))
        {
            p.storeToXML(os, "Data of area " + areaZone.getName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveZonePermissions(File path, Zone zone)
    {
        File playersPath = new File(path, "players");
        File groupsPath = new File(path, "groups");
        for (Entry<UserIdent, PermissionList> entry : zone.getPlayerPermissions().entrySet())
        {
            // Get filename and info
            String username = entry.getKey().getUsername() == null ? entry.getKey().getUuid().toString() : entry.getKey().getUsername();
            UUID uuid = entry.getKey().getUuid();
            String filename = username == null ? uuid.toString() : username;
            String comment = "Permissions for user " + (username != null ? username : "<unknown-username>") + " with UUID "
                    + (uuid != null ? uuid.toString() : "<unknown-uuid>") + COMMENT_INFO;
            filename = filename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            // prevent overwriting files with same playername
            while (new File(playersPath, filename + PERMISSION_FILE_EXT).exists())
                filename = filename + "_";

            // Save permissions
            Properties p = permissionListToProperties(entry.getValue());
            if (entry.getKey().getUsername() != null)
                p.setProperty(FEPermissions.PLAYER_NAME, entry.getKey().getUsername());
            if (entry.getKey().getUuid() != null)
                p.setProperty(FEPermissions.PLAYER_UUID, entry.getKey().getUuid().toString());
            saveProperties(p, playersPath, filename + PERMISSION_FILE_EXT, comment);
        }
        for (Entry<String, PermissionList> entry : zone.getGroupPermissions().entrySet())
        {
            // Get filename and info
            String comment = "Permissions for group " + entry.getKey() + COMMENT_INFO;

            // Save permissions
            Properties p = permissionListToProperties(entry.getValue());
            saveProperties(p, groupsPath, entry.getKey() + PERMISSION_FILE_EXT, comment);
        }
    }

    public static Properties permissionListToProperties(PermissionList list)
    {
        Properties p = new SortedPermisssionProperties();
        for (Entry<String, String> permission : list.entrySet())
            p.setProperty(permission.getKey(), permission.getValue() != null ? permission.getValue() : "");
        return p;
    }

    public static void saveProperties(Properties properties, File path, String filename, String comment)
    {
        path.mkdirs();
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(path, filename))))
        {
            properties.store(os, comment);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------

    // ------------------------------------------------------------
    // -- Loading
    // ------------------------------------------------------------

    @Override
    public ServerZone load()
    {
        File path = basePath;
        try
        {
            // Create ServerZone and load permissions
            ServerZone serverZone = new ServerZone();
            loadZonePermissions(path, serverZone);

            readUserGroupPermissions(serverZone);

            int maxId = 2;

            File[] worldDirs = path.listFiles(directoryFilter);
            if (worldDirs == null)
            {
                LoggingHandler.felog.error("Error loading permissions: invalid path");
                return null;
            }
            for (File worldPath : worldDirs)
            {
                File worldFile = new File(worldPath, "world.xml");
                if (!worldFile.exists())
                    continue;
                Properties worldProperties = new Properties();
                try (InputStream is = new BufferedInputStream(new FileInputStream(worldFile)))
                {
                    worldProperties.loadFromXML(is);
                }
                catch (NumberFormatException | IOException e)
                {
                    LoggingHandler.felog.error("Error reading world " + worldPath.getName());
                    continue;
                }

                // Read world data
                int worldId = Integer.parseInt(worldProperties.getProperty("id"));
                maxId = Math.max(maxId, worldId);

                int dimensionID = Integer.parseInt(worldProperties.getProperty("dimId"));

                // Create WorldZone and load permissions
                WorldZone worldZone = new WorldZone(serverZone, dimensionID, worldId);
                loadZonePermissions(worldPath, worldZone);

                for (File areaPath : worldPath.listFiles(directoryFilter))
                {
                    File areaFile = new File(areaPath, "area.xml");
                    if (!areaFile.exists())
                        continue;
                    Properties areaProperties = new Properties();
                    try (InputStream is = new BufferedInputStream(new FileInputStream(areaFile)))
                    {
                        areaProperties.loadFromXML(is);
                    }
                    catch (NumberFormatException | IOException e)
                    {
                        LoggingHandler.felog.error("Error reading area " + worldPath.getName() + "/" + areaPath.getName());
                        continue;
                    }

                    // Read area data
                    int areaId = Integer.parseInt(areaProperties.getProperty("id"));
                    maxId = Math.max(maxId, areaId);

                    String name = areaProperties.getProperty("name");
                    int x1 = Integer.parseInt(areaProperties.getProperty("x1"));
                    int y1 = Integer.parseInt(areaProperties.getProperty("y1"));
                    int z1 = Integer.parseInt(areaProperties.getProperty("z1"));
                    int x2 = Integer.parseInt(areaProperties.getProperty("x2"));
                    int y2 = Integer.parseInt(areaProperties.getProperty("y2"));
                    int z2 = Integer.parseInt(areaProperties.getProperty("z2"));
                    AreaShape shape = AreaShape.getByName(areaProperties.getProperty("shape"));
                    if (name == null)
                        throw new IllegalArgumentException();

                    // Create AreaZone and load permissions
                    AreaZone areaZone = new AreaZone(worldZone, name, new AreaBase(new Point(x1, y1, z1), new Point(x2, y2, z2)), areaId);
                    if (shape != null)
                        areaZone.setShape(shape);
                    loadZonePermissions(areaPath, areaZone);
                }
            }

            File serverFile = new File(path, "server.xml");
            if (serverFile.exists())
            {
                try
                {
                    Properties serverProperties = new Properties();
                    serverProperties.loadFromXML(new BufferedInputStream(new FileInputStream(serverFile)));
                    serverZone.setMaxZoneId(Integer.parseInt(serverProperties.getProperty("maxZoneId")));
                }
                catch (IllegalArgumentException | IOException e)
                {
                    LoggingHandler.felog.error("Error reading server data " + serverFile.getName());
                    serverZone.setMaxZoneId(maxId);
                }
            }
            else
            {
                serverZone.setMaxZoneId(maxId);
            }

            return serverZone;
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error("Error loading permissions");
            e.printStackTrace();
            return null;
        }
    }

    public static void loadZonePermissions(File path, Zone zone)
    {
        File playersPath = new File(path, "players");
        File groupsPath = new File(path, "groups");

        if (playersPath.exists())
        {
            for (File file : playersPath.listFiles(permissionFilter))
            {
                Properties p = new Properties();
                try (InputStream is = new BufferedInputStream(new FileInputStream(file)))
                {
                    p.load(is);
                }
                catch (IOException e)
                {
                    LoggingHandler.felog.error("Error reading permissions from " + path.getAbsolutePath());
                    continue;
                }

                // Get player
                String username = p.getProperty(FEPermissions.PLAYER_NAME);
                String uuid = p.getProperty(FEPermissions.PLAYER_UUID);
                p.remove(FEPermissions.PLAYER_NAME);
                p.remove(FEPermissions.PLAYER_UUID);
                if (username == null && uuid == null)
                {
                    LoggingHandler.felog.error("User identification missing in " + path.getAbsolutePath());
                    continue;
                }
                UserIdent ident = UserIdent.get(uuid, username);

                // Load permissions
                PermissionList permissions = zone.getOrCreatePlayerPermissions(ident);
                for (Entry<?, ?> permission : p.entrySet())
                {
                    permissions.put((String) permission.getKey(), (String) permission.getValue());
                }
            }

        }

        if (groupsPath.exists())
        {
            for (File file : groupsPath.listFiles(permissionFilter))
            {
                Properties p = new Properties();
                try (InputStream is = new BufferedInputStream(new FileInputStream(file)))
                {
                    p.load(is);
                }
                catch (IOException e)
                {
                    LoggingHandler.felog.error("Error reading permissions from " + path.getAbsolutePath());
                    continue;
                }

                // Get group
                String groupName = file.getName().substring(0, file.getName().length() - PERMISSION_FILE_EXT.length());

                // Load permissions
                PermissionList permissions = zone.getOrCreateGroupPermissions(groupName);
                for (Entry<?, ?> permission : p.entrySet())
                {
                    permissions.put((String) permission.getKey(), (String) permission.getValue());
                }
            }

        }
    }

}
