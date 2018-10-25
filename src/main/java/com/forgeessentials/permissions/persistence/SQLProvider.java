package com.forgeessentials.permissions.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.permissions.core.ZonePersistenceProvider;
import com.forgeessentials.util.EnumDBType;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Throwables;

//FIXME: This class should be modified to use PreparedStatements instead of dynamic sql
public class SQLProvider extends ZonePersistenceProvider
{

    private class TableInfo
    {

        public String name;

        public Map<String, String> columns = new HashMap<>();

        public Set<String> primaryKeys = new HashSet<>();

        public Set<String> nullableKeys = new HashSet<>();

        public TableInfo(String name)
        {
            this.name = name;
        }

        public String getCreateStatement()
        {
            StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS `");
            sb.append(name);
            sb.append("` (");
            for (Entry<String, String> col : columns.entrySet())
            {
                sb.append("`");
                sb.append(col.getKey());
                sb.append("` ");
                sb.append(col.getValue());
                if (nullableKeys.contains(col.getKey()))
                    sb.append(", ");
                else
                    sb.append(" NOT NULL, ");
            }
            sb.append("PRIMARY KEY (`");
            sb.append(StringUtils.join(primaryKeys, "`, `"));
            sb.append("`))");
            return sb.toString();
        }

        public String createSelectStatement(Collection<?> fields)
        {
            for (Object f : fields)
                if (!columns.containsKey(f))
                    throw new RuntimeException("Error in select statement.");
            StringBuilder sb = new StringBuilder("SELECT `");
            sb.append(StringUtils.join(fields, "`, `"));
            sb.append("` FROM `");
            sb.append(name);
            sb.append("`");
            return sb.toString();
        }

        public String createSelectStatement(String[] fields)
        {
            return createSelectStatement(Arrays.asList(fields));
        }

        public String createSelectStatement()
        {
            StringBuilder sb = new StringBuilder("SELECT * FROM `");
            sb.append(name);
            sb.append("`");
            return sb.toString();
        }

        public String createInsertOrReplace(Map<String, Object> fieldsAndValues)
        {
            for (String f : fieldsAndValues.keySet())
                if (!columns.containsKey(f))
                    throw new RuntimeException("Error in select statement.");
            StringBuilder sb = new StringBuilder();
            if (dbType == EnumDBType.H2_FILE)
                sb.append("MERGE INTO `");
            else
                sb.append("REPLACE INTO `");
            sb.append(name);
            sb.append("` (`");
            sb.append(StringUtils.join(fieldsAndValues.keySet(), "`, `"));
            sb.append("`) VALUES ('");
            sb.append(StringUtils.join(fieldsAndValues.values(), "', '"));
            sb.append("')");
            return sb.toString();
        }

        public String createTruncate()
        {
            return "TRUNCATE TABLE `" + name + "`";
        }

        public List<Map<String, Object>> loadList() throws SQLException
        {
            ResultSet resultSet = db.createStatement().executeQuery(createSelectStatement());
            ResultSetMetaData meta = resultSet.getMetaData();
            int columnsCount = meta.getColumnCount();
            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next())
            {
                Map<String, Object> row = new HashMap<>(columnsCount);
                for (int columnIndex = 1; columnIndex <= columnsCount; ++columnIndex)
                    row.put(meta.getColumnName(columnIndex).toLowerCase(), resultSet.getObject(columnIndex));
                list.add(row);
            }
            return list;
        }

        @SuppressWarnings("unused")
        public Map<Integer, Map<String, Object>> loadIntMap(String key) throws SQLException
        {
            ResultSet resultSet = db.createStatement().executeQuery(createSelectStatement());
            ResultSetMetaData meta = resultSet.getMetaData();
            int columnsCount = meta.getColumnCount();
            Map<Integer, Map<String, Object>> list = new HashMap<>();
            while (resultSet.next())
            {
                Map<String, Object> row = new HashMap<>(columnsCount);
                for (int columnIndex = 1; columnIndex <= columnsCount; ++columnIndex)
                    row.put(meta.getColumnName(columnIndex).toLowerCase(), resultSet.getObject(columnIndex));
                list.put(resultSet.getInt(key), row);
            }
            return list;
        }

