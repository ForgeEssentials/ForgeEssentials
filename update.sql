-- this Update script for ForgeEssentials
-- this script updates from ForgeEssentials beta build 1.2.0.398 to 1.2.0.399

-- this script will add a new table that is used with a new feature in ForgeEssentials
CREATE TABLE fepermissions_permProps
 (
 target INTEGER NOT NULL,
 isgroup BOOLEAN NOT NULL,
 perm VARCHAR(255) NOT NULL,
 property VARCHAR(255) NOT NULL,
 zoneID INTEGER NOT NULL
 )

 
-- this script updates from ForgeEssentials beta build 1.1.0.82 to 1.1.0.83
-- this script updates from ForgeEssentials 1.0.0.245 to 1.1.0.246

-- this script will alter the old tables to match the new table format for the Permissions.
ALTER ALTER TABLE zones
RENAME TO fepermissions_zones;

ALTER ALTER TABLE groups
RENAME TO fepermissions_groups;

ALTER ALTER TABLE groupConnectors
RENAME TO fepermissions_groupConnectors;

ALTER ALTER TABLE ladders
RENAME TO fepermissions_ladders;

ALTER ALTER TABLE ladderNames
RENAME TO fepermissions_ladderNames;

ALTER ALTER TABLE players
RENAME TO fepermissions_players;

ALTER ALTER TABLE permissions
RENAME TO fepermissions_permissions;
