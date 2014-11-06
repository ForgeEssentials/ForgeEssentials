package com.forgeessentials.permissions.persistence;

import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.permissions.core.IZonePersistenceProvider;
import com.forgeessentials.util.EnumDBType;
import com.forgeessentials.util.OutputHandler;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLProvider implements IZonePersistenceProvider {

    private static SQLProvider INSTANCE;

    // Prefix
    private static final String TABLE_PREFIX = "fepermissions_";

    // Tables
    private static final Map<String,String> TABLES;
    static
    {
        TABLES = new HashMap<String, String>();
        TABLES.put("ZONE", TABLE_PREFIX + "zone");
        TABLES.put("ZONE_GROUP", TABLE_PREFIX + "zonegroup");
        TABLES.put("ZONE_PERMISSIONS", TABLE_PREFIX + "zonepermissions");
        TABLES.put("GROUP_PERMISSIONS", TABLE_PREFIX + "grouppermissions");
        TABLES.put("PLAYER_PERMISSIONS", TABLE_PREFIX + "playerpermissions");
    }

    private Connection db;
    private EnumDBType dbType;

    public SQLProvider()
    {
        db = ModulePermissions.config.getDBConnector().getChosenConnection();
        dbType = ModulePermissions.config.getDBConnector().getActiveType();

        try
        {
            checkVersion();
            prepareStatements(dbType);
        }
        catch (SQLException e)
        {
            try
            {
                createTables();
                checkVersion();
                prepareStatements(dbType);
            }
            catch (Exception e2)
            {
                Throwables.propagate(e2);
            }
        }
    }

	@Override
	public void save(ServerZone serverZone)
	{
        
        // Clear groups from players (leftovers, if player was removed from all groups)
        for (UserIdent ident : serverZone.getPlayerPermissions().keySet())
        {
            serverZone.clearPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
        }
        // Add groups to players
        for (Entry<UserIdent, Set<String>> entry : serverZone.getPlayerGroups().entrySet())
        {
            serverZone.setPlayerPermissionProperty(entry.getKey(), FEPermissions.PLAYER_GROUPS, StringUtils.join(entry.getValue(), ","));
        }

        try {
            // Use a transaction to be able to rollback if there is an error
            db.setAutoCommit(false);

            saveServerZone(serverZone);
            //saveZonePermissions(path, serverZone);

            for (WorldZone worldZone : serverZone.getWorldZones().values())
            {
                String worldName = worldZone.getName();
                //saveWorldZone(worldName, worldZone);
                //saveZonePermissions(worldName, worldZone);
                for (AreaZone areaZone : worldZone.getAreaZones())
                {
                    String areaName = areaZone.getName();
                    //saveAreaZone(areaPath, areaZone);
                    //saveZonePermissions(areaPath, areaZone);
                }
            }

            db.commit();
        }catch (SQLException se){
            try{
                db.rollback();
            }catch (SQLException se2){

            }
        }
	}

    public void saveServerZone(ServerZone zone) throws SQLException
    {
            /*Statement pstat = db.prepareStatement("UPDATE");

            // Add rows to a batch in a loop. Each iteration adds a
            // new row.
            for (int i = 0; i < firstNames.length; i++) {
                // Add each parameter to the row.
                pstmt.setInt(1, i + 1);
                pstmt.setString(2, lastNames[i]);
                pstmt.setString(3, firstNames[i]);
                pstmt.setString(4, emails[i]);
                pstmt.setString(5, phoneNumbers[i]);
                // Add row to the batch.
                pstmt.addBatch();
            }*/
    }

	@Override
	public ServerZone load()
	{
        try {
            // Check if tables exists and if not build them
            checkAndBuildTables();

            Statement statement = db.createStatement();
            ResultSet result = null;

            // Load zones to hashmap
            result = statement.executeQuery("SELECT id,name,type,parent FROM `"+TABLES.get("ZONE")+"` ORDER BY type,parent");
            Map zones = resultSetToMap(result, "zoneId");

            // Load groups to hashmap
            result = statement.executeQuery("SELECT groupId,zoneId,groupName,groupParent FROM `"+TABLES.get("ZONE_GROUP")+"`");
            Map groups = resultSetToMap(result, "groupId");

            // Load zone permissions to hashmap
            result = statement.executeQuery("SELECT zoneId,permissionKey,permissionValue FROM `"+TABLES.get("ZONE_PERMISSIONS")+"`");
            Map zonePermissions = resultSetPermissionMap(result, "zoneId");

            // Load group permissions to hashmap
            result = statement.executeQuery("SELECT groupId,permissionKey,permissionValue FROM `"+TABLES.get("GROUP_PERMISSIONS")+"`");
            Map groupPermissions = resultSetPermissionMap(result, "groupId");

            // Load player permissions to hashmap
            result = statement.executeQuery("SELECT playerId,zoneId,permissionKey,permissionValue FROM `"+TABLES.get("PLAYER_PERMISSIONS")+"`");
            Map playerPermissions = resultSetPermissionMap(result, "playerId");

            Map<Integer,WorldZone> worldZonesReference = new HashMap();
            Map<Integer,AreaZone> areaZonesReference = new HashMap();

            // Make sure the server zone exists
            if(zones.containsKey(1)){
                Map serverZoneProperties = (HashMap) zones.get(1);
                if(serverZoneProperties.get("type") == 1){
                    ServerZone serverZone = new ServerZone();
                    int maxId = 2;
                    for(Object zoneObject : zones.entrySet())
                    {
                        Map zoneInformation = (HashMap) zoneObject;
                        // Ignore zone with id equals to one or less (Server,Root)
                        if((int)zoneInformation.get("zoneId") <= 1)
                            continue;
                        Map zoneProperties = (HashMap) zonePermissions.get(zoneInformation.get("zoneId"));
                        // If zone type is World(2)
                        if(zoneProperties.get("type") == 2 && !worldZonesReference.containsKey((int)zoneInformation.get("zoneId")) && zoneInformation.containsKey("id") && zoneProperties.containsKey("dimId")){
                            WorldZone worldZone = new WorldZone(serverZone,(int)zoneProperties.get("dimId"),(int)zoneInformation.get("id"));

                        }
                        // If zone type is Area(3)
                        else if(zoneProperties.get("type") == 3){


                        }
                    }

                }
            }


            // Create ServerZone
            //ServerZone serverZone = new ServerZone();
        }
        catch (Exception e)
        {
            OutputHandler.felog.severe("Error loading permissions");
            e.printStackTrace();
            return null;
        }
		return null;
	}

    /**
     *
     * @throws SQLException
     */
    private void checkAndBuildTables() throws SQLException
    {
        ResultSet tableResourceSet = null;
        DatabaseMetaData dbm = db.getMetaData();
        tableResourceSet = dbm.getTables(null, null, TABLE_PREFIX + "%", null);

        Integer count = 0;

        while (tableResourceSet.next())
        {
            if(TABLES.values().contains(tableResourceSet.getString(3)))
            {
                count++;
            }
        }

        if(count != TABLES.size())
        {
            Statement statement = db.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `"+TABLES.get("ZONE")+"` (`id` int(11) NOT NULL,`name` varchar(255) NOT NULL,`type` int(11) NOT NULL,`parent` int(11) NOT NULL,PRIMARY KEY (`id`))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `"+TABLES.get("ZONE_GROUP")+"` (`id` int(11) NOT NULL,`zoneId` int(11) NOT NULL,`name` int(255) NOT NULL,`parent` int(11) NOT NULL, PRIMARY KEY (`id`,`zoneId`))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `"+TABLES.get("ZONE_PERMISSIONS")+"` (`zoneId` int(11) NOT NULL,`permissionKey` varchar(255) NOT NULL,`permissionValue` varchar(255) NOT NULL, PRIMARY KEY (`zoneId`,`permissionKey`))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `"+TABLES.get("GROUP_PERMISSIONS")+"` (`groupId` int(11) NOT NULL,`permissionKey` varchar(255) NOT NULL,`permissionValue` varchar(255) NOT NULL, PRIMARY KEY (`groupId`,`permissionKey`))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `"+TABLES.get("PLAYER_PERMISSIONS")+"` (`playerUuid` varchar(100) NOT NULL,`permissionKey` varchar(255) NOT NULL,`permissionValue` varchar(255)  NOT NULL, PRIMARY KEY (`playerUuid`,`permissionKey`))");
        }
    }

    private Map resultSetToMap(ResultSet rs,String keyFieldName) throws SQLException
    {
        ResultSetMetaData md = rs.getMetaData();
        int columnsCount = md.getColumnCount();
        Map list = new HashMap<Integer,HashMap>();
        while (rs.next())
        {
            Map row = new HashMap(columnsCount);
            for(int columnIndex=1; columnIndex<=columnsCount; ++columnIndex)
            {
                row.put(md.getColumnName(columnIndex),rs.getObject(columnIndex));
            }
            list.put(row.get(keyFieldName), row);
        }
        return list;
    }

    private Map resultSetPermissionMap(ResultSet rs,String keyFieldName) throws SQLException
    {
        ResultSetMetaData md = rs.getMetaData();
        int columnsCount = md.getColumnCount();
        Map list = new HashMap<Integer,HashMap>();
        while (rs.next())
        {
            Map row = new HashMap(columnsCount);
            row.put(rs.getObject(2),rs.getObject(3));
            list.put(row.get(keyFieldName), row);
        }
        return list;
    }
}