        public Map<String, String> loadMap(String key, String value) throws SQLException
        {
            List<String> fields = new ArrayList<>();
            fields.add(key);
            fields.add(value);
            ResultSet resultSet = db.createStatement().executeQuery(createSelectStatement(fields));
            Map<String, String> result = new HashMap<>();
            while (resultSet.next())
                result.put(resultSet.getString(key), resultSet.getString(value));
            return result;
        }

    }

    private static final String TABLE_PREFIX = "fepermissions_";

    private static final String TABLE_INFO = "INFO";
    private static final String TABLE_ZONE = "ZONE";
    private static final String TABLE_GROUP_PERMISSIONS = "GROUP_PERMISSION";
    private static final String TABLE_USER_PERMISSIONS = "USER_PERMISSION";
    private static final String TABLE_USER = "USER";
    // private static final String TABLE_GROUP = "GROUP";

    private static final String INFO_MAX_ZONE_ID = "max_zone_id";

    private static final String VERSION = "1.2";

    private final Map<String, TableInfo> TABLES = setupTableData();

    private Map<String, TableInfo> setupTableData()
    {
        HashMap<String, TableInfo> result = new HashMap<>();
        TableInfo tbl;

        tbl = new TableInfo(TABLE_PREFIX + "info");
        tbl.columns.put("key", "VARCHAR(64)");
        tbl.columns.put("value", "VARCHAR(64)");
        tbl.primaryKeys.add("key");
        result.put(TABLE_INFO, tbl);

        // tbl = new TableInfo(TABLE_PREFIX + "group");
        // tbl.columns.put("name", "VARCHAR(64)");
        // tbl.primaryKeys.add("name");
        // result.put(TABLE_GROUP, tbl);

        tbl = new TableInfo(TABLE_PREFIX + "user");
        tbl.columns.put("uuid", "VARCHAR(36)");
        tbl.columns.put("name", "VARCHAR(128)");
        tbl.primaryKeys.add("uuid");
        tbl.nullableKeys.add("name");
        result.put(TABLE_USER, tbl);

        tbl = new TableInfo(TABLE_PREFIX + "zone");
        tbl.columns.put("id", "INT");
        tbl.columns.put("type", "INT");
        tbl.columns.put("parent_id", "INT");
        tbl.columns.put("name", "VARCHAR(64)");
        tbl.columns.put("dimension", "INT");
        tbl.columns.put("area", "VARCHAR(64)");
        tbl.columns.put("shape", "VARCHAR(16)");
        tbl.primaryKeys.add("id");
        tbl.nullableKeys.add("parent_id");
        tbl.nullableKeys.add("name");
        tbl.nullableKeys.add("dimension");
        tbl.nullableKeys.add("area");
        tbl.nullableKeys.add("shape");
        result.put(TABLE_ZONE, tbl);

        tbl = new TableInfo(TABLE_PREFIX + "group_permission");
        tbl.columns.put("group", "VARCHAR(64)");
        tbl.columns.put("zone_id", "INT");
        tbl.columns.put("permission", "VARCHAR(255)");
        tbl.columns.put("value", "VARCHAR(1023)");
        tbl.primaryKeys.add("group");
        tbl.primaryKeys.add("zone_id");
        tbl.primaryKeys.add("permission");
        result.put(TABLE_GROUP_PERMISSIONS, tbl);

        tbl = new TableInfo(TABLE_PREFIX + "user_permission");
        tbl.columns.put("user", "VARCHAR(36)");
        tbl.columns.put("zone_id", "INT");
        tbl.columns.put("permission", "VARCHAR(255)");
        tbl.columns.put("value", "VARCHAR(1023)");
        tbl.primaryKeys.add("user");
        tbl.primaryKeys.add("zone_id");
        tbl.primaryKeys.add("permission");
        result.put(TABLE_USER_PERMISSIONS, tbl);

        return result;
    }

    // ------------------------------------------------------------

    protected Connection db;

    protected EnumDBType dbType;

    // ------------------------------------------------------------

    public SQLProvider(Connection connection, EnumDBType dbType)
    {
        this.db = connection;
        this.dbType = dbType;

        checkAndCreateTables();
        String version = getVersion();
        if (version == null)
        {
            setVersion(VERSION);
        }
        else
        {
            // check versions
            if (!VERSION.equals(version))
            {
                LoggingHandler.felog.info("Version of permission database incorrect. May not load permissions correctly!");
            }

            if (version.equals("1.0"))
            {
                TableInfo tbl = TABLES.get(TABLE_ZONE);
                executeUpdate("ALTER TABLE `" + tbl.name + "` ADD COLUMN `shape` " + tbl.columns.get("shape"));
                setVersion(VERSION);
            }
        }
    }

    private Set<String> getExistingTables() throws SQLException
    {
        ResultSet resultSet = db.getMetaData().getTables(null, null, TABLE_PREFIX + "%", null);
        Set<String> result = new HashSet<>();
        while (resultSet.next())
            result.add(resultSet.getString(3));
        return result;
    }

    private boolean checkAndCreateTables()
    {
        boolean checkOk = true;
        try
        {
            Set<String> existingTables = getExistingTables();
            Statement statement = db.createStatement();
            for (TableInfo tbl : TABLES.values())
            {
                if (existingTables.contains(tbl.name))
                    continue;
                checkOk = false;
                statement.executeUpdate(tbl.getCreateStatement());
                existingTables.add(tbl.name);
            }
        }
        catch (SQLException e)
        {
            Throwables.propagate(e);
        }
        return checkOk;
    }

    private void executeUpdate(String stmt)
    {
        try
        {
            db.createStatement().executeUpdate(stmt);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private String getVersion()
    {
        try
        {
            ResultSet result = db.createStatement().executeQuery(
                    TABLES.get(TABLE_INFO).createSelectStatement(new String[] { "value" }) + " WHERE `key` = 'version'");
            if (result.next())
                return result.getString(1);
            return null;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    private void setVersion(String version)
    {
        Map<String, Object> fieldsAndValues = new HashMap<>();
        fieldsAndValues.put("key", "version");
        fieldsAndValues.put("value", version);
        executeUpdate(TABLES.get(TABLE_INFO).createInsertOrReplace(fieldsAndValues));
    }

    @Override
    public void save(ServerZone serverZone)
    {
        try
        {
            writeUserGroupPermissions(serverZone);

            // Use a transaction to be able to rollback, if there is an error
            // db.setAutoCommit(false);

            // Truncate old data
            db.createStatement().executeUpdate(TABLES.get(TABLE_ZONE).createTruncate());
            // db.createStatement().executeUpdate(TABLES.get(TABLE_USER).createTruncate());
            // db.createStatement().executeUpdate(TABLES.get(TABLE_GROUP).createTruncate());
            db.createStatement().executeUpdate(TABLES.get(TABLE_GROUP_PERMISSIONS).createTruncate());
            db.createStatement().executeUpdate(TABLES.get(TABLE_USER_PERMISSIONS).createTruncate());

            for (UserIdent ident : serverZone.getKnownPlayers())
            {
                if (!ident.hasUuid())
                    continue;
                Map<String, Object> fieldsAndValues = new HashMap<>();
                fieldsAndValues.put("uuid", ident.getOrGenerateUuid().toString());
                if (ident.hasUsername())
                    fieldsAndValues.put("name", ident.getUsername());
                db.createStatement().executeUpdate(TABLES.get(TABLE_USER).createInsertOrReplace(fieldsAndValues));
            }

            saveServerZone(serverZone);
            saveZonePermissions(serverZone);
            for (WorldZone worldZone : serverZone.getWorldZones().values())
            {
                saveWorldZone(worldZone);
                saveZonePermissions(worldZone);
                for (AreaZone areaZone : worldZone.getAreaZones())
                {
                    saveAreaZone(areaZone);
                    saveZonePermissions(areaZone);
                }
            }
            // db.commit();
            // db.setAutoCommit(true);
        }
        catch (SQLException se)
        {
            try
            {
                db.rollback();
            }
            catch (SQLException se2)
            {
                // Ignore rollback-error
            }
            Throwables.propagate(se);
        }
    }

    private void saveZonePermissions(Zone zone)
    {
        try
        {
            for (Entry<String, PermissionList> group : zone.getGroupPermissions().entrySet())
            {
                for (Entry<String, String> perm : group.getValue().entrySet())
                {
                    Map<String, Object> fieldsAndValues = new HashMap<>();
                    fieldsAndValues.put("group", group.getKey());
                    fieldsAndValues.put("zone_id", zone.getId());
                    fieldsAndValues.put("permission", perm.getKey());
                    fieldsAndValues.put("value", perm.getValue());
                    db.createStatement().executeUpdate(TABLES.get(TABLE_GROUP_PERMISSIONS).createInsertOrReplace(fieldsAndValues));
                }
            }
            for (Entry<UserIdent, PermissionList> user : zone.getPlayerPermissions().entrySet())
            {
                for (Entry<String, String> perm : user.getValue().entrySet())
                {
                    Map<String, Object> fieldsAndValues = new HashMap<>();
                    fieldsAndValues.put("user", user.getKey().getOrGenerateUuid().toString());
                    fieldsAndValues.put("zone_id", zone.getId());
                    fieldsAndValues.put("permission", perm.getKey());
                    fieldsAndValues.put("value", perm.getValue());
                    db.createStatement().executeUpdate(TABLES.get(TABLE_USER_PERMISSIONS).createInsertOrReplace(fieldsAndValues));
                }
            }
            // PreparedStatement pstmt = db.prepareStatement("UPDATE");
            //
            // // Add rows to a batch in a loop. Each iteration adds a new row.
            // for (int i = 0; i < firstNames.length; i++)
            // {
            // // Add each parameter to the row.
            // pstmt.setInt(1, i + 1);
            // pstmt.setString(2, lastNames[i]);
            // pstmt.setString(3, firstNames[i]);
            // pstmt.setString(4, emails[i]);
            // pstmt.setString(5, phoneNumbers[i]);
            // // Add row to the batch.
            // pstmt.addBatch();
            // }
        }
        catch (SQLException e)
        {
            Throwables.propagate(e);
        }
    }

    public void saveServerZone(ServerZone zone) throws SQLException
    {
        Map<String, Object> fieldsAndValues = new HashMap<>();
        fieldsAndValues.put("id", zone.getId());
        fieldsAndValues.put("type", 0);
        fieldsAndValues.put("parent_id", 0);
        db.createStatement().executeUpdate(TABLES.get(TABLE_ZONE).createInsertOrReplace(fieldsAndValues));

        fieldsAndValues = new HashMap<>();
        fieldsAndValues.put("key", INFO_MAX_ZONE_ID);
        fieldsAndValues.put("value", zone.getMaxZoneID());
        db.createStatement().executeUpdate(TABLES.get(TABLE_INFO).createInsertOrReplace(fieldsAndValues));
    }

    private void saveWorldZone(WorldZone zone) throws SQLException
    {
        Map<String, Object> fieldsAndValues = new HashMap<>();
        fieldsAndValues.put("id", zone.getId());
        fieldsAndValues.put("type", 1);
        fieldsAndValues.put("parent_id", zone.getParent().getId());
        fieldsAndValues.put("dimension", zone.getDimensionID());
        db.createStatement().executeUpdate(TABLES.get(TABLE_ZONE).createInsertOrReplace(fieldsAndValues));
    }

    private void saveAreaZone(AreaZone zone) throws SQLException
    {
        Map<String, Object> fieldsAndValues = new HashMap<>();
        fieldsAndValues.put("id", zone.getId());
        fieldsAndValues.put("type", 2);
        fieldsAndValues.put("parent_id", zone.getParent().getId());
        fieldsAndValues.put("name", zone.getShortName());
        fieldsAndValues.put("dimension", zone.getWorldZone().getDimensionID());
        fieldsAndValues.put("area", zone.getArea().toString());
        fieldsAndValues.put("shape", zone.getShape().toString());
        db.createStatement().executeUpdate(TABLES.get(TABLE_ZONE).createInsertOrReplace(fieldsAndValues));
    }

    @Override
    public ServerZone load()
    {
        try
        {
            Map<Integer, Zone> zones = new HashMap<>();

            // Load data from SQL
            Map<String, String> infoData = TABLES.get(TABLE_INFO).loadMap("key", "value");
            List<Map<String, Object>> zonesData = TABLES.get(TABLE_ZONE).loadList();
            List<Map<String, Object>> groupPermissions = TABLES.get(TABLE_GROUP_PERMISSIONS).loadList();
            List<Map<String, Object>> userPermissions = TABLES.get(TABLE_USER_PERMISSIONS).loadList();

            // Create server-zone
            ServerZone serverZone = null;
            for (Map<String, Object> zoneData : zonesData)
                if (zoneData.get("type").equals(0))
                {
                    serverZone = new ServerZone();
                    zones.put(serverZone.getId(), serverZone);
                    break;
                }

            // Check if server-zone could be created - otherwise save was corrupt or just not present
            if (serverZone == null)
            {
                LoggingHandler.felog.error("Error loading permissions: Missing server-zone");
                db.createStatement().executeUpdate(TABLES.get(TABLE_ZONE).createTruncate());
                return null;
            }

            // Create world-zones
            for (Map<String, Object> zoneData : zonesData)
                if (zoneData.get("type").equals(1))
                {
                    WorldZone zone = new WorldZone(serverZone, (Integer) zoneData.get("dimension"), (Integer) zoneData.get("id"));
                    zones.put(zone.getId(), zone);
                }

            // Create area-zones
            for (Map<String, Object> zoneData : zonesData)
                if (zoneData.get("type").equals(2))
                {
                    WorldZone parentZone = (WorldZone) zones.get(zoneData.get("parent_id"));
                    if (parentZone != null)
                    {
                        AreaBase area = AreaBase.fromString((String) zoneData.get("area"));
                        if (area != null)
                        {
                            AreaZone zone = new AreaZone(parentZone, (String) zoneData.get("name"), area, (Integer) zoneData.get("id"));
                            AreaShape shape = AreaShape.getByName((String) zoneData.get("shape"));
                            if (shape != null)
                                zone.setShape(shape);
                            zones.put(zone.getId(), zone);
                        }
                    }
                }

            // Apply group permissions
            for (Map<String, Object> permData : groupPermissions)
            {
                Zone zone = zones.get(permData.get("zone_id"));
                if (zone != null)
                {
                    zone.setGroupPermissionProperty((String) permData.get("group"), (String) permData.get("permission"), (String) permData.get("value"));
                }
            }

            // Apply user permissions
            for (Map<String, Object> permData : userPermissions)
            {
                Zone zone = zones.get(permData.get("zone_id"));
                if (zone != null)
                {
                    zone.setPlayerPermissionProperty(UserIdent.get((String) permData.get("user")), (String) permData.get("permission"),
                            (String) permData.get("value"));
                }
            }

            // Load maxZoneId
            try
            {
                serverZone.setMaxZoneId(Integer.parseInt(infoData.get(INFO_MAX_ZONE_ID)));
            }
            catch (NumberFormatException e)
            {
            }

            // Make sure maxZoneId is valid
            for (Zone zone : zones.values())
                if (zone.getId() > serverZone.getMaxZoneID())
                    serverZone.setMaxZoneId(zone.getId());

            // Add user to groups by fe.internal.player.groups permission
            for (UserIdent ident : serverZone.getPlayerPermissions().keySet())
            {
                String groupList = serverZone.getPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
                serverZone.clearPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
                if (groupList == null)
                    continue;
                String[] groups = groupList.split(",");
                for (String group : groups)
                {
                    serverZone.addPlayerToGroup(ident, group);
                }
            }

            return serverZone;
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error("Error loading permissions");
            e.printStackTrace();
        }
        return null;
    }

}
